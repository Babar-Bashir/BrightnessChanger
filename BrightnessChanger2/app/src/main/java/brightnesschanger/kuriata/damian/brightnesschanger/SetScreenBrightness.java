package brightnesschanger.kuriata.damian.brightnesschanger;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;

import brightnesschanger.kuriata.damian.brightnesschanger.ScreenStateReceiver;

public class SetScreenBrightness extends IntentService {
    ScreenStateReceiver screenStateReceiver;

    public SetScreenBrightness() {
        super("SetScreenBrightness");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        screenStateReceiver = new ScreenStateReceiver();
        registerReceiver(screenStateReceiver, new IntentFilter(Intent.ACTION_SCREEN_ON));
        System.out.println("STARTED!");
    }

    /*@Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        screenStateReceiver = new ScreenStateReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenStateReceiver, intentFilter);

        return super.onStartCommand(intent, flags, startId);
    }*/

    /*@Override
    public void onDestroy() {
        unregisterReceiver(screenStateReceiver);
        System.out.println("DESTROY");
    }*/

}
