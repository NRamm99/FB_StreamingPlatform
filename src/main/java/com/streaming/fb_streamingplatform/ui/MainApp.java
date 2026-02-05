package com.streaming.fb_streamingplatform.ui;

import com.streaming.fb_streamingplatform.infrastructure.DatabaseConfig;
import com.streaming.fb_streamingplatform.infrastructure.DbInit;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        DatabaseConfig config = new DatabaseConfig();
        DbInit.init(config);

        FXMLLoader fxmlLoader = new FXMLLoader(MainApp.class.getResource("/ui/hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800 , 600);
        stage.setTitle("Best streaming platform in the world");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();
    }
}
