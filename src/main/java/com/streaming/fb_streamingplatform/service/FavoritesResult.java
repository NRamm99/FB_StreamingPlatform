package com.streaming.fb_streamingplatform.service;

import com.streaming.fb_streamingplatform.model.Movie;
import java.util.List;

public class FavoritesResult {

    private final List<Movie> favorites;
    private final String status;

    public FavoritesResult(List<Movie> favorites, String status){
        this.favorites = favorites;
        this.status = status;
    }

    public List<Movie> getFavorites() {
        return favorites;
    }

    public String getStatus() {
        return status;
    }
}
