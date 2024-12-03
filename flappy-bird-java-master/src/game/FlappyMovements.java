// package game;

// public class FlappyMovements extends Thread {
//     private volatile boolean isRunning = true;
//     private volatile double birdY = 250;
//     private volatile double velocity = 0;
//     private final double gravity = 0.4;
//     private final double jumpStrength = -8;
//     private GamePanel gamePanel;
    
//     public FlappyMovements(GamePanel gamePanel) {
//         this.gamePanel = gamePanel;
//     }
    
//     public void jump() {
//         velocity = jumpStrength;
//     }
    
//     @Override
//     public void run() {
//         while (isRunning) {
//             velocity += gravity;
//             birdY += velocity;
            
//             // Boundary checks
//             if (birdY < 0) {
//                 birdY = 0;
//                 velocity = 0;
//             }
//             if (birdY > 470) {  // Ground collision
//                 birdY = 470;
//                 gamePanel.gameOver();
//             }
            
//             gamePanel.setBirdY((int)birdY);
//             gamePanel.repaint();
            
//             try {
//                 Thread.sleep(16);  // ~60 FPS
//             } catch (InterruptedException e) {
//                 Thread.currentThread().interrupt();
//                 break;
//             }
//         }
//     }
    
//     public void stopMovement() {
//         isRunning = false;
//     }
// }


package game;

public class FlappyMovements extends Thread {
    private volatile boolean isRunning = true;
    private volatile int birdY = 300;
    private volatile double velocity = 0;
    private final double gravity = 0.5;
    private final double jumpStrength = -8;
    private GamePanel gamePanel;

    public FlappyMovements(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void jump() {
        velocity = jumpStrength;
    }

    public int getBirdY() {
        return (int) birdY;
    }

    public void setBirdY(int birdY) {
        this.birdY = birdY;
    }

    @Override
    public void run() {
        while (isRunning) {
            velocity += gravity;
            birdY += velocity;

            // Boundary checks
            if (birdY < 0) {
                birdY = 0;
                velocity = 0;
            }
            if (birdY > 570) {  // Ground collision
                birdY = 570;
                gamePanel.gameOver();
            }

            gamePanel.setBirdY((int) birdY);
            gamePanel.repaint();

            try {
                Thread.sleep(16);  // ~60 FPS
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void stopMovement() {
        isRunning = false;
    }

    public void reset() {
        birdY = 300;
        velocity = 0;
        isRunning = true;
    }
}