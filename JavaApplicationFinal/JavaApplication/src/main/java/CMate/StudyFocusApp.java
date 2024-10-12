package CMate;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.paint.Color;

import java.io.File;
import java.sql.*;

// Inside your StudyFocusApp class



public class StudyFocusApp extends Application {
    private Scene loginScene, homeScene;
    private TextField usernameField, passwordField;
    private Label statusLabel;
    private String currentUser;
    private Stage stage;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/accounts?useSSL=false&disablePublicKeyRetrieval=true\n";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "root";

    public static void main(String[] args) {
        launch(args);
    }
    private void applyLightTheme(Scene scene) {
        // Set background color
        scene.setFill(Color.WHITE);

        // Customize other UI elements as needed
        // For example, you can set a light gray border for text fields
        String lightGray = "#f0f0f0"; // Light gray color
        String textFieldStyle = "-fx-border-color: " + lightGray + "; -fx-border-width: 1px;";
        usernameField.setStyle(textFieldStyle);
        passwordField.setStyle(textFieldStyle);



        // Set stylesheets for consistent styling
        // Add more stylesheets if necessary
        scene.getStylesheets().add(getClass().getResource("light_theme.css").toExternalForm());
    }
    @Override
    public void start(Stage primaryStage) {
        VBox loginLayout = new VBox(10);
        loginLayout.setPadding(new Insets(20));
        Label titleLabel = new Label("Study Focus App");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        Button loginButton = new Button("Log In");
        loginButton.setOnAction(e -> validateLogin(primaryStage));

        Button createAccountButton = new Button("Create Account");
        createAccountButton.setOnAction(e -> createAccount());

        statusLabel = new Label();
        loginLayout.getChildren().addAll(titleLabel, usernameField, passwordField, loginButton, createAccountButton, statusLabel);
        loginScene = new Scene(loginLayout, 300, 250);

        applyStylesheet(loginScene);

        primaryStage.setScene(loginScene);
        primaryStage.setTitle("Study Focus App");
        primaryStage.show();
        // You can adjust other UI elements accordingly
        // For buttons, you might want a light blue background with dark text
        String buttonStyle = "-fx-background-color: #add8e6; -fx-text-fill: #333333;";
        loginButton.setStyle(buttonStyle);
        createAccountButton.setStyle(buttonStyle);
    }

    private void createAccount() {
        VBox createAccountLayout = new VBox(10);
        createAccountLayout.setPadding(new Insets(20));
        Label titleLabel = new Label("Create Account");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        TextField newUsernameField = new TextField();
        newUsernameField.setPromptText("New Username");
        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");
        Button createButton = new Button("Create");
        createButton.setOnAction(e -> {
            String newUsername = newUsernameField.getText();
            String newPassword = newPasswordField.getText();
            createUser(newUsername, newPassword);
        });
        createAccountLayout.getChildren().addAll(titleLabel, newUsernameField, newPasswordField, createButton);
        Scene createAccountScene = new Scene(createAccountLayout, 300, 200);

        Stage stage = new Stage();
        stage.setScene(createAccountScene);
        stage.setTitle("Create Account");
        stage.show();
    }

    private void createUser(String username, String password) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "INSERT INTO accounts (username, password, points) VALUES (?, ?, ?)";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                preparedStatement.setInt(3, 0);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Account Created", "Account created successfully for " + username);
                } else {
                    showAlert("Account Creation Failed", "Failed to create account.");
                }
            }
        } catch (SQLException ex) {
            showAlert("Error", "An error occurred: " + ex.getMessage());
        }
    }

    private void validateLogin(Stage primaryStage) {
        String username = usernameField.getText();
        usernameField.getStyleClass().add("text-field");
        String password = passwordField.getText();
        passwordField.getStyleClass().add("text-field");

        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT * FROM accounts WHERE username = ? AND password = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, password);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        currentUser = resultSet.getString("username");
                        showAlert("Login Successful", "Welcome, " + currentUser + "!", Alert.AlertType.INFORMATION);
                        switchToHomeScene(primaryStage);
                    } else {
                        statusLabel.setText("Invalid username or password.");
                    }
                }
            }
        } catch (SQLException ex) {
            showAlert("SQL Error", "An SQL error occurred: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType alertType) {
        Platform.runLater(() -> {
            Alert alert = new Alert(alertType);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    private void applyStylesheet(Scene scene) {
        try {
            File cssFile = new File("src/main/java/CMate/styles.css");
            String cssPath = cssFile.toURI().toURL().toExternalForm();
            scene.getStylesheets().add(cssPath);
        } catch (Exception e) {
            String errorMessage = "Failed to load CSS file: " + e.getMessage();
            System.out.println(errorMessage);
            showErrorDialog(errorMessage);
        }
    }

    private void switchToHomeScene(Stage primaryStage) {
        VBox homeLayout = new VBox(10);
        homeLayout.setPadding(new Insets(20));
        Label welcomeLabel = new Label("Welcome, " + currentUser + "!");
        welcomeLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        Button soloLearnButton = new Button("Solo Learn");
        soloLearnButton.getStyleClass().add("solo-learn-button");
        soloLearnButton.setOnAction(e -> startSoloLearn());
        Button relaxMindButton = new Button("Relax Mind");
        relaxMindButton.getStyleClass().add("relax-mind-button");
        relaxMindButton.setOnAction(e -> relaxMind());
        Button leaderboardButton = new Button("Leaderboard");
        leaderboardButton.getStyleClass().add("leaderboard-button");
        leaderboardButton.setOnAction(e -> showLeaderboard());
        Button logoutButton = new Button("Log Out");
        logoutButton.getStyleClass().add("logout-button");
        logoutButton.setOnAction(e -> logout(primaryStage));
        homeLayout.getChildren().addAll(welcomeLabel, soloLearnButton, relaxMindButton, leaderboardButton, logoutButton);
        homeScene = new Scene(homeLayout, 300, 300);

        applyStylesheet(homeScene);

        primaryStage.setScene(homeScene);
    }

    public void startSoloLearn() {
        VBox soloLearnLayout = new VBox(10);
        soloLearnLayout.setPadding(new Insets(20));
        Label titleLabel = new Label("Solo Learning");
        titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        TextField studyTimeField = new TextField();
        studyTimeField.setPromptText("Study Time (minutes)");
        Button startButton = new Button("Start Study Session");
        startButton.setOnAction(e -> {
            int studyTime = Integer.parseInt(studyTimeField.getText());
            startStudySession(studyTime);
            updateUserPoints(currentUser, studyTime);
        });
        soloLearnLayout.getChildren().addAll(titleLabel, studyTimeField, startButton);
        Scene soloLearnScene = new Scene(soloLearnLayout, 300, 200);

        applyStylesheet(soloLearnScene);
        Stage stage = new Stage();
        stage.setScene(soloLearnScene);
        stage.show();
    }

    private void updateUserPoints(String username, int studyTimeInMinutes) {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String updateQuery = "UPDATE accounts SET points = points + ? WHERE username = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setInt(1, studyTimeInMinutes * 60); // Convert minutes to seconds
                preparedStatement.setString(2, username);
                int rowsAffected = preparedStatement.executeUpdate();
                if (rowsAffected > 0) {
                    showAlert("Points Updated", "Points updated successfully for " + username, Alert.AlertType.INFORMATION);
                } else {
                    showAlert("Update Failed", "Failed to update points for " + username, Alert.AlertType.ERROR);
                }
            }
        } catch (SQLException ex) {
            showAlert("Error", "An error occurred: " + ex.getMessage(), Alert.AlertType.ERROR);
        }
    }
    private void startStudySession(int studyTimeInMinutes) {
        Stage primaryStage = new Stage();

        long studyTimeInSeconds = studyTimeInMinutes * 60L;

        Circle loadingCircle = new Circle(50);
        loadingCircle.setFill(Color.TRANSPARENT);
        loadingCircle.setStroke(Color.BLUE);
        loadingCircle.setStrokeWidth(5);

        Label timeLabel = new Label();

        // Digital Clock Label
        Label digitalClockLabel = new Label();
        digitalClockLabel.setStyle("-fx-font-size: 16px;");

        AnimationTimer timer = new AnimationTimer() {
            long startTime = -1;
            long remainingTime = studyTimeInSeconds * 1_000_000_000L;

            @Override
            public void handle(long now) {
                if (startTime < 0) {
                    startTime = now;
                }

                long elapsedTime = now - startTime;
                remainingTime = studyTimeInSeconds * 1_000_000_000L - elapsedTime;

                int minutes = (int) (remainingTime / 1_000_000_000L) / 60;
                int seconds = (int) (remainingTime / 1_000_000_000L) % 60;

                timeLabel.setText(String.format("Time left: %02d:%02d", minutes, seconds));

                // Update Digital Clock
                digitalClockLabel.setText(java.time.LocalTime.now().toString());

                double progress = 1.0 - (double) remainingTime / (studyTimeInSeconds * 1_000_000_000L);
                int red = (int) (255 * progress);
                int green = (int) (255 * (1 - progress));
                red = Math.max(0, Math.min(255, red));
                green = Math.max(0, Math.min(255, green));
                loadingCircle.setStroke(Color.rgb(red, green, 0));

                if (remainingTime <= 0) {
                    stop();
                    showAlert("Study Session Over", "Congratulations! Study session is over!", Alert.AlertType.INFORMATION);
                    int pointsEarned = studyTimeInMinutes * 60;
                    showAlert("Study Session Completed", "You earned " + pointsEarned + " points.", Alert.AlertType.INFORMATION);
                }
            }
        };

        VBox studyLayout = new VBox(10);
        studyLayout.setPadding(new Insets(20));
        studyLayout.getChildren().addAll(loadingCircle, timeLabel, digitalClockLabel); // Add digital clock label

        Button stopButton = new Button("Stop");
        stopButton.setOnAction(event -> timer.stop());

        Button restartButton = new Button("Restart");
        restartButton.setOnAction(event -> {
            timer.start();
            timeLabel.setText(String.format("Time left: %02d:%02d", studyTimeInMinutes, 0));
        });

        HBox buttonsLayout = new HBox(10);
        buttonsLayout.getChildren().addAll(stopButton, restartButton);
        studyLayout.getChildren().add(buttonsLayout);

        timer.start();

        primaryStage.setOnCloseRequest(event -> {
            timer.stop();
        });

        Scene scene = new Scene(studyLayout, 300, 250);
        applyStylesheet(scene);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showAlert(String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle(title);
            alert.setHeaderText(null);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    public void relaxMind() {
        MusicPlayer musicPlayer = new MusicPlayer();
        musicPlayer.addTrack("C:\\Users\\AETI 2\\Desktop\\JavaFinalProject\\Music\\Audio 01.wav");
        musicPlayer.addTrack("C:\\Users\\AETI 2\\Desktop\\JavaFinalProject\\Music\\Audio 02.wav");
        musicPlayer.addTrack("C:\\Users\\AETI 2\\Desktop\\JavaFinalProject\\Music\\Audio 03.wav");

        musicPlayer.play();

        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        statusLabel = new Label("Relaxing your mind with calming music...");
        root.getChildren().add(statusLabel);

        Button stopButton = new Button("Stop Music");
        stopButton.setOnAction(e -> {
            musicPlayer.stop();
            statusLabel.setText("Music stopped.");
        });
        root.getChildren().add(stopButton);

        Button nextButton = new Button("Next Track");
        nextButton.setOnAction(e -> {
            musicPlayer.nextTrack();
            statusLabel.setText("Switched to next track.");
        });
        root.getChildren().add(nextButton);

        Button prevButton = new Button("Previous Track");
        prevButton.setOnAction(e -> {
            musicPlayer.previousTrack();
            statusLabel.setText("Switched to previous track.");
        });
        root.getChildren().add(prevButton);

        Button backButton = new Button("Back to Home Page");
        backButton.setOnAction(e -> {
            musicPlayer.stop();
            stage.close();
        });
        root.getChildren().add(backButton);

        Scene scene = new Scene(root, 400, 250);
        applyStylesheet(scene);
        stage = new Stage();
        stage.setTitle("Relax Your Mind");
        stage.setScene(scene);
        stage.show();
    }

    private void showLeaderboard() {
        String url = "jdbc:mysql://localhost:3306/accounts?useSSL=false";
        String user = "root";
        String password = "root";

        ObservableList<User> users = FXCollections.observableArrayList();

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "SELECT username, points FROM accounts ORDER BY points DESC";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query);
                 ResultSet resultSet = preparedStatement.executeQuery()) {

                while (resultSet.next()) {
                    String username = resultSet.getString("username");
                    int points = resultSet.getInt("points");
                    users.add(new User(username, points));
                }
            }
        } catch (SQLException ex) {
            showAlert("Error", "An error occurred: " + ex.getMessage(), Alert.AlertType.ERROR);
        }

        Platform.runLater(() -> {
            VBox leaderboardLayout = new VBox(10);
            leaderboardLayout.setPadding(new Insets(20));
            Label titleLabel = new Label("Leaderboard");
            titleLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

            TableView<User> tableView = new TableView<>();
            TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
            usernameColumn.setCellValueFactory(new PropertyValueFactory<>("username"));
            TableColumn<User, Integer> pointsColumn = new TableColumn<>("Points");
            pointsColumn.setCellValueFactory(new PropertyValueFactory<>("points"));
            tableView.getColumns().addAll(usernameColumn, pointsColumn);
            tableView.setItems(users);

            leaderboardLayout.getChildren().addAll(titleLabel, tableView);

            Scene leaderboardScene = new Scene(leaderboardLayout, 300, 400);
            Stage leaderboardStage = new Stage();
            applyStylesheet(leaderboardScene);
            leaderboardStage.setScene(leaderboardScene);
            leaderboardStage.setTitle("Leaderboard");
            leaderboardStage.show();
        });
    }

    private void showErrorDialog(String errorMessage) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(errorMessage);
        alert.showAndWait();
    }

    private void logout(Stage primaryStage) {
        if (primaryStage != null) {
            primaryStage.setScene(loginScene);
        } else {
            showAlert("Logout Error", "Unable to logout: primaryStage is null.", Alert.AlertType.ERROR);
        }
    }
}