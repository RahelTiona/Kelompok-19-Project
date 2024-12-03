package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import game.GamePanel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import model.User;
import model.Score;
import config.ScoreDAO;
import config.UserDAO;
import java.util.List;

public class GameDashboard {
    private static User currentUser;
    private static JFrame frame;
    private static JLabel userStatusLabel;
    private static ScoreDAO scoreDAO = new ScoreDAO();
    private static UserDAO userDAO = new UserDAO();

    public static void createAndShowGameMenu() {
        frame = new JFrame("Game Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 600);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);

        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon background = new ImageIcon("src/assets/image/background.png");
                g.drawImage(background.getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };
        panel.setLayout(null);

        userStatusLabel = new JLabel();
        userStatusLabel.setBounds(0, 30, 400, 30);
        userStatusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userStatusLabel.setForeground(Color.BLACK);
        userStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateUserStatusLabel();
        panel.add(userStatusLabel);

        JButton accountButton = createStyledButton("Account", 200);
        JButton highScoreButton = createStyledButton("Show Score", 260);
        JButton playButton = createStyledButton("PLAY", 320);

        accountButton.addActionListener(e -> handleAccountButton());
        highScoreButton.addActionListener(e -> handleHighScoreButton());
        playButton.addActionListener(e -> handlePlayButton());

        panel.add(accountButton);
        panel.add(highScoreButton);
        panel.add(playButton);

        frame.add(panel);
        frame.setVisible(true);
    }

    private static void updateUserStatusLabel() {
        if (currentUser != null) {
            userStatusLabel.setText("Logged in as: " + currentUser.getUsername());
        } else {
            userStatusLabel.setText("Not Logged In");
        }
    }

    private static JButton createStyledButton(String text, int yPosition) {
        JButton button = new JButton() {
            @Override
            protected void paintComponent(Graphics g) {
                g.setColor(getBackground());
                g.fillRect(0, 0, getWidth(), getHeight());
                
                g.setColor(getForeground());
                FontMetrics fm = g.getFontMetrics();
                int x = (getWidth() - fm.stringWidth(getText())) / 2;
                int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                g.drawString(getText(), x, y);
            }
        };
        
        button.setText(text);
        button.setBounds(100, yPosition, 200, 50);
        button.setBackground(Color.YELLOW);
        button.setForeground(Color.BLACK);
        button.setFocusable(false);
        button.setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
        button.setFont(new Font("Arial", Font.BOLD, 14));
        
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

    private static void handleAccountButton() {
        frame.setVisible(false);
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }

    private static void handlePlayButton() {
        if (currentUser == null) {
            int response = JOptionPane.showConfirmDialog(
                frame, 
                "You need to login to play the game.\nWould you like to login now?", 
                "Login Required", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE
            );

            if (response == JOptionPane.YES_OPTION) {
                handleAccountButton();
            }
            return;
        }

        frame.dispose(); 
        SwingUtilities.invokeLater(() -> {
            new GamePanel().setVisible(true);
        });
    }

    private static void handleHighScoreButton() {
        if (currentUser == null) {
            int response = JOptionPane.showConfirmDialog(
                frame, 
                "You need to login to view high scores.\nWould you like to login now?", 
                "Login Required", 
                JOptionPane.YES_NO_OPTION, 
                JOptionPane.QUESTION_MESSAGE
            );

            if (response == JOptionPane.YES_OPTION) {
                handleAccountButton();
            }
            return;
        }
        
        showHighScoresDialog();
    }

    private static void showHighScoresDialog() {
        JDialog dialog = new JDialog(frame, "High Scores", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(frame);

        // Welcome Panel
        JPanel welcomePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomePanel.add(welcomeLabel);

        // Table Panel
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "High Scores"));

        String[] columns = {"Rank", "Player", "Score", "Duration", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable highScoresTable = new JTable(model);
        highScoresTable.setFillsViewportHeight(true);
        highScoresTable.setRowHeight(25);
        highScoresTable.setFont(new Font("Arial", Font.PLAIN, 14));
        highScoresTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        TableColumnModel columnModel = highScoresTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);
        columnModel.getColumn(1).setPreferredWidth(150);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(150);

        try {
            List<Score> scores = scoreDAO.getTopScores(10);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            int rank = 1;
            for (Score score : scores) {
                try {
                    User user = userDAO.getUserById(score.getUserId());
                    model.addRow(new Object[]{
                        rank++,
                        user != null ? user.getUsername() : "Unknown",
                        score.getScore(),
                        score.getGameDuration() + "s",
                        score.getDateTime().format(formatter)
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(dialog,
                "Error loading scores: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }

        JScrollPane scrollPane = new JScrollPane(highScoresTable);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton playButton = new JButton("Play Game");
        JButton closeButton = new JButton("Close");

        playButton.addActionListener(e -> {
            dialog.dispose();
            handlePlayButton();
        });
        closeButton.addActionListener(e -> dialog.dispose());

        buttonPanel.add(playButton);
        buttonPanel.add(closeButton);

        dialog.add(welcomePanel, BorderLayout.NORTH);
        dialog.add(tablePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    public static void setCurrentUser(User user) {
        currentUser = user;
        // Update label status login setiap kali user diset
        if (userStatusLabel != null) {
            updateUserStatusLabel();
        }
    }

    public static User getCurrentUser() {
        return currentUser;
    }
}