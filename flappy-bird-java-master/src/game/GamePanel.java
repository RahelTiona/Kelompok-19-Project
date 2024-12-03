// package game;

// import java.awt.*;
// import java.awt.event.KeyAdapter;
// import java.awt.event.KeyEvent;
// import java.util.List;
// import javax.swing.*;
// import javax.imageio.ImageIO;
// import java.io.IOException;

// public class GamePanel extends JPanel implements Runnable {
//     private PipeMovements pipeMovements;
//     private int birdY;
//     private int score;
//     private boolean isGameActive;

//     // Images
//     private Image background;
//     private Image bird;
//     private Image topPipe;
//     private Image bottomPipe;

//     public GamePanel() {
//         setPreferredSize(new Dimension(800, 600));
//         setBackground(Color.CYAN);

//         // Initialize game variables
//         birdY = 300;
//         score = 0;
//         isGameActive = true;

//         // Load assets
//         try {
//             background = ImageIO.read(getClass().getResource("/assets/image/flappybirdbg.png"));
//             bird = ImageIO.read(getClass().getResource("/assets/image/yellowbird.png"));
//             topPipe = ImageIO.read(getClass().getResource("/assets/image/toppipe.png"));
//             bottomPipe = ImageIO.read(getClass().getResource("/assets/image/bottompipe.png"));
//         } catch (IOException e) {
//             e.printStackTrace();
//         }

//         // Initialize pipes
//         pipeMovements = new PipeMovements(this);
//         pipeMovements.start();

//         // Add key listener for bird jump
//         addKeyListener(new KeyAdapter() {
//             @Override
//             public void keyPressed(KeyEvent e) {
//                 if (e.getKeyCode() == KeyEvent.VK_SPACE && isGameActive) {
//                     birdY -= 20;

//                     // Play flap sound
//                     SoundPlayer.playSound("point.wav");
//                 }
//             }
//         });
//         setFocusable(true);
//     }

//     public void setBirdY(int birdY) {
//         this.birdY = birdY;
//     }

//     public int getBirdY() {
//         return birdY;
//     }

//     public void incrementScore() {
//         score++;
//     }

//     public int getScore() {
//         return score;
//     }

//     public boolean isGameActive() {
//         return isGameActive;
//     }

//     public void gameOver() {
//         isGameActive = false;
//         pipeMovements.stopMovement();

//         // Play game over sound
//         SoundPlayer.playSound("die.wav");
//     }

//     public void setPipes(List<Pipe> pipes) {
//         pipeMovements.setPipes(pipes);
//     }

//     @Override
//     protected void paintComponent(Graphics g) {
//         super.paintComponent(g);

//         // Draw the background
//         g.drawImage(background, 0, 0, getWidth(), getHeight(), null);

//         // Draw the bird
//         g.drawImage(bird, 100, birdY, 30, 30, null);

//         // Draw the pipes
//         for (Pipe pipe : pipeMovements.getPipes()) {
//             g.drawImage(topPipe, pipe.getX(), pipe.getGapPosition() - topPipe.getHeight(null), 60, topPipe.getHeight(null), null);
//             g.drawImage(bottomPipe, pipe.getX(), pipe.getGapPosition() + 200, 60, getHeight() - pipe.getGapPosition() - 200, null);
//         }

//         // Draw the score
//         g.setColor(Color.BLACK);
//         g.setFont(new Font("Arial", Font.BOLD, 24));
//         g.drawString("Score: " + score, 10, 30);

//         if (!isGameActive) {
//             g.setColor(Color.RED);
//             g.setFont(new Font("Arial", Font.BOLD, 48));
//             g.drawString("Game Over", 250, 300);
//         }
//     }

//     @Override
//     public void run() {
//         while (isGameActive) {
//             birdY += 2;
//             repaint();

//             try {
//                 Thread.sleep(16);
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             }
//         }
//     }
// }

package game;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.IOException;
import java.time.LocalDateTime;
import config.ScoreDAO;
import model.Score;
import model.User;

public class GamePanel extends JPanel implements Runnable {
    // Game components
    private Bird bird;
    private PipeMovements pipeMovements;
    private FlappyMovements flappyMovements;
    private long gameStartTime;
    private int score;
    private boolean isGameActive;
    
    // Images
    private Image background;
    private Image birdImage;
    private Image topPipe;
    private Image bottomPipe;
    
    // Constants
    private static final int PANEL_WIDTH = 800;
    private static final int PANEL_HEIGHT = 600;
    private static final int BIRD_START_X = 100;
    private static final int BIRD_START_Y = 300;
    
    public GamePanel() {
        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
        setBackground(Color.CYAN);
        initializeGame();
    }
    
    private void initializeGame() {
        // Initialize game state
        score = 0;
        isGameActive = true;
        gameStartTime = System.currentTimeMillis();
        
        // Create bird with initial position
        bird = new Bird(BIRD_START_X, BIRD_START_Y);
        
        // Initialize movements
        pipeMovements = new PipeMovements(this);
        flappyMovements = new FlappyMovements(this);
        
        // Load images
        loadImages();
        
        // Add keyboard controls
        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE && isGameActive) {
                    bird.jump();
                    flappyMovements.jump();
                    SoundPlayer.playSound("wing.wav");
                } else if (e.getKeyCode() == KeyEvent.VK_R && !isGameActive) {
                    restartGame();
                }
            }
        });
        
        // Start game threads
        pipeMovements.start();
        flappyMovements.start();
    }
    
    private void loadImages() {
        try {
            background = ImageIO.read(getClass().getResourceAsStream("/assets/image/flappybirdbg.png"));
            birdImage = ImageIO.read(getClass().getResourceAsStream("/assets/image/yellowbird.png"));
            topPipe = ImageIO.read(getClass().getResourceAsStream("/assets/image/toppipe.png"));
            bottomPipe = ImageIO.read(getClass().getResourceAsStream("/assets/image/bottompipe.png"));
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading game assets", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw background
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        
        // Draw pipes
        for (Pipe pipe : pipeMovements.getPipes()) {
            g.drawImage(topPipe, pipe.getX(), 0, Pipe.WIDTH, pipe.getGapStart(), null);
            g.drawImage(bottomPipe, pipe.getX(), 
                pipe.getGapStart() + Pipe.GAP_HEIGHT, 
                Pipe.WIDTH, 
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
        g.drawString(gameOver, gameOverX, getHeight() / 2 - 50);
        
        g.setFont(new Font("Arial", Font.BOLD, 24));
        String finalScore = "Final Score: " + score;
        int scoreX = (getWidth() - g.getFontMetrics().stringWidth(finalScore)) / 2;
        g.drawString(finalScore, scoreX, getHeight() / 2);
        
        String restart = "Press 'R' to Restart";
        int restartX = (getWidth() - g.getFontMetrics().stringWidth(restart)) / 2;
        g.drawString(restart, restartX, getHeight() / 2 + 50);
    }
    
    public void gameOver() {
        if (isGameActive) {
            isGameActive = false;
            SoundPlayer.playSound("die.wav");
            saveScore();
        }
    }
    
    private void saveScore() {
        User currentUser = GameDashboard.getCurrentUser();
        if (currentUser != null) {
            try {
                ScoreDAO scoreDAO = new ScoreDAO();
                int gameDuration = (int)((System.currentTimeMillis() - gameStartTime) / 1000);
                Score gameScore = new Score(currentUser.getId(), score, gameDuration);
                gameScore.setDateTime(LocalDateTime.now());
                
                Score existingHighScore = scoreDAO.getHighestScoreByUserId(currentUser.getId());
                if (existingHighScore == null || score > existingHighScore.getScore()) {
                    scoreDAO.saveScore(gameScore);
                    JOptionPane.showMessageDialog(this, 
                        "New High Score: " + score + "!", 
                        "Congratulations", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    scoreDAO.saveScore(gameScore);
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error saving score: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void restartGame() {
        // Reset game state
        score = 0;
        isGameActive = true;
        gameStartTime = System.currentTimeMillis();
        
        // Reset bird position
        bird = new Bird(BIRD_START_X, BIRD_START_Y);
        
        // Reset movements
        pipeMovements.resetGame();
        flappyMovements = new FlappyMovements(this);
        flappyMovements.start();
        
        // Request focus for controls
        requestFocus();
    }
    
    @Override
    public void run() {
        while (isGameActive) {
            bird.update();
            repaint();
            try {
                Thread.sleep(16);  // ~60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    // Getters and setters
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
        pipeMovements.setPipes(pipes);
    }
}