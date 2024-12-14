package game;

public class FlappyMovements extends Thread implements GameInitializable {
    private volatile boolean isRunning = true;
    private volatile int birdY = 300;
    private volatile double velocity = 0;
    private final double gravity = 0.5;
    private final double jumpStrength = -8;
    private GamePanel gamePanel;
    private boolean isInitialState = true; // Implementasi flag dari interface

    public FlappyMovements(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    @Override
    public void initialize() {
        // Set burung dalam kondisi awal
        isInitialState = true;
        birdY = 300;
        velocity = 0;
    }

    @Override
    public void startMovement() {
        // Mulai gerakan setelah loncatan pertama
        isInitialState = false;
    }

    @Override
    public boolean isInitialState() {
        return isInitialState;
    }

    public void jump() {
        if (isInitialState) {
            startMovement(); // Aktifkan gerakan saat loncatan pertama
        }
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
            // Hanya terapkan gravitasi jika tidak dalam kondisi awal
            if (!isInitialState) {
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
            }

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
        initialize(); // Kembalikan ke kondisi awal
    }
}