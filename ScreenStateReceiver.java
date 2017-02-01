package brightnesschanger.kuriata.damian.brightnesschanger;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.widget.Toast;

import brightnesschanger.kuriata.damian.brightnesschanger.MainActivity;

public class ScreenStateReceiver extends BroadcastReceiver {
    public boolean screenIsOn = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if(action.equals(Intent.ACTION_SCREEN_ON)) {
            screenIsOn = true;
        }
        else if(action.equals(Intent.ACTION_SCREEN_OFF)) {
            screenIsOn = false;
        }
    }
}
