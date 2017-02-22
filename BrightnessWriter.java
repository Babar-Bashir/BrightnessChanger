package brightnesschanger.kuriata.damian.brightnesschanger;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ProcessBuilder;
import java.lang.Process;
import java.io.OutputStream;

import brightnesschanger.kuriata.damian.brightnesschanger.CloseApplicationBroadcastReceiver;
import brightnesschanger.kuriata.damian.brightnesschanger.MainActivity;

public class BrightnessWriter {
    private static Process rootProcess;
    private static OutputStream outputToFile;

    public static void getRootAccess(Context applicationContext) {
        try {
            rootProcess = new ProcessBuilder().command("su").start();
        }catch(IOException e) {

        }
    }

    public static boolean writeBrightness(int brightnessValue) {
        String targetFilename = "/sys/class/leds/wled/brightness";
        String commandToExecute = "echo " + Integer.toString(brightnessValue) + " > " + targetFilename;
        try {
            outputToFile = rootProcess.getOutputStream();
            outputToFile.write((commandToExecute + "\n").getBytes());
            outputToFile.flush();
            return true;
        }catch(IOException e) {
            return false;
        }
    }

    public static void freeResources() {
        try {
            outputToFile.close();
        }catch(IOException e) {
            ;
        }
        rootProcess.destroy();
    }
}
