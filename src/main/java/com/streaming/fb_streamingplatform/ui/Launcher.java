package com.streaming.fb_streamingplatform.ui;


import com.streaming.fb_streamingplatform.repository.FavoriteRepository;
import com.streaming.fb_streamingplatform.service.StreamingService;
import javafx.application.Application;

import java.sql.SQLException;

public class Launcher {

    public static void main(String[] args) throws SQLException {
        // Application.launch(MainApp.class, args);
        StreamingService streamingService = new StreamingService();
        streamingService.addFavorite(5, 5);
    }
}
