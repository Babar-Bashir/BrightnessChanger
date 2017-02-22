package brightnesschanger.kuriata.damian.brightnesschanger;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.widget.Toast;

import brightnesschanger.kuriata.damian.brightnesschanger.BrightnessWriter;
import brightnesschanger.kuriata.damian.brightnesschanger.MainActivity;
import brightnesschanger.kuriata.damian.brightnesschanger.BrightnessValueContainer;

public class ScreenStateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_SCREEN_ON)) {
            if(!BrightnessWriter.writeBrightness(BrightnessValueContainer.brightnessValue)) {
                Toast.makeText(context, "Cannot change brightness, have you granted su permission?", Toast.LENGTH_SHORT).show();
                System.out.println("ROOT ACCESS DENIED!");
            }
        }
    }
}
