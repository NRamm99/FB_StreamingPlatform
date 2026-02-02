package com.streaming.fb_streamingplatform.repository;

import com.streaming.fb_streamingplatform.infrastructure.DatabaseConfig;
import com.streaming.fb_streamingplatform.model.Movie;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
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

    public List<Movie> getMovies() throws SQLException {
        List<Movie> movies = new ArrayList<>();
        Movie temp;

        String sql = "SELECT id, title, rating FROM movies";

        try (var conn = config.getConnection();
             var stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                temp = new Movie(rs.getInt("id"), rs.getString("title"), rs.getDouble("rating"));
                movies.add(temp);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return movies;
    }
}
