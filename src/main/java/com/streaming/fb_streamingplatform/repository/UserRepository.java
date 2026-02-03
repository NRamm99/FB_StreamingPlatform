package com.streaming.fb_streamingplatform.repository;

import com.streaming.fb_streamingplatform.infrastructure.DatabaseConfig;
import com.streaming.fb_streamingplatform.model.User;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class UserRepository {
    private final DatabaseConfig config;

    public UserRepository(DatabaseConfig config) {
        this.config = config;
    }

    private Optional<User> mapUser(ResultSet rs) throws SQLException {
        return Optional.of(new User(rs.getInt("id"), rs.getString("email"), rs.getString("name")));
    }

    // Optional skulle være det nye drengene! Google det - det er ret cool!
    // - Ramm
    public Optional<User> getByEmail(String email) throws SQLException {
        String sql = "SELECT id, email, name FROM users WHERE email = ? ;";

        try (var conn = config.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapUser(rs);
            }
        }
        return Optional.empty();
    }

    public Optional<User> getByUserId(int userId) throws SQLException {
        String sql = "SELECT id, email, name FROM users WHERE id = ?;";

        try (var conn = config.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return mapUser(rs);
            }
        }
        return Optional.empty();
    }

    public void add(String email, String name) throws SQLException {
        String sql = """
                INSERT INTO users (email, name)
                VALUES (?, ?);
                """;

        try (var conn = config.getConnection();
             var stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, email);
            stmt.setString(2, name);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException(e);
        }

    }

}
