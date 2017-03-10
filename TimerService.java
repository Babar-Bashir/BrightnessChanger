package brightnesschanger.kuriata.damian.brightnesschanger;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.preference.PreferenceManager;

import brightnesschanger.kuriata.damian.brightnesschanger.ApplicationBroadcastReceiver;

public class TimerService extends IntentService {

    private static boolean started = false;
    private SharedPreferences sharedPreferences;
  //  private boolean serviceDestroyed;
    private ApplicationBroadcastReceiver receiver;

    public static boolean isStarted() {
        return started;
    }

    public TimerService() {
        super("TimerService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        registerReceiver(receiver, new IntentFilter(Actions.ACTION_CLOSE_APPLICATION));
        started = true;
        System.out.println("CREATE");
        //serviceDestroyed = false;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        if(getAutomaticallyCloseAppCheckboxStateFromSharedPreferences()) {
            stopSelf();
            System.out.println("Stopping");
        }

        int timeout = getServiceTimeoutFromSharedPreferences();
        System.out.println(timeout);
        new CountDownTimer(timeout*1000*10, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                System.out.println("ON TICK");
            }
            @Override
            public void onFinish() {
                    Context context = TimerService.this;
                    Intent exitIntent = new Intent(context, MainActivity.class);
                    exitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    exitIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    exitIntent.setAction(Actions.ACTION_CLOSE_APPLICATION);
                    System.out.println("RECEIVER");
                    started = false;
                    context.startActivity(exitIntent);
            }
        }.start();

    }

    private int getServiceTimeoutFromSharedPreferences() {
        return Integer.parseInt(sharedPreferences.getString(getString(R.string.timer_preference_key), "1"));
    }

    private boolean getAutomaticallyCloseAppCheckboxStateFromSharedPreferences() {
        return sharedPreferences.getBoolean(getString(R.string.automatically_close_app_preference_key), true);
    }
    @Override
    public void onHandleIntent(Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startID) {

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        System.out.println("TIMER STOPPED");
        started = false;
       // serviceDestroyed = true;
    }

}
