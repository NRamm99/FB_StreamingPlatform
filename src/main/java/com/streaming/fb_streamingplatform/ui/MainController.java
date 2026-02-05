package com.streaming.fb_streamingplatform.ui;

import com.streaming.fb_streamingplatform.model.Movie;
import com.streaming.fb_streamingplatform.model.User;
import com.streaming.fb_streamingplatform.service.FavoriteService;
import com.streaming.fb_streamingplatform.service.MovieService;
import com.streaming.fb_streamingplatform.service.UserService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.geometry.Insets;

public class MainController {
    private final MovieService movieService = new MovieService();
    private final FavoriteService favoriteService = new FavoriteService();
    private final UserService userService = new UserService();

    private final ObservableList<Movie> movies = FXCollections.observableArrayList();
    private final ObservableList<Movie> favorites = FXCollections.observableArrayList();
    private final ObservableList<User> users = FXCollections.observableArrayList();

    @FXML
    private Label activeUserLabel;

    private User activeUser;

    @FXML
    private ListView<Movie> movieListView;
    @FXML
    private ListView<Movie> favoriteMovieListView;
    @FXML
    private ListView<User> userListView;

    @FXML
    private TextField movieSearchField;
    @FXML
    private TextField userSearchField;


    @FXML
    private void initialize() {
        // Movie list init
        movieListView.setItems(movies);
        movies.addAll(movieService.getMovies());

        // User list init
        userListView.setItems(users);
        userListView.setPlaceholder(
                new Label("No users found")
        );
        users.addAll(userService.getUsers());

        // Favorite list init
        favoriteMovieListView.setItems(favorites);
        // init placeholder in list view - This is our "status"
        favoriteMovieListView.setPlaceholder(
                new Label("Select a user to see favorites")
        );


        // Favorite list "listen for click"
        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, newUser) -> {
            if (newUser == null) return;
            setActiveUser(newUser);
        });

        // User search as-you-type (by email)
        userSearchField.textProperty().addListener((obs, oldText, newText) -> {
            try {
                users.setAll(userService.searchUsersByEmail(newText));

                //Placeholder uipdate
                if (users.isEmpty()) {
                    userListView.setPlaceholder(
                            new Label("No users found")
                    );
                }

            } catch (RuntimeException e) {
                showError(e.getMessage());
            }
        });
    }

    @FXML
    private void sortByRating() {
        movies.clear();
        movies.addAll(movieService.getMoviesSortedByRating());
    }

    @FXML
    private void searchMovies() {
        try {
            movies.clear();
            movies.addAll(movieService.searchMovies(movieSearchField.getText()));
        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    // Helper methjod :)
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Something went wrong");
        alert.setContentText(message);
        alert.showAndWait();
    }

    // This is for the User list - so we can update the fav list by taking the active user that is selected
    private void setActiveUser(User user) {
        this.activeUser = user;

        activeUserLabel.setText("Active user: " + user.getName()); // or getEmail()

        try {
            favorites.clear();
            favorites.addAll(favoriteService.getFavoritesByUserId(user.getId()));

            // Checks if the user has no favs - and if so - set placeholder to indicate in list view
            if (favorites.isEmpty()) {
                favoriteMovieListView.setPlaceholder(
                        new Label(user.getName() + " currently has no favorites")
                );
            }

        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void addFavorite() {
        // 1) validate active user
        if (activeUser == null) {
            showError("Select a user first.");
            return;
        }

        // 2) validate selected movie
        Movie selectedMovie = movieListView.getSelectionModel().getSelectedItem();
        if (selectedMovie == null) {
            showError("Select a movie first.");
            return;
        }

        try {
            // 3) call service
            favoriteService.addFavorite(activeUser.getId(), selectedMovie.getId());

            // 4) refresh favorites list for the active user
            favorites.setAll(favoriteService.getFavoritesByUserId(activeUser.getId()));

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }

    @FXML
    private void removeFavorite() {
        // validate active user
        if (activeUser == null) {
            showError("Select a user first.");
            return;
        }

        // validate selected movie
        Movie selectedMovie = favoriteMovieListView.getSelectionModel().getSelectedItem();
        if (selectedMovie == null) {
            showError("Select a favorite first.");
            return;
        }

        try {
            // call service
            favoriteService.removeFavorite(activeUser.getId(), selectedMovie.getId());

            // refresh favorites list for the active user
            favorites.setAll(favoriteService.getFavoritesByUserId(activeUser.getId()));

            // If the user just removed the last fav - now it will display the placeholder
            if (favorites.isEmpty()) {
                favoriteMovieListView.setPlaceholder(
                        new Label(activeUser.getName() + " currently has no favorites")
                );
            }

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }


    @FXML
    private void openAddUserDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add user");
        dialog.setHeaderText("Enter name and email");

        TextField nameField = new TextField();
        nameField.setPromptText("Name");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10));
        grid.addRow(0, new Label("Name:"), nameField);
        grid.addRow(1, new Label("Email:"), emailField);

        dialog.getDialogPane().setContent(grid);

        ButtonType okBtn = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(okBtn, ButtonType.CANCEL);

        // Disable OK until both fields have something
        var okNode = dialog.getDialogPane().lookupButton(okBtn);
        okNode.setDisable(true);

        Runnable validate = () -> okNode.setDisable(
                nameField.getText().isBlank() || emailField.getText().isBlank()
        );

        nameField.textProperty().addListener((a, b, c) -> validate.run());
        emailField.textProperty().addListener((a, b, c) -> validate.run());
        validate.run();

        dialog.showAndWait().ifPresent(result -> {
            if (result != okBtn) return;

            String name = nameField.getText().trim();
            String email = emailField.getText().trim();

            try {
                userService.addUser(email, name);

                // refresh user list
                users.setAll(userService.getUsers());

            } catch (Exception e) {
                showError(e.getMessage());
            }
        });
    }

    @FXML
    private void removeUser() {
        // 1) validate active user
        if (activeUser == null) {
            showError("Select a user first.");
            return;
        }

        try {
            // 3) call service
            userService.removeUserById(activeUser.getId());

            // 4) refresh favorites list for the active user
            users.setAll(userService.getUsers());

        } catch (Exception e) {
            showError(e.getMessage());
        }
    }


}
