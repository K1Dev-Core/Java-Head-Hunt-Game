import javax.sound.sampled.*;
import java.io.File;

public class SoundManager {
    
    public static void playSound(String filePath) {
        new Thread(() -> {
            try {
                File soundFile = new File(filePath);
                AudioInputStream audioStream = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(audioStream);
                clip.start();
            } catch (Exception e) {
                System.err.println("Cannot play sound: " + filePath);
            }
        }).start();
    }
}
