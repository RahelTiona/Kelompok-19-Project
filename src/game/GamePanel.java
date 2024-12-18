package game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import config.ScoreDAO;
import gui.GameDashboard;
import game.BirdSkin; // Add this new import
import model.Score;
import model.User;

public class GamePanel extends JPanel {
    private Bird bird;
    private PipeMovements pipeMovements;
    private FlappyMovements flappyMovements;
    private long gameStartTime;
    private int score;
    private boolean isGameActive;
    private boolean isFirstJump;
    private ArrayList<Pipe> pipes;
    private ScoreDAO scoreDAO;
    
    private Image background;
    private Image birdImage;
    private Image topPipe;
    private Image bottomPipe;

    private static final int PANEL_WIDTH = 450;
    private static final int PANEL_HEIGHT = 700;
    private static final int BIRD_START_X = 100;
    private static final int BIRD_START_Y = 300;

    // Update constructor to use the separate BirdSkin enum
    public GamePanel(BirdSkin skin) {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.CYAN);
        pipes = new ArrayList<>();
        scoreDAO = new ScoreDAO();
        initializeGame(skin);
    }

    private void initializeGame(BirdSkin skin) {
        score = 0;
        isGameActive = true;
        gameStartTime = System.currentTimeMillis();

        bird = new Bird(BIRD_START_X, BIRD_START_Y);
        pipeMovements = new PipeMovements(this);
        flappyMovements = new FlappyMovements(this);

        loadImages(skin);
        setupControls();
    }

    private void loadImages(BirdSkin skin) {
        try {
            ClassLoader cl = getClass().getClassLoader();
            background = ImageIO.read(cl.getResourceAsStream("assets/image/flappybirdbg.png"));

            // Load bird image based on selected skin
            String birdImagePath = "assets/image/" + skin.getFilename();
            birdImage = ImageIO.read(cl.getResourceAsStream(birdImagePath));

            topPipe = ImageIO.read(cl.getResourceAsStream("assets/image/toppipe.png"));
            bottomPipe = ImageIO.read(cl.getResourceAsStream("assets/image/bottompipe.png"));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading game assets", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void setupControls() {
        setFocusable(true);
        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e);
            }
        };
        addKeyListener(keyAdapter);
    }

    private void handleKeyPress(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE && isGameActive) {
            if (isFirstJump) {
                isFirstJump = false; // Disable the flag after the first jump
                startGame(); // Start pipe and bird movement
            }
            bird.jump();
            flappyMovements.jump();
        }
    }
    

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw background
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);

        // Draw pipes
        for (Pipe pipe : pipes) {
            g.drawImage(topPipe, pipe.getX(), 0, pipe.getWidth(), pipe.getGapStart(), null);
            g.drawImage(bottomPipe, pipe.getX(),
                    pipe.getGapStart() + Pipe.GAP_HEIGHT,
                    pipe.getWidth(),
                    getHeight() - (pipe.getGapStart() + Pipe.GAP_HEIGHT),
                    null);
        }

        // Draw bird
        g.drawImage(birdImage, bird.getX(), bird.getY(), bird.getWidth(), bird.getHeight(), null);

        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 24));
        g.drawString("Score: " + score, 10, 30);

        // Draw game over screen
        if (!isGameActive) {
            drawGameOver(g);
        }
    }

    private void drawGameOver(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, getWidth(), getHeight());
    
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));
        String gameOver = "Game Over";
        int gameOverX = (getWidth() - g.getFontMetrics().stringWidth(gameOver)) / 2;
        g.drawString(gameOver, gameOverX, getHeight() / 2 - 100);
    
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String finalScore = "Final Score: " + score;
        int scoreX = (getWidth() - g.getFontMetrics().stringWidth(finalScore)) / 2;
        g.drawString(finalScore, scoreX, getHeight() / 2 - 50);
    
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.setColor(Color.YELLOW);
        String buttonText = "Return to Dashboard";
        int buttonTextX = (getWidth() - g.getFontMetrics().stringWidth(buttonText)) / 2;
        g.drawString(buttonText, buttonTextX, getHeight() / 2 + 50);
    }

    public void gameOver() {
        if (isGameActive) {
            isGameActive = false;
            SoundPlayer.playSound("die.wav");
            
            // Hanya simpan skor jika lebih dari 0
            if (score > 0) {
                saveScore();  // Simpan skor ke database
            }
            
            pipeMovements.stopMovement();
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    int mouseX = e.getX();
                    int mouseY = e.getY();
        
                    if (mouseY >= getHeight() / 2 + 40 && mouseY <= getHeight() / 2 + 60) {
                        GameDashboard.showGameDashboard();
                        ((JFrame) SwingUtilities.getWindowAncestor(GamePanel.this)).dispose(); 
                    }
                }
            });
        
            repaint();
        }
    }
    
    private void saveScore() {
        User currentUser = GameDashboard.getCurrentUser();
        if (currentUser != null && score > 0) { // Hanya simpan jika score lebih dari 0
            try {
                int gameDuration = (int) ((System.currentTimeMillis() - gameStartTime) / 1000);
                Score gameScore = new Score(currentUser.getId(), score, gameDuration);
                gameScore.setDateTime(LocalDateTime.now());
                ScoreDAO scoreDAO = new ScoreDAO();
                scoreDAO.initializeUserScore(gameScore);  
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void restartGame() {
        score = 0;
        isGameActive = true;
        isFirstJump = true; // Mengatur ulang flag isFirstJump
        gameStartTime = System.currentTimeMillis();
    
        bird = new Bird(BIRD_START_X, BIRD_START_Y); // Reset posisi burung
        pipeMovements.stopMovement(); // Hentikan pergerakan pipa sementara
        flappyMovements.stop(); // Hentikan pergerakan burung sementara
    
        repaint(); // Perbarui tampilan untuk memastikan burung di posisi awal
        requestFocus(); // Memastikan panel aktif untuk mendeteksi input
    }
    
    

    public void incrementScore() {
        score++;
        SoundPlayer.playSound("point.wav");
    }

    public int getBirdY() {
        return bird.getY();
    }

    public void setBirdY(int y) {
        bird.setY(y);
    }

    public boolean isGameActive() {
        return isGameActive;
    }

    public void setPipes(java.util.List<Pipe> pipes) {
        this.pipes = new ArrayList<>(pipes);
        repaint();
    }

    public ArrayList<Pipe> getPipes() {
        return new ArrayList<>(pipes);
    }

    public void startGame() {
        if (pipeMovements != null && flappyMovements != null) {
            pipeMovements.start();
            flappyMovements.start();
            requestFocus();
        }
    }
}