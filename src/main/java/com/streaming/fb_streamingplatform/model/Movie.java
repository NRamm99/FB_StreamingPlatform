package com.streaming.fb_streamingplatform.model;

public class Movie {
    int id;
    String title;
    double rating;

    public Movie(int id, String title, double rating){
        this.id = id;
        this.title = title;
        this.rating = rating;
    }

    public int getId() {
        return id;
    }

    public double getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public String toString(){
        return "#" + id + " | " + title + " | Rating: " + rating;
    }
}
