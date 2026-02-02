module com.streaming.fb_streamingplatform {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    exports com.streaming.fb_streamingplatform.ui;
    opens com.streaming.fb_streamingplatform.ui to javafx.fxml;
}