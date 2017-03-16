package brightnesschanger.kuriata.damian.brightnesschanger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ApplicationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Actions.ACTION_CLOSE_APPLICATION)){
            Intent exitIntent = new Intent(context, MainActivity.class);
            exitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            exitIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            exitIntent.setAction(Actions.ACTION_CLOSE_APPLICATION);
            context.startActivity(exitIntent);
        }
        else if(intent.getAction().equals(Actions.ACTION_REOPEN_MAIN_ACTIVITY)) {
            Intent reopenIntent = new Intent(context, MainActivity.class);
            reopenIntent.setAction(Intent.ACTION_MAIN);
            reopenIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            reopenIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(reopenIntent);
        }
        else if(intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            BrightnessWriter.writeBrightness(BrightnessValueContainer.brightnessValue, context.getApplicationContext());
        }
        else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Intent exitIntent = new Intent(context, MainActivity.class);
            exitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            exitIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            exitIntent.setAction(Actions.ACTION_CLOSE_APPLICATION_WITH_CHECKING_SETTINGS);
            context.startActivity(exitIntent);
        }
    }
}
