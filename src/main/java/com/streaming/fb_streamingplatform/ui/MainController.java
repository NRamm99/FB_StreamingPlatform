package com.streaming.fb_streamingplatform.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class MainController {
    @FXML
    private Label welcomeText;

    // SLET VENLIGST IKKE!!!
    // Det her er hvordan vi handler Optional i UI laget - og returnerer noget brugervenligt til brugeren. bemærkj, at det næsten er ligesom en alm if else!
//    @FXML
//    private void onSearchUser() {
//        String email = emailField.getText();
//
//        Optional<User> userOpt = streamingService.findUserByEmail(email);
//
//        userOpt.ifPresentOrElse(
//                user -> loadFavorites(user),
//                () -> showStatus("No user found")
//        );
//    }


}
