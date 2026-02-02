package com.streaming.fb_streamingplatform.repository;

import com.streaming.fb_streamingplatform.infrastructure.DatabaseConfig;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MovieRepository {
    private final DatabaseConfig config;

    public MovieRepository(DatabaseConfig config) {
        this.config = config;
    }

    public int getMovieCount() throws SQLException {
        String sql = "SELECT COUNT(*) FROM movies";

        try (var conn = config.getConnection();
             var stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                return rs.getInt(1);
            }

            return 0;
        }
    }

    public List<String> getMovies() throws SQLException {
        String sql = "SELECT title FROM movies";

        try (var conn = config.getConnection();
             var stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                System.out.println(rs.getString("title"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
