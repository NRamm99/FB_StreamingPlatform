package com.streaming.fb_streamingplatform.repository;

import com.streaming.fb_streamingplatform.infrastructure.DatabaseConfig;
import com.streaming.fb_streamingplatform.model.Movie;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FavoriteRepository {
    private final DatabaseConfig config;

    public FavoriteRepository(DatabaseConfig config) {
        this.config = config;
    }

    private Movie mapMovie(ResultSet rs) throws SQLException {
        return new Movie(
                rs.getInt("id"),
                rs.getString("title"),
                rs.getDouble("rating")
        );
    }

    public List<Movie> getFavoritesByUserId(int userId) {
        List<Movie> favorites = new ArrayList<>();

        String sql = """
                SELECT m.id, m.title, m.rating
                FROM favorites f
                JOIN movies m ON f.movie_id = m.id
                WHERE f.user_id = ?;
                """;

        try (var conn = config.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    favorites.add(mapMovie(rs));
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException("Failed to load favorites for userId= " + userId + " | " + e);
        }

        return favorites;
    }

    public void add(int userId, int movieId) throws SQLException {
        String sql = """
                INSERT INTO favorites (user_id, movie_id)
                VALUES (?, ?);
                """;

        try (var conn = config.getConnection();
        var stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, movieId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        }
    }
}
