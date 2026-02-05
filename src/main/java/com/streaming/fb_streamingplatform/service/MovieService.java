package com.streaming.fb_streamingplatform.service;

import com.streaming.fb_streamingplatform.infrastructure.DatabaseConfig;
import com.streaming.fb_streamingplatform.model.Movie;
import com.streaming.fb_streamingplatform.repository.MovieRepository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class MovieService {
    private final DatabaseConfig config;
    private final MovieRepository movieRepository;

    public MovieService() {
        this.config = new DatabaseConfig();
        this.movieRepository = new MovieRepository(config);
    }

    public List<Movie> getMoviesSortedByRating() {
        try {
            return movieRepository.getMovieSortedByRating();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch movies sorted by rating", e);
        }
    }

    public List<Movie> getMovies() {
        try {
            return movieRepository.getMovies();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to fetch movies" + e);
        }
    }

    public List<Movie> searchMovies(String query) {
        try {
            List<Movie> movies = movieRepository.getMovies();

            if (query == null || query.isBlank()) {
                return movies;
            }

            String lowerQuery = query.toLowerCase();

            return movies.stream()
                    .filter(movie ->
                            movie.getTitle().toLowerCase().contains(lowerQuery)
                    )
                    .toList();

        } catch (SQLException e) {
            throw new RuntimeException("Could not load movies", e);
        }
    }

    public Optional<Movie> getByMovieId(int movieId) {
        try {
            return movieRepository.getByMovieId(movieId);
        } catch (SQLException e) {
            throw new RuntimeException("Could not fetch movie by id" + e);
        }
    }
}
