package game;

public class Bird implements GameInitializable {
    private int x;
    private int y;
    private final int width = 30;
    private final int height = 30;
    private double velocityY;
    private final double gravity = 0.5;
    private final double jumpStrength = -8;
    private boolean isDead = false;
    private boolean isInitialState = true; // Implementasi flag dari interface

    public Bird(int initialX, int initialY) {
        this.x = initialX;
        this.y = initialY;
        this.velocityY = 0;
    }

    @Override
    public void initialize() {
        // Set burung dalam kondisi awal
        isInitialState = true;
        y = 300;
        velocityY = 0;
        isDead = false;
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

    public void update() {
        if (!isDead && !isInitialState) { // Tambahkan kondisi !isInitialState
            velocityY += gravity;
            y += velocityY;

            // Boundary checks
            if (y < 0) {
                y = 0;
                velocityY = 0;
            }
            if (y > 570) {  // Ground collision
                y = 570;
                isDead = true;
            }
        }
    }

    public void jump() {
        if (!isDead) {
            if (isInitialState) {
                startMovement(); // Aktifkan gerakan saat loncatan pertama
            }
            velocityY = jumpStrength;
        }
    }

    // Getters and setters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public void reset(int initialY) {
        this.y = initialY;
        this.velocityY = 0;
        this.isDead = false;
        initialize(); // Kembalikan ke kondisi awal
    }
}