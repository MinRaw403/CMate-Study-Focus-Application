package CMate;

import java.sql.*;

public class AppWithoutGUI {
    private String username;
    private int points;

    public AppWithoutGUI(String username) {
        this.username = username;
        this.points = 0;
    }

    public String getUsername() {
        return username;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void addPoints(int pointsToAdd) {
        this.points += pointsToAdd;
        updatePointsInDatabase();
    }

    private void updatePointsInDatabase() {
        String url = "jdbc:mysql://localhost:3306/account";
        String user = "root";
        String password = "root";

        try (Connection connection = DriverManager.getConnection(url, user, password)) {
            String query = "UPDATE accounts SET points = ? WHERE username = ?";
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, points);
            preparedStatement.setString(2, username);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating points in the database: " + e.getMessage());
        }
    }
}