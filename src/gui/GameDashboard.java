package gui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import game.GamePanel;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import model.User;
import model.Score;
import config.ScoreDAO;
import config.UserDAO;
import custom_component.StyledButton;
import java.util.List;
import java.util.Optional;

public class GameDashboard {
    // Konstanta utama untuk konfigurasi tampilan frame
    private static final int FRAME_WIDTH = 400;
    private static final int FRAME_HEIGHT = 700;
    private static final String BACKGROUND_PATH = "src/assets/image/background.png";

    // Variabel global untuk menyimpan state aplikasi
    private static BirdSkin currentSkin = BirdSkin.YELLOW;
    private static User currentUser;
    private static JFrame frame;
    private static JLabel userStatusLabel;
    private static final ScoreDAO scoreDAO = new ScoreDAO();
    private static final UserDAO userDAO = new UserDAO();

    // Enum untuk mendefinisikan jenis skin burung yang tersedia
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

    // Fungsi utama untuk membuat dan menampilkan menu 
    public static void createAndShowGameMenu() {
        initializeFrame();
        JPanel panel = createMainPanel();
        setupUI(panel);
        frame.add(panel);
        frame.setVisible(true);
    }

    // Fungsi untuk inisialisasi frame utama
    private static void initializeFrame() {
        frame = new JFrame("Game Menu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
    }

    // Fungsi untuk membuat panel utama dengan background kustom
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

    // Fungsi untuk mengatur UI
    private static void setupUI(JPanel panel) {
        panel.setLayout(null);
        setupUserStatusLabel(panel);
        setupButtons(panel);

        JLabel gameTitleLabel = new JLabel("FLAPPY BIRD GAME", SwingConstants.CENTER);
        Font titleFont = new Font("Rockwell", Font.BOLD, 30);
        gameTitleLabel.setFont(titleFont);
        gameTitleLabel.setForeground(new Color(0, 0, 205));
        gameTitleLabel.setBounds(0, 470, FRAME_WIDTH, 40);
        panel.add(gameTitleLabel);
    }

    // Fungsi untuk membuat label status pengguna
    private static void setupUserStatusLabel(JPanel panel) {
        userStatusLabel = new JLabel();
        userStatusLabel.setBounds(0, 30, FRAME_WIDTH, 30);
        userStatusLabel.setFont(new Font("Arial", Font.BOLD, 16));
        userStatusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        updateUserStatusLabel();
        panel.add(userStatusLabel);
    }

    // Fungsi untuk mengatur tombol-tombol
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

    // Fungsi untuk menampilkan dialog skor tertinggi
    private static void showHighScoresDialog() {
        JDialog dialog = new JDialog(frame, "High Scores", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(650, 500);
        dialog.setLocationRelativeTo(frame);
        dialog.setResizable(false);

        JPanel mainPanel = createHighScoreMainPanel();

        dialog.add(mainPanel);
        dialog.setVisible(true);
    }

    private static JPanel createHighScoreMainPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gp = new GradientPaint(0, 0, new Color(135, 206, 235),
                        0, getHeight(), new Color(255, 255, 255));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 10, 10, 10));

        JLabel titleLabel = new JLabel("Score Leaderboard");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JScrollPane tableScrollPane = new JScrollPane(createHighScoreTable());
        tableScrollPane.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        JPanel buttonPanel = createHighScoreButtonPanel();

        mainPanel.add(titleLabel, BorderLayout.NORTH);
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private static JPanel createHighScoreButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        StyledButton playButton = new StyledButton("Play Game", Color.YELLOW);
        StyledButton closeButton = new StyledButton("Close", Color.YELLOW);

        playButton.addActionListener(e -> {
            handlePlayButton();
        });
        closeButton.addActionListener(e -> {
            ((JDialog) SwingUtilities.getWindowAncestor(buttonPanel)).dispose();
        });

        buttonPanel.add(playButton);
        buttonPanel.add(closeButton);
        return buttonPanel;
    }

    private static void showSkinSelectionDialog() {
        if (!checkUserLoggedIn("change skin")) return;

        JDialog skinDialog = new JDialog(frame, "Select Bird Skin", true);
        skinDialog.setLayout(new GridLayout(3, 1, 10, 10));
        skinDialog.setSize(300, 400);
        skinDialog.setLocationRelativeTo(frame);

        for (BirdSkin skin : BirdSkin.values()) {
            JButton skinButton = new JButton(skin.name() + " Bird");
            skinButton.setIcon(new ImageIcon("src/assets/image/" + skin.getFilename()));

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

    // Fungsi untuk menangani aksi tombol "Play"
    private static void handlePlayButton() {
        if (!checkUserLoggedIn("play the game")) return;
        launchGame();
    }

    // Fungsi untuk meluncurkan game
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

    // Fungsi untuk Show Score
    private static void handleHighScoreButton() {
        if (!checkUserLoggedIn("view high scores")) return;
        showHighScoresDialog();
    }

    // Fungsi untuk memeriksa apakah pengguna telah login
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

    // Fungsi untuk menangani tombol "Account"
    private static void handleAccountButton() {
        frame.setVisible(false);
        SwingUtilities.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Fungsi untuk membuat tabel skor tertinggi
    private static JTable createHighScoreTable() {
        String[] columns = {"Rank", "Player", "Score", "Time", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setRowHeight(30);
        table.setFont(new Font("Arial", Font.PLAIN, 14));

        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.getTableHeader().setBackground(new Color(52, 152, 219));
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.GRAY));

        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected) {
                    c.setBackground(row % 2 == 0 ? Color.WHITE : new Color(242, 242, 242));
                }

                setHorizontalAlignment(column == 0 ? SwingConstants.CENTER : SwingConstants.LEFT);
                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));

                return c;
            }
        });

        TableColumnModel columnModel = table.getColumnModel();
        int[] columnWidths = {40, 100, 60, 60, 100};
        for (int i = 0; i < columnWidths.length; i++) {
            columnModel.getColumn(i).setPreferredWidth(columnWidths[i]);
        }

        populateTableData(model);
        return table;
    }

    // Fungsi untuk mengisi data ke tabel skor dari database
    private static void populateTableData(DefaultTableModel model) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            List<Score> scores = scoreDAO.getAllScores();
            int rank = 1;

            for (Score score : scores) {
                User user = userDAO.getUserById(score.getUserId());
                model.addRow(new Object[]{
                        rank++,
                        Optional.ofNullable(user).map(User::getUsername).orElse("Unknown"),
                        score.getScore(),
                        score.getGameDuration() + "s",
                        score.getDateTime().format(formatter)
                });
            }
        } catch (Exception e) {
            handleScoreLoadError(e);
        }
    }

    // Fungsi untuk menangani kesalahan saat memuat skor
    private static void handleScoreLoadError(Exception e) {
        e.printStackTrace();
        JOptionPane.showMessageDialog(null,
                "Error loading scores: " + e.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
    }

    // Fungsi untuk memperbarui label status pengguna
    private static void updateUserStatusLabel() {
        userStatusLabel.setText(Optional.ofNullable(currentUser)
                .map(user -> "Logged in as: " + user.getUsername())
                .orElse("Not Logged In"));
    }

    // Getter untuk pengguna saat ini
    public static User getCurrentUser() {
        return currentUser;
    }

    // Setter untuk pengguna saat ini
    public static void setCurrentUser(User user) {
        currentUser = user;
        Optional.ofNullable(userStatusLabel).ifPresent(label -> updateUserStatusLabel());
    }

    // Fungsi untuk menampilkan dashboard game
    public static void showGameDashboard() {
        createAndShowGameMenu();
    }
}