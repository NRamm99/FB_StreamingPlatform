package com.streaming.fb_streamingplatform.ui;


import com.streaming.fb_streamingplatform.infrastructure.DatabaseConfig;
import com.streaming.fb_streamingplatform.repository.UserRepository;
import javafx.application.Application;

import java.sql.SQLException;

public class Launcher {

    public static void main(String[] args) {
        Application.launch(MainApp.class, args);

        DatabaseConfig config = new DatabaseConfig();
        UserRepository ur = new UserRepository(config);

        try {
            System.out.println(ur.getByEmail("alice@mail.com"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
