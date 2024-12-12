package config;

import model.Score;
import java.sql.*;

public class ScoreDAO {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/db_flappybird";
    private static final String USER = "root";
    private static final String PASS = "";

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, USER, PASS);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found.", e);
        }
    }

    public boolean initializeUserScore(Score score) throws SQLException {
        String insertQuery = "INSERT INTO score (id, score, game_duration, date_time) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, score.getUserId());
            stmt.setInt(2, score.getScore());
            stmt.setInt(3, score.getGameDuration());
            stmt.setTimestamp(4, Timestamp.valueOf(score.getDateTime()));
            
            int affectedRows = stmt.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        score.setScoreId(generatedKeys.getInt(1));
                        return true;
                    }
                }
            }
            return false;
        }
    }

    public boolean updateScore(Score score) throws SQLException {
        String updateQuery = "UPDATE score SET score = ?, game_duration = ?, date_time = ? WHERE score_id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(updateQuery)) {
            
            stmt.setInt(1, score.getScore());
            stmt.setInt(2, score.getGameDuration());
            stmt.setTimestamp(3, Timestamp.valueOf(score.getDateTime()));
            stmt.setInt(4, score.getScoreId());
            
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        }
    }

    public Score getHighestScoreByUserId(int userId) throws SQLException {
        String query = "SELECT * FROM score WHERE id = ? ORDER BY score DESC LIMIT 1";
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Score(
                        rs.getInt("score_id"),
                        rs.getInt("id"),
                        rs.getInt("score"),
                        rs.getInt("game_duration"),
                        rs.getTimestamp("date_time").toLocalDateTime()
                    );
                }
            }
        }
        return null;
    }
    
    public java.util.List<Score> getAllScores() throws SQLException {
        String query = "SELECT * FROM score ORDER BY score DESC"; 
        java.util.List<Score> scores = new java.util.ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    scores.add(new Score(
                        rs.getInt("score_id"),
                        rs.getInt("id"),
                        rs.getInt("score"),
                        rs.getInt("game_duration"),
                        rs.getTimestamp("date_time").toLocalDateTime()
                    ));
                }
            }
        }
        return scores;
    }
    public java.util.List<Score> getTopScores(int limit) throws SQLException {
        String query = "SELECT * FROM score ORDER BY score DESC LIMIT ?";
        java.util.List<Score> scores = new java.util.ArrayList<>();
        
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            
            stmt.setInt(1, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    scores.add(new Score(
                        rs.getInt("score_id"),
                        rs.getInt("id"),
                        rs.getInt("score"),
                        rs.getInt("game_duration"),
                        rs.getTimestamp("date_time").toLocalDateTime()
                    ));
                }
            }
        }
        return scores;
    }
}