package game;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class PipeMovements extends Thread {
    private static final int INITIAL_PIPE_COUNT = 3;
    private static final int PIPE_SPACING = 300;
    private static final int INITIAL_X = 800;
    private static final int BIRD_X = 100;
    private static final int BIRD_SIZE = 30;
    private static final long FRAME_TIME = 16;
    
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
        int rightmostX = Integer.MIN_VALUE;
        for (Pipe pipe : pipes) {
            rightmostX = Math.max(rightmostX, pipe.getX());
        }
        
        for (Pipe pipe : pipes) {
            pipe.move();
            
            if (pipe.needsScoring(BIRD_X) && gamePanel.isGameActive()) {
                gamePanel.incrementScore();
            }
            
            if (pipe.isOffScreen()) {
                int newX = rightmostX + PIPE_SPACING;
                pipe.reset(newX);
                rightmostX = newX; 
            }
        }
    }
    
    private void checkCollisions() {
        if (!gamePanel.isGameActive()) return;
        
        int birdY = gamePanel.getBirdY();
        
        if (birdY < 0 || birdY + BIRD_SIZE > gamePanel.getHeight()) {
            gamePanel.gameOver();
            return;
        }
        
        for (Pipe pipe : pipes) {
            if (Math.abs(pipe.getX() - BIRD_X) < Pipe.WIDTH + BIRD_SIZE) {
                if (checkBirdCollision(pipe, birdY)) {
                    gamePanel.gameOver();
                    return;
                }
            }
        }
    }
    
    private boolean checkBirdCollision(Pipe pipe, int birdY) {
        int[] corners = {
            birdY,
            birdY + BIRD_SIZE,
            birdY + (BIRD_SIZE / 2)
        };
        
        for (int y : corners) {
            if (pipe.isPointInPipe(BIRD_X, y) || 
                pipe.isPointInPipe(BIRD_X + BIRD_SIZE, y)) {
                return true;
            }
        }
        return false;
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