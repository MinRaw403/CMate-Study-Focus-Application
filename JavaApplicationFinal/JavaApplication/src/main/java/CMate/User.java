package CMate;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {

    private final String username;
    private double totalStudyTime;
    private final int points;

    public User(String username, int points) {
        this.username = username;
        this.points = points;
    }

    public String getUsername() {
        return username;
    }

    public int getPoints() {
        return points;
    }

    public StringProperty usernameProperty() {
        return new SimpleStringProperty(username);
    }

    public IntegerProperty pointsProperty() {
        return new SimpleIntegerProperty(points);
    }

    public Number getTotalStudyTime() {
        return totalStudyTime;
    }
}