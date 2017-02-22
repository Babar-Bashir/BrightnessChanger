package brightnesschanger.kuriata.damian.brightnesschanger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import brightnesschanger.kuriata.damian.brightnesschanger.Actions;
import brightnesschanger.kuriata.damian.brightnesschanger.MainActivity;

public class CloseApplicationBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Actions.ACTION_CLOSE_APPLICATION)){
            Intent exitIntent = new Intent(context, MainActivity.class);
            exitIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //.FLAG_ACTIVITY_NEW_TASK/CLEAR_TOP
            exitIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            exitIntent.setAction(Actions.ACTION_CLOSE_APPLICATION);
            context.startActivity(exitIntent);
        }
    }
}
