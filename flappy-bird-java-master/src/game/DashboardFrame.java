package game;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import config.ScoreDAO;
import config.UserDAO;
import gui.LoginFrame;
import model.Score;
import model.User;

public class DashboardFrame extends JFrame {
    // Use Integer instead of generic type
    private Integer currentUserId;
    private JLabel welcomeLabel;
    private JTable highScoresTable;
    
    // Constants for frame and layout
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 600;
    private static final int PADDING = 20;
    
    // Constructor with explicit Integer userId
    public DashboardFrame(Integer userId) {
        this.currentUserId = userId;
        initializeFrame();
        createAndShowGUI();
        loadUserData();
        loadHighScores();
    }
    
    private void initializeFrame() {
        setTitle("Flappy Bird - Dashboard");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setLocationRelativeTo(null);
        setResizable(false);
    }
    
    private void createAndShowGUI() {
        // Create main container with padding
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        ((JComponent) contentPane).setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        
        // Add components to the content pane
        contentPane.add(createTopPanel(), BorderLayout.NORTH);
        contentPane.add(createCenterPanel(), BorderLayout.CENTER);
        contentPane.add(createBottomPanel(), BorderLayout.SOUTH);
    }
    
    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        welcomeLabel = new JLabel("Welcome back!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        topPanel.add(welcomeLabel);
        return topPanel;
    }
    
    private JPanel createCenterPanel() {
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "High Scores"),
            BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING)
        ));
        
        // Create table with non-editable model
        String[] columns = {"Rank", "Player", "Score", "Duration", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        highScoresTable = new JTable(model);
        highScoresTable.setFillsViewportHeight(true);
        highScoresTable.setRowHeight(25);
        highScoresTable.setFont(new Font("Arial", Font.PLAIN, 14));
        highScoresTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        
        // Configure column widths
        TableColumnModel columnModel = highScoresTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(50);  // Rank
        columnModel.getColumn(1).setPreferredWidth(150); // Player
        columnModel.getColumn(2).setPreferredWidth(100); // Score
        columnModel.getColumn(3).setPreferredWidth(100); // Duration
        columnModel.getColumn(4).setPreferredWidth(150); // Date
        
        // Add table to scroll pane
        JScrollPane scrollPane = new JScrollPane(highScoresTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        centerPanel.add(scrollPane, BorderLayout.CENTER);
        
        return centerPanel;
    }
    
    private JPanel createBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, PADDING, PADDING));
        
        JButton playButton = createStyledButton("Play Game");
        JButton logoutButton = createStyledButton("Logout");
        
        playButton.addActionListener(e -> startGame());
        logoutButton.addActionListener(e -> logout());
        
        bottomPanel.add(playButton);
        bottomPanel.add(logoutButton);
        
        return bottomPanel;
    }
    
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }
    
    private void loadUserData() {
        // Add null check for currentUserId
        if (currentUserId == null) {
            JOptionPane.showMessageDialog(this,
                "Invalid User ID",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            UserDAO userDAO = new UserDAO();
            User user = userDAO.getUserById(currentUserId);
            
            if (user != null) {
                welcomeLabel.setText("Welcome back, " + user.getUsername() + "!");
            } else {
                welcomeLabel.setText("Welcome back!");
                JOptionPane.showMessageDialog(this,
                    "User not found",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log full stack trace
            JOptionPane.showMessageDialog(this,
                "Error loading user data: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void loadHighScores() {
        try {
            ScoreDAO scoreDAO = new ScoreDAO();
            List<Score> scores = scoreDAO.getTopScores(10);
            DefaultTableModel model = (DefaultTableModel) highScoresTable.getModel();
            model.setRowCount(0);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            UserDAO userDAO = new UserDAO(); // Create outside the loop for efficiency
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
                } catch (Exception userLookupEx) {
                    // Log individual user lookup errors without stopping entire process
                    System.err.println("Could not retrieve user for score: " + score.getScoreId());
                }
            }
            
            // Handle empty scores list
            if (scores.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "No high scores available",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace(); // Log full stack trace
            JOptionPane.showMessageDialog(this,
                "Error loading high scores: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void startGame() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            JFrame gameFrame = new JFrame("Flappy Bird");
            gameFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            gameFrame.setSize(800, 600);
            gameFrame.setLocationRelativeTo(null);
            gameFrame.setResizable(false);
            gameFrame.add(new GamePanel());
            gameFrame.setVisible(true);
        });
    }
    
    private void logout() {
        dispose();
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }
}