package brightnesschanger.kuriata.damian.brightnesschanger;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.widget.Toast;

import brightnesschanger.kuriata.damian.brightnesschanger.BrightnessWriter;
import brightnesschanger.kuriata.damian.brightnesschanger.MainActivity;
import brightnesschanger.kuriata.damian.brightnesschanger.BrightnessValueContainer;
import brightnesschanger.kuriata.damian.brightnesschanger.TimerService;
import brightnesschanger.kuriata.damian.brightnesschanger.BrightnessWriter;

public class ScreenStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_SCREEN_ON)) {
            BrightnessWriter.writeBrightness(BrightnessValueContainer.brightnessValue, context.getApplicationContext());
        }
    }
}
