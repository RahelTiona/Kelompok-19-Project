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
import java.util.Optional;

public class GameDashboard {
    private static final int FRAME_WIDTH = 400;
    private static final int FRAME_HEIGHT = 700;
    private static final int BUTTON_WIDTH = 200;
    private static final int BUTTON_HEIGHT = 50;
    private static final int BUTTON_X = 100;
    private static final String BACKGROUND_PATH = "src/assets/image/background.png";

    public enum BirdSkin {
        YELLOW("yellowbird.png"),
        BLUE("bluebird.png"),
        RED("redbird.png");

        private final String filename;

        BirdSkin(String filename) {
            this.filename = filename;
        }

        public String getFilename() {
            return filename;
        }
    }

    private static BirdSkin currentSkin = BirdSkin.YELLOW; 
    private static User currentUser;
    private static JFrame frame;
    private static JLabel userStatusLabel;
    private static final ScoreDAO scoreDAO = new ScoreDAO();
    private static final UserDAO userDAO = new UserDAO();
    
    private static class StyledButton extends JButton {
        public StyledButton(String text, int yPosition) {
            setText(text);
            setBounds(BUTTON_X, yPosition, BUTTON_WIDTH, BUTTON_HEIGHT);
            setBackground(Color.YELLOW);
            setForeground(Color.BLACK);
            setFocusable(false);
            setBorder(BorderFactory.createLineBorder(Color.BLACK, 2));
            setFont(new Font("Arial", Font.BOLD, 14));
            setupHoverEffect();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            g.setColor(getBackground());
            g.fillRect(0, 0, getWidth(), getHeight());
            g.setColor(getForeground());
            FontMetrics fm = g.getFontMetrics();
            drawCenteredText(g, fm);
        }
        
        private void drawCenteredText(Graphics g, FontMetrics fm) {
            int x = (getWidth() - fm.stringWidth(getText())) / 2;
            int y = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g.drawString(getText(), x, y);
        }
        
        private void setupHoverEffect() {
            addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    setBackground(new Color(255, 255, 150));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    setBackground(Color.YELLOW);
                }
            });
        }
    }

    public static void createAndShowGameMenu() {
        initializeFrame();
        JPanel panel = createMainPanel();
        setupUI(panel);
        frame.add(panel);
        frame.setVisible(true);
    }
    
    private static void initializeFrame() {
        frame = new JFrame("Game Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }
    
    private static JPanel createMainPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Optional.ofNullable(new ImageIcon(BACKGROUND_PATH).getImage())
                    .ifPresent(img -> g.drawImage(img, 0, 0, getWidth(), getHeight(), null));
            }
        };
    }
    
    private static void setupUI(JPanel panel) {
        panel.setLayout(null);
        setupUserStatusLabel(panel);
        setupButtons(panel);
    }
    
    private static void setupUserStatusLabel(JPanel panel) {
        userStatusLabel = new JLabel();
        userStatusLabel.setBounds(0, 30, FRAME_WIDTH, 30);
        userStatusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateUserStatusLabel();
        panel.add(userStatusLabel);
    }
    
    private static void setupButtons(JPanel panel) {
        StyledButton accountButton = new StyledButton("Account", 220);
        StyledButton highScoreButton = new StyledButton("Show Score", 280);
        StyledButton playButton = new StyledButton("PLAY", 340);
        StyledButton skinButton = new StyledButton("Change Skin", 400);
        
        accountButton.addActionListener(e -> handleAccountButton());
        skinButton.addActionListener(e -> showSkinSelectionDialog());
        highScoreButton.addActionListener(e -> handleHighScoreButton());
        playButton.addActionListener(e -> handlePlayButton());
        
        panel.add(accountButton);
        panel.add(skinButton);
        panel.add(highScoreButton);
        panel.add(playButton);
    }
    
    private static void showSkinSelectionDialog() {
        if (!checkUserLoggedIn("change skin")) return;

        JDialog skinDialog = new JDialog(frame, "Select Bird Skin", true);
        skinDialog.setLayout(new GridLayout(3, 1, 10, 10));
        skinDialog.setSize(300, 400);
        skinDialog.setLocationRelativeTo(frame);

        // Create buttons for each skin
        for (BirdSkin skin : BirdSkin.values()) {
            JButton skinButton = new JButton(skin.name() + " Bird");
            try {
                skinButton.setIcon(new ImageIcon("src/assets/image/" + skin.getFilename()));
            } catch (Exception e) {
                skinButton.setText(skin.name() + " Bird (Image Not Found)");
            }
            
            skinButton.addActionListener(e -> {
                currentSkin = skin;
                JOptionPane.showMessageDialog(skinDialog, 
                    skin.name() + " bird selected!", 
                    "Skin Changed", 
                    JOptionPane.INFORMATION_MESSAGE);
                skinDialog.dispose();
            });
            skinDialog.add(skinButton);
        }

        skinDialog.setVisible(true);
    }
    
    private static void handlePlayButton() {
        if (!checkUserLoggedIn("play the game")) return;
        launchGame();
    }
    
    private static void launchGame() {
        frame.setVisible(false);
        SwingUtilities.invokeLater(() -> {
            JFrame gameFrame = new JFrame("Flappy Bird");
            GamePanel gamePanel = new GamePanel(currentSkin);
            gameFrame.setContentPane(gamePanel);
            gameFrame.pack();
            gameFrame.setLocationRelativeTo(null);
            gameFrame.setResizable(false);

            gameFrame.addWindowListener(new java.awt.event.WindowAdapter() {
                @Override
                public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                    createAndShowGameMenu();
                }
            });

            gameFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            gameFrame.setVisible(true);
            gamePanel.requestFocusInWindow();
            gamePanel.startGame();
        });
    }

    private static void handleHighScoreButton() {
        if (!checkUserLoggedIn("view high scores")) return;
        showHighScoresDialog();
    }
    
    private static boolean checkUserLoggedIn(String action) {
        if (currentUser != null) return true;
        
        int response = JOptionPane.showConfirmDialog(
            frame,
            "You need to login to " + action + ".\nWould you like to login now?",
            "Login Required",
            JOptionPane.YES_NO_OPTION
        );
        
        if (response == JOptionPane.YES_OPTION) {
            handleAccountButton();
        }
        return false;
    }
    
    private static void showHighScoresDialog() {
        JDialog dialog = createHighScoreDialog();
        setupHighScoreDialogContent(dialog);
        dialog.setVisible(true);
    }
    
    private static JDialog createHighScoreDialog() {
        JDialog dialog = new JDialog(frame, "High Scores", true);
        dialog.setLayout(new BorderLayout(10, 10));
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(frame);
        return dialog;
    }
    
    private static void setupHighScoreDialogContent(JDialog dialog) {
        JPanel welcomePanel = createWelcomePanel();
        JPanel tablePanel = createScoreTablePanel();
        JPanel buttonPanel = createButtonPanel(dialog);
        
        dialog.add(welcomePanel, BorderLayout.NORTH);
        dialog.add(tablePanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private static JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel welcomeLabel = new JLabel("Welcome back, " + currentUser.getUsername() + "!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        panel.add(welcomeLabel);
        return panel;
    }
    
    private static JPanel createScoreTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY), "High Scores"));
        panel.add(new JScrollPane(createHighScoreTable()), BorderLayout.CENTER);
        return panel;
    }
    
    private static JTable createHighScoreTable() {
        String[] columns = {"Rank", "Player", "Score", "Duration", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        
        JTable table = new JTable(model);
        configureTableProperties(table);
        populateTableData(model);
        return table;
    }
    
    private static void populateTableData(DefaultTableModel model) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            List<Score> scores = scoreDAO.getTopScores(10);
            int rank = 1;
            
            for (Score score : scores) {
                try {
                    User user = userDAO.getUserById(score.getUserId());
                    model.addRow(new Object[]{
                        rank++,
                        Optional.ofNullable(user).map(User::getUsername).orElse("Unknown"),
                        score.getScore(),
                        score.getGameDuration() + "s",
                        score.getDateTime().format(formatter)
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            handleScoreLoadError(e);
        }
    }
    
    private static void handleScoreLoadError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,
            "Error loading scores: " + e.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
    }
    
    private static void configureTableProperties(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(25);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        TableColumnModel columnModel = table.getColumnModel();
        int[] columnWidths = {50, 150, 100, 100, 150};
        for (int i = 0; i < columnWidths.length; i++) {
            columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
        }
    }
    
    private static JPanel createButtonPanel(JDialog dialog) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton playButton = new JButton("Play Game");
        JButton closeButton = new JButton("Close");
        
        playButton.addActionListener(e -> {
            dialog.dispose();
            handlePlayButton();
        });
        closeButton.addActionListener(e -> dialog.dispose());
        
        panel.add(playButton);
        panel.add(closeButton);
        return panel;
    }
    
    private static void handleAccountButton() {
        frame.setVisible(false);
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
    
    public static void setCurrentUser(User user) {
        currentUser = user;
        Optional.ofNullable(userStatusLabel).ifPresent(label -> updateUserStatusLabel());
    }
    
    private static void updateUserStatusLabel() {
        userStatusLabel.setText(Optional.ofNullable(currentUser)
            .map(user -> "Logged in as: " + user.getUsername())
            .orElse("Not Logged In"));
    }
    
    public static User getCurrentUser() {
        return currentUser;
    }

    public static void showGameDashboard() {
        createAndShowGameMenu();
    }
}

