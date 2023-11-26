package app.controller;

import app.dbConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.sql.*;
import java.util.*;


public class mainpageController {

    @FXML
    private GridPane scheduleGridPane;
    @FXML
    private Spinner<Integer> hourSpinner;
    @FXML
    private Spinner<Integer> minuteSpinner;
    @FXML
    private TextArea descriptionTextArea;
    @FXML
    private ComboBox<String> amPmBox;
    @FXML
    private Label scheduleLabel;
    @FXML
    private GridPane gridPane;
    @FXML
    private ComboBox<String> dayBox;
    @FXML
    private Spinner<Integer> FromhourSpinner;
    @FXML
    private Spinner<Integer> FromminuteSpinner;
    @FXML
    private ComboBox<String> FromamPmBox;
    @FXML
    private ColorPicker colorPicker;

    public void initialize() throws SQLException {
        displaySchedules();
        //from
        FromamPmBox.setValue("AM");
        FromamPmBox.setVisibleRowCount(2);
        FromamPmBox.getItems().add("AM");
        FromamPmBox.getItems().add("PM");
        FromamPmBox.setEditable(false);

        //to
        amPmBox.setValue("AM");
        amPmBox.setVisibleRowCount(2);
        amPmBox.getItems().add("AM");
        amPmBox.getItems().add("PM");
        amPmBox.setEditable(false);

        //day
        dayBox.setValue("Monday");
        dayBox.setVisibleRowCount(7);
        dayBox.getItems().add("Monday");
        dayBox.getItems().add("Tuesday");
        dayBox.getItems().add("Wednesday");
        dayBox.getItems().add("Thursday");
        dayBox.getItems().add("Friday");
        dayBox.getItems().add("Saturday");
        dayBox.getItems().add("Sunday");
        dayBox.setEditable(false);

        //spinner
        SpinnerValueFactory<Integer> hourValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
        SpinnerValueFactory<Integer> minuteValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        this.hourSpinner.setValueFactory(hourValues);
        this.minuteSpinner.setValueFactory(minuteValues);
        SpinnerValueFactory<Integer> fromHourValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 1);
        SpinnerValueFactory<Integer> fromMinuteValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 60, 0);
        this.FromhourSpinner.setValueFactory(fromHourValues);
        this.FromminuteSpinner.setValueFactory(fromMinuteValues);

        gridPane.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                newScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            }
        });
    }
    public static class ScheduleBlock {
        private final String day;
        private final String fromTime;  // "HH:mm AM/PM" format
        private final String toTime;    // "HH:mm AM/PM" format
        private final String description;
        private final String color;

        public ScheduleBlock(String day, String fromTime, String toTime, String description, String color) {
            this.day = day;
            this.fromTime = fromTime;
            this.toTime = toTime;
            this.description = description;
            this.color = color;
        }

        // Getters
        public String getDay() { return this.day; }
        public String getFromTime() { return this.fromTime; }
        public String getToTime() { return this.toTime; }
        public String getDescription() { return this.description; }
        public String getColor() { return this.color; }

    }
    public void SubmitScheduleBlock(ActionEvent event) throws SQLException {
        String selectedDay = dayBox.getValue();
        String fromTime = formatTime(FromhourSpinner.getValue(), FromminuteSpinner.getValue(), FromamPmBox.getValue());
        String toTime = formatTime(hourSpinner.getValue(), minuteSpinner.getValue(), amPmBox.getValue());
        String description = descriptionTextArea.getText();
        Color selectedColor = colorPicker.getValue();
        String color = String.format("#%02x%02x%02x",
                (int) (selectedColor.getRed() * 255),
                (int) (selectedColor.getGreen() * 255),
                (int) (selectedColor.getBlue() * 255));
        int fromRowIndex = calculateRowIndex(fromTime);
        int toRowIndex = calculateRowIndex(toTime);

        //backend
        ScheduleBlock scheduleBlock = new ScheduleBlock(selectedDay, fromTime, toTime, description, color);
        addToDatabase(scheduleBlock);

        //frontend
        if (toRowIndex < fromRowIndex) {
            // If the toTime is on the next day, create two panes
            // First pane: fromTime to the end of the day (6:45 AM)
            ScheduleBlockPane eveningPane = new ScheduleBlockPane(selectedDay, fromTime, "6:45 AM", description, color);
            eveningPane.addScheduleBlockPane();

            // Second pane: from the start of the day (7:00 AM) to toTime
            ScheduleBlockPane morningPane = new ScheduleBlockPane(selectedDay, "7:00 AM", toTime, description, color);
            morningPane.addScheduleBlockPane();
        } else {
            // Create a single pane for the schedule block
            int rowSpan = calculateRowSpan(selectedDay, fromTime, toTime, description, color);
            ScheduleBlockPane scheduleBlockPane = new ScheduleBlockPane(selectedDay, fromTime, toTime, description, color);
            scheduleBlockPane.addScheduleBlockPane();
        }
    }

    private String formatTime(int hour, int minute, String amPm) {
        return String.format("%02d:%02d %s", hour, minute, amPm);
    }

    public void addToDatabase(ScheduleBlock scheduleBlock) throws SQLException {
        dbConnection connectNow = new dbConnection();
        Connection connection = connectNow.connect();

        String insertQuery = "INSERT INTO schedules (day, start_time, end_time, description, color) VALUES (?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, scheduleBlock.getDay());
            preparedStatement.setString(2, scheduleBlock.getFromTime());
            preparedStatement.setString(3, scheduleBlock.getToTime());
            preparedStatement.setString(4, scheduleBlock.getDescription());
            preparedStatement.setString(5, scheduleBlock.getColor());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public class ScheduleBlockPane extends Pane {
        private static int columnIndex;
        private static int rowIndex;
        private static int rowSpan;
        private Button deleteButton;


        public ScheduleBlockPane(String day, String fromTime, String toTime, String description, String color) {
            // Create UI elements to display the schedule block information
            this.getStyleClass().add("pane-border");

            Label descriptionLabel = new Label("Event: " + description);
            descriptionLabel.setWrapText(true); // Enable text wrapping
            descriptionLabel.setMaxWidth(70);
            descriptionLabel.setLayoutX(5);
            descriptionLabel.setLayoutY(20);
            // Customize the appearance of the pane
            setStyle("-fx-background-color: " + color + ";");
            setPrefHeight(80); // Adjust the height as needed

            // Add the UI elements to the pane
            getChildren().addAll(descriptionLabel);
            columnIndex = calculateColumnIndex(day);
            rowIndex = calculateRowIndex(fromTime);
            rowSpan = calculateRowSpan(day, fromTime, toTime, description, color);

            deleteButton = new Button("X");
            deleteButton.setLayoutX(75);
            deleteButton.setLayoutY(3);
            deleteButton.setOnAction(e -> {
                try {
                    handleDelete(day, fromTime, toTime, description, color);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            deleteButton.setStyle("-fx-background-color: lightblue; -fx-text-fill: black;");
            // Add the delete button to the pane
            getChildren().add(deleteButton);
        }

        private String handleColorChange() {
            Color selectedColor = colorPicker.getValue();

            return String.format("-fx-background-color: #%02x%02x%02x;",
                    (int) (selectedColor.getRed() * 255),
                    (int) (selectedColor.getGreen() * 255),
                    (int) (selectedColor.getBlue() * 255));
        }
        private void handleDelete(String day, String fromTime, String toTime, String description, String color) throws SQLException {
            // Remove from database
            removeFromDatabase(day, fromTime, toTime, description, color);

            // Remove this pane from the grid
            gridPane.getChildren().remove(this);
        }
        public void addScheduleBlockPane()
        {
            gridPane.add(this, columnIndex, rowIndex, 1, rowSpan);
        }
    }
    private int calculateColumnIndex(String selectedDay) {
        int columnIndex = 1; // Default value
        switch (selectedDay) {
            case "Monday":
                columnIndex = 1;
                break;
            case "Tuesday":
                columnIndex = 2;
                break;
            case "Wednesday":
                columnIndex = 3;
                break;
            case "Thursday":
                columnIndex = 4;
                break;
            case "Friday":
                columnIndex = 5;
                break;
            case "Saturday":
                columnIndex = 6;
                break;
            case "Sunday":
                columnIndex = 7;
                break;
        }
        return columnIndex;
    }
    private int calculateRowIndex(String fromTime) {
        // Split the time into components
        String[] parts = fromTime.split("[: ]"); // Split by colon and space
        int hour = Integer.parseInt(parts[0]);
        int minute = Integer.parseInt(parts[1]);
        String amPm = parts[2];

        // Convert to 24-hour format
        if (hour == 12) {
            hour = (amPm.equalsIgnoreCase("AM")) ? 0 : 12;
        } else if (amPm.equalsIgnoreCase("PM")) {
            hour += 12;
        }

        // Calculate the row index based on the time
        int rowIndex;
        if (hour >= 7 && hour < 12) {
            // For times between 7:00 AM and 11:45 AM
            rowIndex = (hour - 7) * 4 + minute / 15 + 1;
        } else if (hour >= 12 && hour < 24) {
            // For times between 12:00 PM and 11:45 PM
            rowIndex = 21 + (hour - 12) * 4 + minute / 15;
        } else {
            // For times between 12:00 AM and 6:45 AM
            rowIndex = 69 + hour * 4 + minute / 15;
        }

        // Adjust for times that exactly fall on 12:00 AM
        if (hour == 0 && minute == 0) {
            rowIndex = 69;
        }

        return rowIndex;
    }

    public int calculateRowSpan(String selectedDay, String fromTime, String toTime, String description, String color) {
        int fromRowIndex = calculateRowIndex(fromTime);
        int toRowIndex = calculateRowIndex(toTime);

        // Handle wrap-around scenario (e.g., fromTime in PM and toTime in AM next day)
        if (toRowIndex < fromRowIndex) {
            ScheduleBlockPane scheduleBlockPane = new ScheduleBlockPane(selectedDay, "7:00 AM", toTime, description, color);
            scheduleBlockPane.addScheduleBlockPane();
        }

        // Calculate the row span
        int rowSpan = toRowIndex - fromRowIndex;

        // For a block that ends exactly at the start of a new interval
        // (e.g., 8:00 to 9:00 should be a span of 4, not 3)
        if (rowSpan == 0) {
            rowSpan = 96; // Full cycle, covers the entire planner grid
        }

        return rowSpan;
    }

    public List<ScheduleBlock> loadSchedulesFromDatabase() throws SQLException {
        List<ScheduleBlock> schedules = new ArrayList<>();
        dbConnection connectNow = new dbConnection(); // Replace with your database connection class
        Connection connection = connectNow.connect(); // Establish connection

        String query = "SELECT day, start_time, end_time, description, color FROM schedules"; // Modify as per your table structure

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                String day = resultSet.getString("day");
                String startTime = resultSet.getString("start_time");
                String endTime = resultSet.getString("end_time");
                String description = resultSet.getString("description");
                String color = resultSet.getString("color");

                ScheduleBlock scheduleBlock = new ScheduleBlock(day, startTime, endTime, description, color);
                schedules.add(scheduleBlock);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }

        return schedules;
    }

    public void displaySchedules() throws SQLException {
        List<ScheduleBlock> schedules = loadSchedulesFromDatabase();

        for (ScheduleBlock schedule : schedules) {
            int fromRowIndex = calculateRowIndex(schedule.getFromTime());
            int toRowIndex = calculateRowIndex(schedule.getToTime());

            if (toRowIndex < fromRowIndex) {
                // Schedule wraps around to the next day
                ScheduleBlockPane eveningPane = new ScheduleBlockPane(
                        schedule.getDay(),
                        schedule.getFromTime(),
                        "6:45 AM", // Assuming this is the end of your day
                        schedule.getDescription(),
                        schedule.getColor()
                );
                eveningPane.addScheduleBlockPane(); // Add pane for the evening part

                ScheduleBlockPane morningPane = new ScheduleBlockPane(
                        schedule.getDay(),
                        "7:00 AM", // Assuming this is the start of your day
                        schedule.getToTime(),
                        schedule.getDescription(),
                        schedule.getColor()

                );
                morningPane.addScheduleBlockPane(); // Add pane for the morning part
            } else {
                // Schedule does not wrap around
                ScheduleBlockPane pane = new ScheduleBlockPane(
                        schedule.getDay(),
                        schedule.getFromTime(),
                        schedule.getToTime(),
                        schedule.getDescription(),
                        schedule.getColor()

                );
                pane.addScheduleBlockPane();
            }
        }
    }


    private void removeFromDatabase(String day, String fromTime, String toTime, String description, String color) throws SQLException {
        dbConnection connectNow = new dbConnection();
        Connection connection = connectNow.connect();

        String deleteQuery = "DELETE FROM schedules WHERE day = ? AND start_time = ? AND end_time = ? AND description = ? AND color = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, day);
            preparedStatement.setString(2, fromTime);
            preparedStatement.setString(3, toTime);
            preparedStatement.setString(4, description);
            preparedStatement.setString(5, color);


            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions appropriately
        }
    }



}
