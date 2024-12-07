package game;

public class Bird {
    private int x;
    private int y;
    private final int width = 30;
    private final int height = 30;
    private double velocityY;
    private final double gravity = 0.5;
    private final double jumpStrength = -8;
    private boolean isDead = false;

    public Bird(int initialX, int initialY) {
        this.x = initialX;
        this.y = initialY;
        this.velocityY = 0;
    }

    public void update() {
        if (!isDead) {
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
    }
}