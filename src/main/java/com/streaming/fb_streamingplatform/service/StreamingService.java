package com.streaming.fb_streamingplatform.service;

import com.streaming.fb_streamingplatform.infrastructure.DatabaseConfig;
import com.streaming.fb_streamingplatform.model.Movie;
import com.streaming.fb_streamingplatform.model.User;
import com.streaming.fb_streamingplatform.repository.FavoriteRepository;
import com.streaming.fb_streamingplatform.repository.MovieRepository;
import com.streaming.fb_streamingplatform.repository.UserRepository;

import java.sql.SQLException;
import java.util.Collections;
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


    // USER

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


    // FAVORITE

    public List<Movie> addFavorite(int userId, int movieId) throws Exception {

        // Check if user exists
        if (userRepository.getByUserId(userId).isEmpty()) {
            throw new Exception("User with that ID does not exist");
        }

        // Check if movie exists
        if (movieRepository.getByMovieId(movieId).isEmpty()) {
            throw new Exception("Movie with that ID does not exist");
        }

        // Gets user's favorites
        List<Movie> favMovies = favoriteRepository.getFavoritesByUserId(userId);

        // check if selected movie is already a favorite
        for (Movie mov : favMovies) {
            if (mov.getId() == movieId) {
                throw new Exception("Movie is already a favorite");
            }
        }

        // Add favorite
        favoriteRepository.add(userId, movieId);

        //Return updated favorites list
        return favoriteRepository.getFavoritesByUserId(userId);
    }

    public List<Movie> getMoviesSortedByRating() {
        try {
            return movieRepository.getMovieSortedByRating();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch movies sorted by rating", e);
        }
    }
      

    public List<Movie> removeFavorite(int userId, int movieId) throws Exception {
        // check if user exists
        if (userRepository.getByUserId(userId).isEmpty()) {
            throw new Exception("User with that ID does not exist");
        }

        // check if movie exists
        if (movieRepository.getByMovieId(movieId).isEmpty()) {
            throw new Exception("Movie with that ID does not exist");
        }

        // check if user already have that favorite movie
        List<Movie> favMovies = favoriteRepository.getFavoritesByUserId(userId);

        // check if user has no favorites
        if (favMovies.isEmpty()){
            throw new Exception("The user has no favorites to remove.");
        }

        // check if selected movie is a favorite
        for (Movie mov : favMovies) {
            if (mov.getId() == movieId) {
                favoriteRepository.remove(userId, movieId);
                return favoriteRepository.getFavoritesByUserId(userId);
            }
        }

        throw new Exception("The user does not have the selected movie as a favorite.");
    }

    public FavoritesResult findFavoritesByEmail(String email){

        if (email == null || email.isBlank() || !email.contains("@")){
            return new FavoritesResult(Collections.emptyList(), "Invalid email input");
        }

        Optional<User> userOpt = findUserByEmail(email.trim());

        if (userOpt.isEmpty()){
            return new FavoritesResult(Collections.emptyList(), "No user found");
        }

        List<Movie> favorites = favoriteRepository.getFavoritesByUserId(userOpt.get().getId());

        if (favorites.isEmpty()){
            return new FavoritesResult(Collections.emptyList(), "No favorites");

        }

        return new FavoritesResult(favorites, "Loaded " + favorites.size() + " favorites");
    }
}
