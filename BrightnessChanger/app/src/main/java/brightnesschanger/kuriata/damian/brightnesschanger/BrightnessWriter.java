package brightnesschanger.kuriata.damian.brightnesschanger;

import android.widget.Toast;

import java.io.IOException;
import java.lang.ProcessBuilder;
import java.lang.Process;
import java.io.OutputStream;

public class BrightnessWriter {
    private static Process rootProcess;
    private static OutputStream outputToFile;

    public static boolean getRootAccess() {
        try {
            rootProcess = new ProcessBuilder().command("su").start();
            return true;
        }catch(IOException e) {
            return false;
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
