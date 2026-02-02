package com.streaming.fb_streamingplatform.service;

import com.streaming.fb_streamingplatform.model.User;
import com.streaming.fb_streamingplatform.repository.UserRepository;

import java.sql.SQLException;
import java.util.Optional;

public class StreamingService {

    private final UserRepository userRepository;

    public StreamingService(UserRepository userRepository) {
        this.userRepository = userRepository;
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
}
