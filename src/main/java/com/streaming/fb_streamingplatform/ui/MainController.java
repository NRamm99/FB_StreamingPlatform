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
        // Bind lists
        movieListView.setItems(movies);
        userListView.setItems(users);
        favoriteMovieListView.setItems(favorites);

        // Default placeholders
        userListView.setPlaceholder(new Label("No users found"));
        favoriteMovieListView.setPlaceholder(new Label("Select a user to see favorites"));

        // Load initial data safely (so app still opens if db is down)
        try {
            movies.setAll(movieService.getMovies());
            users.setAll(userService.getUsers());
        } catch (RuntimeException e) {
            System.err.println("DB ERROR: " + e.getMessage());
            showError("Could not connect to MySQL.\nIs the server running?");

            // We do this so hthat the app will still run if the database is down
            movies.clear();
            users.clear();
            favorites.clear();

            // update placeholder while db is down
            userListView.setPlaceholder(new Label("Could not load users (DB offline)"));
            favoriteMovieListView.setPlaceholder(new Label("DB offline"));
            return;
        }

        // User click: set active user + refresh favorites
        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldUser, newUser) -> {
            if (newUser != null) {
                setActiveUser(newUser);
            }
        });

        // Search as you type (email)
        userSearchField.textProperty().addListener((obs, oldText, newText) -> {
            try {
                users.setAll(userService.searchUsersByEmail(newText));

                // placeholder only shows when list is empty
                if (users.isEmpty() && newText != null && !newText.isBlank()) {
                    userListView.setPlaceholder(new Label("No users found"));
                }
            } catch (RuntimeException e) {
                showError("Something went wrong.");
                System.err.println("ERROR: " + e.getMessage());
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
            showError("Something went wrong.");
            System.err.println("ERROR: " + e.getMessage());
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
            showError("That user does not exist.");
            System.err.println("ERROR: " + e.getMessage());
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
            showError("That user or movie does not exist.");
            System.err.println("ERROR: " + e.getMessage());

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
            showError("That user, or favorite movie does not exist.");
            System.err.println("ERROR: " + e.getMessage());

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
                showError("Something went wrong trying to create the user.");
                System.err.println("ERROR: " + e.getMessage());
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
            showError("The user was not found.");
            System.err.println("ERROR: " + e.getMessage());
        }
    }

    @FXML
    private void showTop5() {
        try {
            // Get top 5 (sorted by rating desc)
            var top5 = movieService.getMoviesSortedByRating()
                    .stream()
                    .limit(5)
                    .toList();

            ListView<Movie> topListView = new ListView<>();
            topListView.setItems(FXCollections.observableArrayList(top5));

            Dialog<Void> dialog = new Dialog<>();
            dialog.setTitle("Top 5 Movies");
            dialog.setHeaderText("Top 5 movies by rating");
            dialog.getDialogPane().setContent(topListView);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.OK);
            dialog.getDialogPane().setPrefSize(300, 225);

            dialog.showAndWait();

        } catch (RuntimeException e) {
            showError(e.getMessage());
        }
    }


}
