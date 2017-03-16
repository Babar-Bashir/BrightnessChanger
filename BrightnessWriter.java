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
    private static byte toastShowsCounter = 0;

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
            if(toastShowsCounter < 5) {
                Toast.makeText(context, R.string.could_not_change_the_brightness, Toast.LENGTH_SHORT).show();
                toastShowsCounter++;
            }
        }
    }

    public static void freeResources() {
            try {
                outputToFile.close();
            } catch (IOException e) {
                // assume this exception never will be thrown
            }
            rootProcess.destroy();
    }
}
