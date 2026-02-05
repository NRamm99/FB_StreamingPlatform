package com.streaming.fb_streamingplatform.service;

import com.streaming.fb_streamingplatform.infrastructure.DatabaseConfig;
import com.streaming.fb_streamingplatform.model.Favorite;
import com.streaming.fb_streamingplatform.model.Movie;
import com.streaming.fb_streamingplatform.model.User;
import com.streaming.fb_streamingplatform.repository.FavoriteRepository;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FavoriteService {
    private final DatabaseConfig config;
    private final FavoriteRepository favoriteRepository;
    private final UserService userService;
    private final MovieService movieService;

    public FavoriteService() {
        this.config = new DatabaseConfig();
        this.favoriteRepository = new FavoriteRepository(config);

        this.userService = new UserService();
        this.movieService = new MovieService();


    }

    public void addFavorite(int userId, int movieId) throws Exception {

        // Check if user exists
        if (userService.getByUserId(userId).isEmpty()) {
            throw new RuntimeException("User with that ID does not exist");
        }

        // Check if movie exists
        if (movieService.getByMovieId(movieId).isEmpty()) {
            throw new RuntimeException("Movie with that ID does not exist");
        }

        // Gets user's favorites
        List<Movie> favMovies = favoriteRepository.getFavoritesByUserId(userId);

        // check if selected movie is already a favorite
        for (Movie mov : favMovies) {
            if (mov.getId() == movieId) {
                throw new RuntimeException("Movie is already a favorite");
            }
        }

        // Add favorite
        favoriteRepository.add(userId, movieId);

    }


    public void removeFavorite(int userId, int movieId) throws Exception {
        // check if user exists
        if (userService.getByUserId(userId).isEmpty()) {
            throw new Exception("User with that ID does not exist");
        }

        // check if movie exists
        if (movieService.getByMovieId(movieId).isEmpty()) {
            throw new Exception("Movie with that ID does not exist");
        }

        // check if user already have that favorite movie
        List<Movie> favMovies = favoriteRepository.getFavoritesByUserId(userId);

        // check if user has no favorites
        if (favMovies.isEmpty()) {
            throw new Exception("The user has no favorites to remove.");
        }

        // check if selected movie is a favorite
        for (Movie mov : favMovies) {
            if (mov.getId() == movieId) {
                favoriteRepository.remove(userId, movieId);
                return;
            }
        }

        throw new Exception("The user does not have the selected movie as a favorite.");
    }

    public List<Movie> getFavoritesByUserId(int userId) {
        try {
            return favoriteRepository.getFavoritesByUserId(userId);
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not load favorites for user " + userId, e);
        }
    }
}
