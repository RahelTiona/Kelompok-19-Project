package main;

import javax.swing.*;
import gui.GameDashboard;
import java.io.File;

public class FlappyBirdGame {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        verifyAssets();
        SwingUtilities.invokeLater(() -> {
            GameDashboard.createAndShowGameMenu();
        });
    }

    private static void verifyAssets() {
        String[] directories = {
            "src/assets",
            "src/assets/image",
            "src/assets/sound"
        };

        for (String dir : directories) {
            File directory = new File(dir);
            if (!directory.exists()) {
                directory.mkdirs();
            }
        }

        String[] requiredImages = {
            "background.png",
            "flappybirdbg.png",
            "yellowbird.png",
            "toppipe.png",
            "bottompipe.png"
        };

        for (String image : requiredImages) {
            File imageFile = new File("src/assets/image/" + image);
            if (!imageFile.exists()) {
                System.err.println("Warning: Required image file missing: " + image);
                System.err.println("Please ensure all required assets are in place.");
            }
        }

        String[] requiredSounds = {
            "point.wav",
            "die.wav",
            "wing.wav"
        };

        for (String sound : requiredSounds) {
            File soundFile = new File("src/assets/sound/" + sound);
            if (!soundFile.exists()) {
                System.err.println("Warning: Required sound file missing: " + sound);
                System.err.println("Please ensure all required assets are in place.");
            }
        }
    }
}