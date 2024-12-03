package game;

public class Pipe {
    // Constants
    public static final int WIDTH = 60;
    public static final int GAP_HEIGHT = 200;
    public static final int MIN_GAP_START = 100;
    public static final int MAX_GAP_START = 400;
    public static final int MOVEMENT_SPEED = 2;
    
    private int x;
    private int gapStart;  // Y position where the gap starts
    private boolean passed;  // Track if bird has passed this pipe for scoring
    
    public Pipe(int startX) {
        this.x = startX;
        randomizeGapPosition();
        this.passed = false;
    }
    
    public void move() {
        x -= MOVEMENT_SPEED;
    }
    
    public boolean isOffScreen() {
        return x + WIDTH < 0;
    }
    
    public boolean needsScoring(int birdX) {
        if (!passed && birdX > x + WIDTH) {
            passed = true;
            return true;
        }
        return false;
    }
    
    public int getX() {
        return x;
    }
    
    public int getGapStart() {
        return gapStart;
    }
    
    public int getWidth() {
        return WIDTH;
    }
    
    public boolean isPassed() {
        return passed;
    }
    
    public void setPassed(boolean passed) {
        this.passed = passed;
    }
    
    public boolean isPointInPipe(int pointX, int pointY) {
        if (pointX >= x && pointX <= x + WIDTH) {
            return pointY <= gapStart || pointY >= gapStart + GAP_HEIGHT;
        }
        return false;
    }
    
    public void randomizeGapPosition() {
        this.gapStart = MIN_GAP_START + (int)(Math.random() * (MAX_GAP_START - MIN_GAP_START));
    }
    
    public void reset(int newX) {
        this.x = newX;
        this.passed = false;
        randomizeGapPosition();
    }
}