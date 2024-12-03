package game;

import java.io.*;
import javax.sound.sampled.*;

public class SoundPlayer {

    /**
     * Memutar suara dari file yang berada di folder assets/sound.
     * @param soundFileName Nama file suara (contoh: "point.wav")
     */
    public static void playSound(String soundFileName) {
        try {
            // Mendapatkan lokasi file suara dari resource
            InputStream audioSrc = SoundPlayer.class.getResourceAsStream("/assets/sound/" + soundFileName);
            InputStream bufferedIn = new BufferedInputStream(audioSrc);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedIn);

            // Menyiapkan clip untuk memutar suara
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Gagal memutar suara: " + soundFileName);
        }
    }
}

