package brightnesschanger.kuriata.damian.brightnesschanger;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;

import brightnesschanger.kuriata.damian.brightnesschanger.BrightnessWriter;
import brightnesschanger.kuriata.damian.brightnesschanger.MainActivity;

public class ScreenStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_SCREEN_ON)) {
            BrightnessWriter.writeBrightness(MainActivity.getCurrentBrightnessValue());
        }
    }
}
