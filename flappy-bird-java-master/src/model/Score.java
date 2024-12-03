// package model;

// import java.time.LocalDateTime;

// public class Score {
//     private int scoreId;
//     private int userId;
//     private int score;
//     private int gameDuration;
//     private LocalDateTime dateTime;
    
//     public Score(int scoreId, int userId, int score, int gameDuration, LocalDateTime dateTime) {
//         this.scoreId = scoreId;
//         this.userId = userId;
//         this.score = score;
//         this.gameDuration = gameDuration;
//         this.dateTime = dateTime;
//     }
    
//     // Constructor for new score entry (without scoreId)
//     public Score(int userId, int score, int gameDuration) {
//         this.userId = userId;
//         this.score = score;
//         this.gameDuration = gameDuration;
//         this.dateTime = LocalDateTime.now();
//     }
    
//     // Getters and Setters
//     public int getScoreId() {
//         return scoreId;
//     }
    
//     public void setScoreId(int scoreId) {
//         this.scoreId = scoreId;
//     }
    
//     public int getUserId() {
//         return userId;
//     }
    
//     public void setUserId(int userId) {
//         this.userId = userId;
//     }
    
//     public int getScore() {
//         return score;
//     }
    
//     public void setScore(int score) {
//         this.score = score;
//     }
    
//     public int getGameDuration() {
//         return gameDuration;
//     }
    
//     public void setGameDuration(int gameDuration) {
//         this.gameDuration = gameDuration;
//     }
    
//     public LocalDateTime getDateTime() {
//         return dateTime;
//     }
    
//     public void setDateTime(LocalDateTime dateTime) {
//         this.dateTime = dateTime;
//     }
// }

package model;

import java.time.LocalDateTime;

public class Score {
    private int scoreId;
    private int userId;
    private int score;
    private int gameDuration;
    private LocalDateTime dateTime;
    
    public Score(int scoreId, int userId, int score, int gameDuration, LocalDateTime dateTime) {
        this.scoreId = scoreId;
        this.userId = userId;
        this.score = score;
        this.gameDuration = gameDuration;
        this.dateTime = dateTime;
    }
    
    public Score(int userId, int score, int gameDuration) {
        this.userId = userId;
        this.score = score;
        this.gameDuration = gameDuration;
        this.dateTime = LocalDateTime.now();
    }
    
    // Full getters and setters
    public int getScoreId() { return scoreId; }
    public void setScoreId(int scoreId) { this.scoreId = scoreId; }
    
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }
    
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    
    public int getGameDuration() { return gameDuration; }
    public void setGameDuration(int gameDuration) { this.gameDuration = gameDuration; }
    
    public LocalDateTime getDateTime() { return dateTime; }
    public void setDateTime(LocalDateTime dateTime) { this.dateTime = dateTime; }
}