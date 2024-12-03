// package game;

// import java.util.List;

// import java.util.ArrayList;
// import java.util.List;
// import java.util.Random;

// public class PipeMovements extends Thread {
//     private volatile boolean isRunning = true;
//     private List<Pipe> pipes = new ArrayList<>();
//     private Random random = new Random();
//     private GamePanel gamePanel;
    
//     // Constants for pipe configuration
//     private static final int PIPE_WIDTH = 60;
//     private static final int PIPE_GAP = 200;
//     private static final int PIPE_SPACING = 300;
//     private static final int INITIAL_PIPE_X = 800;
//     private static final int MIN_GAP_POSITION = 100;
//     private static final int GAP_RANGE = 200;
    
//     public PipeMovements(GamePanel gamePanel) {
//         this.gamePanel = gamePanel;
//         initializePipes();
//     }
    
//     private void initializePipes() {
//         for (int i = 0; i < 3; i++) {
//             int gapPosition = generateGapPosition();
//             pipes.add(new Pipe(INITIAL_PIPE_X + (i * PIPE_SPACING), gapPosition));
//         }
//         gamePanel.setPipes(pipes);
//     }
    
//     private int generateGapPosition() {
//         return random.nextInt(GAP_RANGE) + MIN_GAP_POSITION;
//     }
    
//     @Override
//     public void run() {
//         while (isRunning) {
//             try {
//                 updatePipes();
//                 checkCollisions();
//                 updateGamePanel();
//                 Thread.sleep(16); // Approximately 60 FPS
//             } catch (InterruptedException e) {
//                 Thread.currentThread().interrupt();
//                 break;
//             }
//         }
//     }
    
//     private void updatePipes() {
//         for (Pipe pipe : pipes) {
//             pipe.setX(pipe.getX() - 2);
            
//             if (pipe.getX() < -PIPE_WIDTH) {
//                 pipe.setX(findRightmostX() + PIPE_SPACING);
//                 pipe.setGapPosition(generateGapPosition());
//                 if (gamePanel.isGameActive()) {
//                     gamePanel.incrementScore();
//                 }
//             }
//         }
//     }
    
//     private int findRightmostX() {
//         int rightmostX = -PIPE_WIDTH;
//         for (Pipe pipe : pipes) {
//             rightmostX = Math.max(rightmostX, pipe.getX());
//         }
//         return rightmostX;
//     }
    
//     private void checkCollisions() {
//         if (hasCollision() && gamePanel.isGameActive()) {
//             gamePanel.gameOver();
//         }
//     }
    
//     private boolean hasCollision() {
//         int birdX = 100;  // Bird's fixed X position
//         int birdY = gamePanel.getBirdY();
//         int birdSize = 30;
        
//         for (Pipe pipe : pipes) {
//             if (birdX + birdSize > pipe.getX() && birdX < pipe.getX() + PIPE_WIDTH) {
//                 if (birdY < pipe.getGapPosition() || 
//                     birdY + birdSize > pipe.getGapPosition() + PIPE_GAP) {
//                     return true;
//                 }
//             }
//         }
        
//         return birdY < 0 || birdY + birdSize > gamePanel.getHeight();
//     }
    
//     private void updateGamePanel() {
//         gamePanel.setPipes(new ArrayList<>(pipes));
//         gamePanel.repaint();
//     }
    
//     public void stopMovement() {
//         isRunning = false;
//     }
    
//     public List<Pipe> getPipes() {
//         return pipes;
//     }
    
//     public void setPipes(List<Pipe> pipes) {
//         this.pipes = pipes;
//     }
// }

// // package game;

// // import java.util.ArrayList;
// // import java.util.List;

// // public class PipeMovements extends Thread {
// //     private GamePanel gamePanel;
// //     private List<Pipe> pipes;
// //     private boolean isRunning;

// //     public PipeMovements(GamePanel gamePanel) {
// //         this.gamePanel = gamePanel;
// //         this.pipes = new ArrayList<>();
// //         this.isRunning = true;
// //     }

// //     public void setPipes(List<Pipe> pipes) {
// //         this.pipes = pipes;
// //     }

// //     public List<Pipe> getPipes() {
// //         return pipes;
// //     }

// //     public boolean isCollidingWithPipe() {
// //         for (Pipe pipe : pipes) {
// //             if (pipe.getX() < 130 && pipe.getX() + 60 > 100 && (gamePanel.getBirdY() < pipe.getGapPosition() || gamePanel.getBirdY() + 30 > pipe.getGapPosition() + 200)) {
// //                 return true;  // Tabrakan dengan pipa
// //             }
// //         }
// //         return false;
// //     }

// //     public void stopMovement() {
// //         isRunning = false;  // Hentikan pergerakan pipa
// //     }

// //     @Override
// //     public void run() {
// //         while (isRunning) {
// //             // Gerakkan pipa ke kiri
// //             for (Pipe pipe : pipes) {
// //                 pipe.setX(pipe.getX() - 5);
// //             }

// //             // Cek tabrakan
// //             if (isCollidingWithPipe()) {
// //                 gamePanel.gameOver();  // Panggil gameOver jika terjadi tabrakan
// //                 return;
// //             }

// //             try {
// //                 Thread.sleep(20);
// //             } catch (InterruptedException e) {
// //                 e.printStackTrace();
// //             }

// //             gamePanel.repaint();  // Perbarui tampilan game
// //         }
// //     }
// // }







package game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PipeMovements extends Thread {
    // Constants
    private static final int INITIAL_PIPE_COUNT = 3;
    private static final int PIPE_SPACING = 300;
    private static final int INITIAL_X = 800;
    private static final int BIRD_X = 100;
    private static final int BIRD_SIZE = 30;
    private static final long FRAME_TIME = 16; // ~60 FPS
    
    private volatile boolean isRunning = true;
    private final CopyOnWriteArrayList<Pipe> pipes;
    private final GamePanel gamePanel;
    
    public PipeMovements(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
        this.pipes = new CopyOnWriteArrayList<>();
        initializePipes();
    }
    
    private void initializePipes() {
        for (int i = 0; i < INITIAL_PIPE_COUNT; i++) {
            pipes.add(new Pipe(INITIAL_X + (i * PIPE_SPACING)));
        }
        gamePanel.setPipes(new ArrayList<>(pipes));
    }
    
    @Override
    public void run() {
        while (isRunning) {
            try {
                updatePipes();
                checkCollisions();
                updateGamePanel();
                Thread.sleep(FRAME_TIME);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    private void updatePipes() {
        int rightmostX = -Pipe.WIDTH;
        
        for (Pipe pipe : pipes) {
            pipe.move();
            rightmostX = Math.max(rightmostX, pipe.getX());
            
            // Score when passing pipe
            if (pipe.needsScoring(BIRD_X)) {
                if (gamePanel.isGameActive()) {
                    gamePanel.incrementScore();
                }
            }
            
            // Recycle off-screen pipes
            if (pipe.isOffScreen()) {
                pipe.reset(rightmostX + PIPE_SPACING);
            }
        }
    }
    
    private void checkCollisions() {
        if (!gamePanel.isGameActive()) return;
        
        int birdY = gamePanel.getBirdY();
        
        // Check ground collision
        if (birdY < 0 || birdY + BIRD_SIZE > gamePanel.getHeight()) {
            gamePanel.gameOver();
            return;
        }
        
        // Check pipe collisions
        for (Pipe pipe : pipes) {
            // Only check pipes that are near the bird
            if (Math.abs(pipe.getX() - BIRD_X) < Pipe.WIDTH + BIRD_SIZE) {
                boolean collision = false;
                
                // Check all corners of the bird sprite
                collision |= pipe.isPointInPipe(BIRD_X, birdY);  // Top-left
                collision |= pipe.isPointInPipe(BIRD_X + BIRD_SIZE, birdY);  // Top-right
                collision |= pipe.isPointInPipe(BIRD_X, birdY + BIRD_SIZE);  // Bottom-left
                collision |= pipe.isPointInPipe(BIRD_X + BIRD_SIZE, birdY + BIRD_SIZE);  // Bottom-right
                
                if (collision) {
                    gamePanel.gameOver();
                    return;
                }
            }
        }
    }
    
    private void updateGamePanel() {
        gamePanel.setPipes(new ArrayList<>(pipes));
        gamePanel.repaint();
    }
    
    public void stopMovement() {
        isRunning = false;
    }
    
    public List<Pipe> getPipes() {
        return new ArrayList<>(pipes);
    }
    
    public void setPipes(List<Pipe> newPipes) {
        pipes.clear();
        pipes.addAll(newPipes);
    }
    
    public void resetGame() {
        pipes.clear();
        initializePipes();
        isRunning = true;
    }
}