package brightnesschanger.kuriata.damian.brightnesschanger;

import android.app.IntentService;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;

import brightnesschanger.kuriata.damian.brightnesschanger.ScreenStateReceiver;
import brightnesschanger.kuriata.damian.brightnesschanger.Actions;

public class ScreenBrightnessService extends IntentService {
    private ScreenStateReceiver screenStateReceiver;
    private static final int NOTIFICATION_ID = 1;

    public ScreenBrightnessService() {
        super("ScreenBrightnessChanger");
    }
    @Override
    public void onCreate() {
        super.onCreate();
        screenStateReceiver = new ScreenStateReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        registerReceiver(screenStateReceiver, filter);
        System.out.println("On create");
        createAndStartForegroundService();
    }

    private void createAndStartForegroundService() {
        Intent notificationIntent = new Intent(this, CloseApplicationBroadcastReceiver.class);
        notificationIntent.setAction(Actions.ACTION_CLOSE_APPLICATION);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 123, notificationIntent, 0);
        Notification applicationNotification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.notification_content_title))
                .setContentText(getString(R.string.notification_content_text))
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                .addAction(android.R.drawable.btn_default_small, getString(R.string.close_action_name), pendingIntent)
                .setContentIntent(pendingIntent)
                .build();

        startForeground(NOTIFICATION_ID, applicationNotification);
    }

    @Override
    public void onHandleIntent(Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        System.out.println("On destroy");
        stopForeground(true);
        unregisterReceiver(screenStateReceiver);
    }

}
