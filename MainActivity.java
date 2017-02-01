package brightnesschanger.kuriata.damian.brightnesschanger;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;

import java.io.FileNotFoundException;
import java.lang.Process;
import java.lang.Runtime;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.Integer;
import java.io.FileReader;

import brightnesschanger.kuriata.damian.brightnesschanger.ScreenStateReceiver;

public class MainActivity extends AppCompatActivity {

    private Process rootProcess = null;
    private OutputStream outputToFile = null;
    private int brightnessValue;
    private ScreenStateReceiver screenStateReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getRootAccess();
        connectWithProcessOutputStream();
        setButtonsOnClickAction();
        registerScreenStateReceiver();
    }
    private void registerScreenStateReceiver() {
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        screenStateReceiver= new ScreenStateReceiver();
        registerReceiver(screenStateReceiver, filter);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case R.id.about:
                createAndShowAboutDialog();
            break;
            case R.id.settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            break;
        }
        return true;
    }
    private void createAndShowAboutDialog() {
        AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
        aboutDialog.setTitle(R.string.about_dialog_title);
        aboutDialog.setMessage(R.string.about_dialog_content);
        aboutDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });
        aboutDialog.show();
    }
    private void getRootAccess() {
        try {
            rootProcess = Runtime.getRuntime().exec("su");
        }catch(IOException e) {
            Toast toast = Toast.makeText(this, R.string.cannot_get_root_access,Toast.LENGTH_LONG);
            toast.show();
            System.exit(1);
        }
    }
    private void connectWithProcessOutputStream() {
        outputToFile = rootProcess.getOutputStream();
    }
    private void setButtonsOnClickAction() {
        Button changeButton = (Button) findViewById(R.id.change_btn);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBrightnessValue(false);
            }
        });

        Button setToDefaultButton = (Button) findViewById(R.id.default_btn);
        setToDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setBrightnessValue(true);
            }
        });
    }
    private void setBrightnessValue(final boolean setToDefault) {
        if(setToDefault) {
           // brightnessValue = getDefaultBrightnessValue();
            brightnessValue = getDefaultBrightnessValue();
        }
        else {
            brightnessValue = getBrightnessValueFromEditText();
            if (brightnessValue < 1) {
                Toast message = Toast.makeText(this, R.string.you_entered_a_wrong_value, Toast.LENGTH_LONG);
                message.show();
                //exit from function
                return;
            }
        }
        writeBrightnessValueToFile(brightnessValue);
    }
    private int getDefaultBrightnessValue() {
        String filename = "/sys/class/leds/wled/max_brightness";
        try {
            FileReader reader = new FileReader(filename);
            char [] valueChars = new char [5];
            try {
                int tmp = reader.read(valueChars);
                String valueString = new String(valueChars).trim();
                brightnessValue = Integer.parseInt(valueString) / 2;
                reader.close();
            }catch(IOException f) {
                brightnessValue = -1;
            }

        }catch(FileNotFoundException e) {
            brightnessValue = -1;
        }
        return brightnessValue;
    }
    private int getBrightnessValueFromEditText() {
        EditText brightnessInput = (EditText) findViewById(R.id.brightness_input_edtext);
        String brightnessValueStr = brightnessInput.getText().toString();
        int brightnessValue;
        //if user entered nothing
        if(brightnessValueStr.matches("")) {
            brightnessValue = -1;
        }
        else {
            brightnessValue = Integer.parseInt(brightnessValueStr);
        }
        return brightnessValue;
    }
    private void writeBrightnessAfterScreenOf() {
        System.out.println(this.brightnessValue);
        if(brightnessValue != 0) {
            writeBrightnessValueToFile(this.brightnessValue);
        }
    }
    private void writeBrightnessValueToFile(int brightnessValue) {
        String targetFilename = "/sys/class/leds/wled/brightness";
        String commandToExecute = "echo " + Integer.toString(brightnessValue) + " > " + targetFilename;
        try {
            OutputStream outputToFile = rootProcess.getOutputStream();
            outputToFile.write((commandToExecute + "\n").getBytes());
            outputToFile.flush();
        }catch(IOException e) {
            Toast message = Toast.makeText(this, R.string.could_not_change_the_brightness, Toast.LENGTH_LONG);
            message.show();
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if(screenStateReceiver.screenIsOn) {
            writeBrightnessAfterScreenOf();
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        rootProcess.destroy();
        try {
            outputToFile.close();
        }catch(IOException e) {
            // do nothing
        }
        if(screenStateReceiver != null) {
            unregisterReceiver(screenStateReceiver);
        }
    }
}
