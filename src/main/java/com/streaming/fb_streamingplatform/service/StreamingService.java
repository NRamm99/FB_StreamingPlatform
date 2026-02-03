package com.streaming.fb_streamingplatform.service;

import com.streaming.fb_streamingplatform.infrastructure.DatabaseConfig;
import com.streaming.fb_streamingplatform.model.Movie;
import com.streaming.fb_streamingplatform.model.User;
import com.streaming.fb_streamingplatform.repository.FavoriteRepository;
import com.streaming.fb_streamingplatform.repository.MovieRepository;
import com.streaming.fb_streamingplatform.repository.UserRepository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class StreamingService {

    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final MovieRepository movieRepository;

    public StreamingService() {
        DatabaseConfig config = new DatabaseConfig();
        this.userRepository = new UserRepository(config);
        this.favoriteRepository = new FavoriteRepository(config);
        this.movieRepository = new MovieRepository(config);
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

    public void addFavorite(int userId, int movieId) throws Exception {
        // Check if user exists
        if (userRepository.getByUserId(userId).isEmpty()) {
            throw new Exception("User with that ID does not exist");
        }

        // Check if movie exists
        if (movieRepository.getByMovieId(movieId).isEmpty()) {
            throw new Exception("Movie with that ID does not exist");
        }

        // Check if user already have that favorite movie
        List<Movie> favMovies = favoriteRepository.getFavoritesByUserId(userId);

            for(Movie mov : favMovies) {
                if (mov.getId() == movieId) {
                    throw new Exception("Movie is already favorite");
                }
            }
        favoriteRepository.add(userId, movieId);
    }
}
