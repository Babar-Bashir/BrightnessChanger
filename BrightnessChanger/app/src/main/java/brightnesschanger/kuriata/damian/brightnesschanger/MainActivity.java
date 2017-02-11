package brightnesschanger.kuriata.damian.brightnesschanger;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.provider.Settings;
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
import android.widget.SeekBar;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.Integer;
import java.io.FileReader;;

import brightnesschanger.kuriata.damian.brightnesschanger.ScreenStateReceiver;
import brightnesschanger.kuriata.damian.brightnesschanger.BrightnessWriter;
import brightnesschanger.kuriata.damian.brightnesschanger.SetScreenBrightness;

public class MainActivity extends AppCompatActivity {

    private ScreenStateReceiver screenStateReceiver;
    private SeekBar brightnessSeekBar;
    private EditText brightnessEditText;
    private int maxBrightnessValue;
    private static int seekBarValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(!BrightnessWriter.getRootAccess()) {
            Toast.makeText(this, R.string.cannot_get_root_access, Toast.LENGTH_LONG).show();
            // working without root makes no sense
            System.exit(1);
        }
        maxBrightnessValue = getMaxBrightnessValue();

        brightnessEditText = (EditText) findViewById(R.id.brightness_input_edtext);

        setUpBrightnessSeekBar();
        setButtonsOnClickAction();

        registerScreenStateReceiver();

        /*int brightnessValueAfterApplicationCreation =
                15;

        setBrightnessSeekBarValue(brightnessValueAfterApplicationCreation);
        seekBarValue = brightnessValueAfterApplicationCreation;
        setEditTextValue(brightnessValueAfterApplicationCreation);*/

    }

    /*private int getScreenBrightnessAfterApplicationCreation() {
        char [] buffer = new char[3];
        try {
            // reading from file in root filesystem does not require root permission
            FileReader reader = new FileReader("/sys/class/leds/wled/brightness");
            int tmp = reader.read(buffer);
            reader.close();
        }catch(IOException e) {
            buffer = "10".toCharArray();
        }
        System.out.println(Integer.parseInt(new String(buffer)));
        return Integer.parseInt(new String(buffer));
    }*/

    private void registerScreenStateReceiver() {
        screenStateReceiver = new ScreenStateReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenStateReceiver, filter);
    }

    private void setUpBrightnessSeekBar() {
        brightnessSeekBar = (SeekBar) findViewById(R.id.brightness_seek_bar);
        brightnessSeekBar.setMax(maxBrightnessValue);
        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if(b) {
                    if (i == 0) {
                        seekBarValue = 1;
                    }
                    else {
                        seekBarValue = i;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBarValue = brightnessSeekBar.getProgress();
                if(seekBarValue == 0) {
                    seekBarValue = 1;
                }
                setEditTextValue(seekBarValue);
                BrightnessWriter.writeBrightness(seekBarValue);
            }
        });
    }

    private void setEditTextValue(int value) {
        brightnessEditText.setText(Integer.toString(value));
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

    private int getMaxBrightnessValue() {
        String maxBrightnessFilename = "/sys/class/leds/wled/max_brightness";
        int brightnessValue;
        try {
            FileReader reader = new FileReader(maxBrightnessFilename);
            char [] valueChars = new char [5];
            try {
                int tmp = reader.read(valueChars);
                String valueString = new String(valueChars).trim();
                brightnessValue = Integer.parseInt(valueString);
                reader.close();
            }catch(IOException f) {
                brightnessValue = -1;
            }

        }catch(FileNotFoundException e) {
            brightnessValue = -1;
        }
        return brightnessValue;
    }

    private void setButtonsOnClickAction() {
        Button changeButton = (Button) findViewById(R.id.change_btn);
        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBarValue = getBrightnessValueFromEditText();
                BrightnessWriter.writeBrightness(seekBarValue);
                setBrightnessSeekBarValue(seekBarValue);
            }
        });

        Button setToDefaultButton = (Button) findViewById(R.id.default_btn);
        setToDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seekBarValue = getDefaultBrightnessValue();
                BrightnessWriter.writeBrightness(seekBarValue);
                setBrightnessSeekBarValue(seekBarValue);
                setEditTextValue(seekBarValue);
            }
        });
    }

    private int getDefaultBrightnessValue() {
        return maxBrightnessValue/2;

    }
    private void setBrightnessSeekBarValue(int value) {
        brightnessSeekBar.setProgress(value);
    }

    private int getBrightnessValueFromEditText() {
        String brightnessValueStr = brightnessEditText.getText().toString();
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

    public static int getCurrentBrightnessValue() {
        return seekBarValue;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BrightnessWriter.freeResources();
        unregisterReceiver(screenStateReceiver);
    }

}
