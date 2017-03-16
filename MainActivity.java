package brightnesschanger.kuriata.damian.brightnesschanger;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuInflater;
import android.widget.SeekBar;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileReader;
import java.util.Timer;
import java.util.TimerTask;

import brightnesschanger.kuriata.damian.brightnesschanger.ScreenStateReceiver;
import brightnesschanger.kuriata.damian.brightnesschanger.BrightnessWriter;
import brightnesschanger.kuriata.damian.brightnesschanger.ScreenBrightnessService;
import brightnesschanger.kuriata.damian.brightnesschanger.BrightnessValueContainer;
import brightnesschanger.kuriata.damian.brightnesschanger.Actions;

public class MainActivity extends AppCompatActivity {
    private ScreenBrightnessService screenBrightnessService;
    private SeekBar brightnessSeekBar;
    private EditText brightnessEditText;
    //private Context applicationContext;
    private short maxBrightnessValue;
    private static boolean settingsMenuClickedPreviously;
    private SettingsFragment settingsFragment;
    private short timerTimeLimit;
    private boolean timerShouldBeStopped = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BrightnessWriter.getRootAccess();
        System.out.println("STARTING APPLICATION");

        maxBrightnessValue = getMaxBrightnessValue();

        brightnessEditText = (EditText) findViewById(R.id.brightness_input_edtext);

        try {
            BrightnessValueContainer.brightnessValue =
                    (short) Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        }catch(Settings.SettingNotFoundException e) {
            BrightnessValueContainer.brightnessValue = 30;
        }

        brightnessEditText.setText(Short.toString(BrightnessValueContainer.brightnessValue));
        createAndSetupScreenBrightnessService();
        setUpBrightnessSeekBar();
        setButtonsOnClickAction();

        settingsFragment = new SettingsFragment();
        settingsMenuClickedPreviously = false;
    }

    private void createAndSetupScreenBrightnessService() {
        screenBrightnessService = new ScreenBrightnessService();
        Intent serviceIntent = new Intent(this, ScreenBrightnessService.class);
        startService(serviceIntent);
    }

    private void setUpBrightnessSeekBar() {
        brightnessSeekBar = (SeekBar) findViewById(R.id.brightness_seek_bar);
        brightnessSeekBar.setMax(maxBrightnessValue);
        brightnessSeekBar.setProgress(BrightnessValueContainer.brightnessValue);
        final Context applicationContext = getApplicationContext();

        brightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    if (i == 0) {
                        BrightnessValueContainer.brightnessValue = 1;
                    } else {
                        BrightnessValueContainer.brightnessValue = (short) i;
                    }
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                BrightnessValueContainer.brightnessValue = (short) brightnessSeekBar.getProgress();
                if (BrightnessValueContainer.brightnessValue == 0) {
                    BrightnessValueContainer.brightnessValue = 1;
                }
                setEditTextValue(BrightnessValueContainer.brightnessValue);
                BrightnessWriter.writeBrightness(BrightnessValueContainer.brightnessValue, applicationContext);
            }
        });
    }

    @Override
    public void onNewIntent(Intent intent) {
        if(intent.getAction().equals(Actions.ACTION_CLOSE_APPLICATION)) {
            closeApplication();
        }
        else if(intent.getAction().equals(Actions.ACTION_CLOSE_APPLICATION_WITH_CHECKING_SETTINGS)) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            if(preferences.getBoolean(getString(R.string.automatically_close_app_preference_key), false)) {
                System.out.println("CLOSINT APP");
                closeApplication();
            }
            else {
                timerTimeLimit = getTimeToTerminateApplication();
                Timer timer = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        if(timerTimeLimit != 0) {
                            timerTimeLimit--;
                        }
                        else {
                            cancel();
                            closeApplication();
                        }
                    }
                };
                timer.schedule(task, 0, 1000 * 60/*express time in one second*/);
            }
        }

    }

    private short getTimeToTerminateApplication() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return Short.parseShort(prefs.getString(getString(R.string.timer_preference_key), "2"));
    }

    private void setEditTextValue(int value) {
        brightnessEditText.setText(Short.toString((short) value));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        super.onOptionsItemSelected(item);
        switch(item.getItemId()) {
            case R.id.about:
                createAndShowAboutDialog();
            break;
            case R.id.settings:
                if(!settingsMenuClickedPreviously) {
                    stopService(new Intent(this, ScreenBrightnessService.class));
                    getFragmentManager().beginTransaction()
                            .replace(android.R.id.content, settingsFragment)
                            .commit();
                    settingsMenuClickedPreviously = true;
                }
                else {
                    startService(new Intent(this, ScreenBrightnessService.class));
                    getFragmentManager().beginTransaction()
                            .remove(settingsFragment)
                            .commit();
                    settingsMenuClickedPreviously = false;
                }

            break;
        }
        return true;
    }

    private void createAndShowAboutDialog() {
        AlertDialog.Builder aboutDialog = new AlertDialog.Builder(this);
        aboutDialog.setTitle(R.string.about_dialog_title);
        aboutDialog.setMessage(getString(R.string.about_dialog_content) + getString(R.string.app_version));
        aboutDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // do nothing
            }
        });
        aboutDialog.show();
    }

    private short getMaxBrightnessValue() {
        String maxBrightnessFilename = "/sys/class/leds/wled/max_brightness";
        short brightnessValue;
        try {
            FileReader reader = new FileReader(maxBrightnessFilename);
            char [] valueChars = new char [5];
            try {
                int tmp = reader.read(valueChars);
                String valueString = new String(valueChars).trim();
                brightnessValue = Short.parseShort(valueString);
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
        final Context applicationContext = getApplicationContext();

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BrightnessValueContainer.brightnessValue = getBrightnessValueFromEditText();
                BrightnessWriter.writeBrightness(BrightnessValueContainer.brightnessValue, applicationContext);
                setBrightnessSeekBarValue(BrightnessValueContainer.brightnessValue);
            }
        });

        Button setToDefaultButton = (Button) findViewById(R.id.default_btn);
        setToDefaultButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BrightnessValueContainer.brightnessValue = getDefaultBrightnessValue();
                BrightnessWriter.writeBrightness(BrightnessValueContainer.brightnessValue, applicationContext);
                setBrightnessSeekBarValue(BrightnessValueContainer.brightnessValue);
                setEditTextValue(BrightnessValueContainer.brightnessValue);
            }
        });

        Button exitFromApplicationButton = (Button) findViewById(R.id.exit_btn);
        exitFromApplicationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeApplication();
            }
        });
    }

    private short getDefaultBrightnessValue() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        short brightnessValue = Short.parseShort(preferences.getString(getString(R.string.default_brightness_value_edit_text_preference_key), "8"));
        System.out.println(brightnessValue);
        return brightnessValue;
    }

    private boolean getCloseAppAutomaticallySetting() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        return preferences.getBoolean(getString(R.string.automatically_close_app_preference_key), false);
    }

    private void setBrightnessSeekBarValue(int value) {
        brightnessSeekBar.setProgress(value);
    }

    private short getBrightnessValueFromEditText() {
        String brightnessValueStr = brightnessEditText.getText().toString();
        short brightnessValue;
        //if user entered nothing
        if(brightnessValueStr.matches("")) {
            brightnessValue = -1;
        }
        else {
            brightnessValue = Short.parseShort(brightnessValueStr);
        }
        return brightnessValue;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void closeApplication() {
        stopService(new Intent(this, ScreenBrightnessService.class));
        System.out.println("CLOSING APPLICATION");
        finishAffinity();
    }

}
