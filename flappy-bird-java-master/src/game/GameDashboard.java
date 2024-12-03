package game;

import javax.swing.*;
import java.awt.*;
import gui.LoginFrame;
import model.User;

public class GameDashboard {
    private static User currentUser;
    private static JFrame frame;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            createAndShowGameMenu();
        });
    }

    public static void createAndShowGameMenu() {
        // Create main frame
        frame = new JFrame("Game Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        // Main panel with background
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                // Draw background
                ImageIcon background = new ImageIcon("src/assets/image/background.png");
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        panel.setLayout(null);

        // Create buttons with consistent styling
        JButton accountButton = createStyledButton("Account", 200);
        JButton highScoreButton = createStyledButton("High Score", 260);
        JButton playButton = createStyledButton("PLAY", 320);

        // Add action listeners
        accountButton.addActionListener(e -> handleAccountButton());
        highScoreButton.addActionListener(e -> handleHighScoreButton());
        playButton.addActionListener(e -> handlePlayButton());

        // Add buttons to panel
        panel.add(accountButton);
        panel.add(highScoreButton);
        panel.add(playButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static JButton createStyledButton(String text, int yPosition) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                // Paint yellow background
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                
                // Paint text
                g.setColor(getForeground());
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g.drawString(getText(), x, y);
            }
        };
        
        button.setText(text);
        button.setBounds(130, yPosition, 120, 40);
        button.setBackground(Color.YELLOW);
        button.setForeground(Color.BLACK);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Add hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 255, 150));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.YELLOW);
            }
        });
        
        return button;
    }

    // ... rest of the code remains the same ...
    
    private static void handleAccountButton() {
        frame.setVisible(false);
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }

    private static void handleHighScoreButton() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(frame,
                "Please login first to view high scores",
                "Login Required",
                JOptionPane.INFORMATION_MESSAGE);
            handleAccountButton();
        } else {
            frame.setVisible(false);
            SwingUtilities.invokeLater(() -> {
                new DashboardFrame(currentUser.getId()).setVisible(true);
            });
        }
    }

    private static void handlePlayButton() {
        if (currentUser == null) {
            int choice = JOptionPane.showConfirmDialog(frame,
                "Would you like to login first to save your score?\nClick No to play as guest.",
                "Login Recommended",
                JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (choice == JOptionPane.YES_OPTION) {
                handleAccountButton();
                return;
            } else if (choice == JOptionPane.CANCEL_OPTION) {
                return;
            }
        }

        // Start the game
        frame.setVisible(false);
        SwingUtilities.invokeLater(() -> {
            JFrame gameFrame = new JFrame("Flappy Bird");
            gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            GamePanel gamePanel = new GamePanel();
            gameFrame.add(gamePanel);
            gameFrame.pack();
            gameFrame.setLocationRelativeTo(null);
            gameFrame.setVisible(true);
            
            // Start game thread
            new Thread(gamePanel).start();
            
            // Add window listener to handle game closure
            gameFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosed(java.awt.event.WindowEvent windowEvent) {
                    createAndShowGameMenu();
                }
            });
        });
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}