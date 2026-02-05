package com.streaming.fb_streamingplatform.service;

import com.streaming.fb_streamingplatform.infrastructure.DatabaseConfig;
import com.streaming.fb_streamingplatform.model.User;
import com.streaming.fb_streamingplatform.repository.UserRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class UserService {
    private final DatabaseConfig config;
    private final UserRepository userRepository;

    public UserService() {
        this.config = new DatabaseConfig();
        this.userRepository = new UserRepository(config);
    }

    public Optional<User> findUserByEmail(String email) {
        if ((email == null) || (email.isBlank()) || (!email.contains("@"))) {
            return Optional.empty();
        }

        try {
            return userRepository.getByEmail(email.trim());
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch user by email", e);
        }
    }

    public void addUser(String email, String name) throws SQLException {
        // check if email is empty
        if (email.trim().isEmpty()) {
            throw new RuntimeException("The email is empty. Please provide your email.");
        }

        // Check if there is an @ in the string
        if (!email.contains("@")) {
            throw new RuntimeException("The email does not contain an '@' and is therefore not valid.");
        }

        userRepository.add(email, name);
    }

    public void removeUserById(int userId){
        if (getByUserId(userId).isEmpty()){
            throw new RuntimeException("No user was found. (ID NOT FOUND)");
        }
        userRepository.removeUserById(userId);
    }

    public Optional<User> getByUserId(int userId) {
        try {
            return userRepository.getByUserId(userId);
        } catch (SQLException e) {
            throw new RuntimeException("Could not fetch user by id" + e);
        }
    }

    public List<User> getUsers() {
        try {
            return userRepository.getUsers();
        } catch (SQLException e) {
            throw new RuntimeException("Could not fetch users" + e);
        }
    }

    public List<User> searchUsersByEmail(String query) {
        try {
            List<User> allUsers = userRepository.getUsers();

            if (query == null || query.isBlank()) {
                return allUsers;
            }

            String q = query.toLowerCase();

            return allUsers.stream()
                    .filter(u -> u.getEmail() != null && u.getEmail().toLowerCase().contains(q))
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException("Could not load users.", e);
        }
    }
}