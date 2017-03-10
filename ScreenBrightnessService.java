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
    /** This class extends IntentService class and it's responsible for handling screen brightness
     * This service starts when application starts and stops when application menu is opened up
     */
    private ApplicationBroadcastReceiver screenStateReceiver;
    private static final int NOTIFICATION_ID = 1;

    public ScreenBrightnessService() {
        super("ScreenBrightnessChanger");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        screenStateReceiver = new ApplicationBroadcastReceiver();
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        //filter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenStateReceiver, filter);
        System.out.println("On create");
        createAndStartForegroundService();
    }

    private void createAndStartForegroundService() {
        Intent closeAppNotificationIntent = new Intent(this, ApplicationBroadcastReceiver.class);
        closeAppNotificationIntent.setAction(Actions.ACTION_CLOSE_APPLICATION);
        Intent reopenMainActivityNotificationIntent = new Intent(this, ApplicationBroadcastReceiver.class);
        reopenMainActivityNotificationIntent.setAction(Actions.ACTION_REOPEN_MAIN_ACTIVITY);

        startForeground(NOTIFICATION_ID, createNotification(closeAppNotificationIntent, reopenMainActivityNotificationIntent));
    }

    private Notification createNotification(Intent closeAppNotificationIntent, Intent reopenMainActivityNotificationIntent) {
        PendingIntent closeMainActivityPendingIntent = PendingIntent.getBroadcast(this, 123, closeAppNotificationIntent, 0);
        PendingIntent reopenMainActivityPendingIntent = PendingIntent.getBroadcast(this, 124, reopenMainActivityNotificationIntent, 0);

        Notification applicationNotification = new Notification.Builder(this)
                .setContentTitle(getString(R.string.notification_content_title))
                .setContentText(getString(R.string.notification_content_text))
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                .addAction(android.R.drawable.btn_default_small, getString(R.string.close_action_name), closeMainActivityPendingIntent)
                .addAction(android.R.drawable.btn_default_small, getString(R.string.return_to_app), reopenMainActivityPendingIntent)
                .setContentIntent(closeMainActivityPendingIntent)
                .setContentIntent(reopenMainActivityPendingIntent)
                .build();

        return applicationNotification;

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
