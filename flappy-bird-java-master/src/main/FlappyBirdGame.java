// package main;

// import java.awt.Dimension;
// import javax.swing.JFrame;
// import javax.swing.SwingUtilities;
// import gui.BorderLayout;

// public class FlappyBirdGame extends JFrame {
//     private static final int WINDOW_WIDTH = 800;
//     private static final int WINDOW_HEIGHT = 600;

//     public FlappyBirdGame() {
//         setTitle("Flappy Bird");
//         setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//         setPreferredSize(new Dimension(WINDOW_WIDTH, WINDOW_HEIGHT));
//         setResizable(false);

//         setLayout(new BorderLayout());
//         add(new game.GamePanel(), BorderLayout.CENTER);

//         pack();
//         setLocationRelativeTo(null);
//         setVisible(true);
//     }

//     public static void main(String[] args) {
//         SwingUtilities.invokeLater(() -> new FlappyBirdGame());
//     }
// }



package main;

import javax.swing.*;

import game.GameDashboard;

import java.awt.*;
import java.io.File;

public class FlappyBirdGame {
    public static void main(String[] args) {
        // Set look and feel to system default
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Verify assets directory
        verifyAssets();

        // Start game on EDT
        SwingUtilities.invokeLater(() -> {
            GameDashboard.createAndShowGameMenu();
        });
    }

    private static void verifyAssets() {
        // Verify required directories exist
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

        // Verify required images
        String[] requiredImages = {
            "background.png",
            "flappybirdbg.png",
            "yellowbird.png",
            "toppipe.png",
            "bottompipe.png"
        };

        for (String image : requiredImages) {
            File imageFile = new File("flappy-bird-java-master/src/assets/image/" + image);
            if (!imageFile.exists()) {
                System.err.println("Warning: Required image file missing: " + image);
                System.err.println("Please ensure all required assets are in place.");
            }
        }

        // Verify required sounds
        String[] requiredSounds = {
            "point.wav",
            "die.wav",
            "wing.wav"
        };

        for (String sound : requiredSounds) {
            File soundFile = new File("flappy-bird-java-master/src/assets/sound/" + sound);
            if (!soundFile.exists()) {
                System.err.println("Warning: Required sound file missing: " + sound);
                System.err.println("Please ensure all required assets are in place.");
            }
        }
    }
}