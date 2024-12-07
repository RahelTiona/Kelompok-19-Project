package main;

import javax.swing.*;
import gui.GameDashboard;

public class FlappyBirdGame {
    public static void main(String[] args) {
        setLookAndFeel();
        SwingUtilities.invokeLater(GameDashboard::createAndShowGameMenu);
    }

    private static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 