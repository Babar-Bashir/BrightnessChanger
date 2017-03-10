package brightnesschanger.kuriata.damian.brightnesschanger;

import android.content.Context;
import android.widget.Toast;

import java.io.IOException;
import java.lang.ProcessBuilder;
import java.lang.Process;
import java.io.OutputStream;

public class BrightnessWriter {
    private static Process rootProcess;
    private static OutputStream outputToFile;

    public static void getRootAccess() {
            try {
                rootProcess = new ProcessBuilder().command("su").start();
            } catch (IOException e) {

            }
    }

    public static void writeBrightness(int brightnessValue, Context context) {
        String targetFilename = "/sys/class/leds/wled/brightness";
        String commandToExecute = "echo " + Integer.toString(brightnessValue) + " > " + targetFilename;
        try {
            outputToFile = rootProcess.getOutputStream();
            outputToFile.write((commandToExecute + "\n").getBytes());
            outputToFile.flush();
        }catch(IOException e) {
            Toast.makeText(context, R.string.could_not_change_the_brightness, Toast.LENGTH_SHORT).show();
        }
    }

    public static void freeResources() {
            try {
                outputToFile.close();
            } catch (IOException e) {
                System.exit(0);
            }
            rootProcess.destroy();
    }
}
