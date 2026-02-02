module com.streaming.fb_streamingplatform {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.streaming.fb_streamingplatform to javafx.fxml;
    exports com.streaming.fb_streamingplatform;
}