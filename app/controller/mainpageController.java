package app.controller;

import app.dbConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;


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



    public void initialize() {
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
        this.FromhourSpinner.setValueFactory(hourValues);
        this.FromminuteSpinner.setValueFactory(minuteValues);



    }
    public static class ScheduleBlock {
        private final int fromHour;
        private final int fromMinute;
        private final String fromAmPm;
        private final String description;
        private final String day;
        private final int toHour;
        private final int toMinute;
        private final String toAmPm;

        public ScheduleBlock(String day, int fromHour, int fromMinute, String fromAmPm, int toHour, int toMinute, String toAmPm, String description) {
            this.day = day;
            this.fromHour = fromHour;
            this.fromMinute = fromMinute;
            this.fromAmPm = fromAmPm;
            this.toHour = toHour;
            this.toMinute = toMinute;
            this.toAmPm = toAmPm;
            this.description = description;
        }
        public String getDay()
        {
            return this.day;
        }
        public int getFromHour() {
            return this.fromHour;
        }

        public int getFromMinute() {
            return this.fromMinute;
        }

        public String getFromAmPm() {
            return this.fromAmPm;
        }
        public int getToHour() {
            return this.toHour;
        }

        public int getToMinute() {
            return this.toMinute;
        }

        public String getToAmPm() {
            return this.toAmPm;
        }

        public String getDescription() {
            return this.description;
        }
    }
    public void SubmitScheduleBlock(ActionEvent event) {
        String selectedDay = dayBox.getValue();
        int selectedFromHour = hourSpinner.getValue();
        int selectedFromMinute = minuteSpinner.getValue();
        String selectedFromAmPm = amPmBox.getValue();
        int selectedHour = hourSpinner.getValue();
        int selectedMinute = minuteSpinner.getValue();
        String selectedAmPm = amPmBox.getValue();
        String description = descriptionTextArea.getText();

        // Create a schedule block with the selected time, AM/PM, and description
        ScheduleBlock scheduleBlock = new ScheduleBlock(selectedDay, selectedFromHour, selectedFromMinute, selectedFromAmPm, selectedHour, selectedMinute, selectedAmPm, description);
        // Add the schedule block to database
        addToDatabase(scheduleBlock);
        int columnIndex = determineColumnIndex(selectedDay);
        int rowIndex = 1;

        ScheduleBlockPane scheduleBlockPane = new ScheduleBlockPane(selectedDay, selectedFromHour, selectedFromMinute, selectedFromAmPm, selectedHour, selectedMinute, selectedAmPm, description);
        gridPane.add(scheduleBlockPane, columnIndex, rowIndex);
    }
    private int determineColumnIndex(String selectedDay) {
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

    public void addToDatabase(ScheduleBlock scheduleBlock) {
        dbConnection connectNow = new dbConnection();
        Connection connection = connectNow.connect();

        String insertQuery = "INSERT INTO schedules (day, fromHour, fromMinute, fromAmpm, toHour, toMinute, toAmpm, description) VALUES ('" +
                scheduleBlock.getDay() + "', " +
                scheduleBlock.getFromHour() + ", " +
                scheduleBlock.getFromMinute() + ", '" +
                scheduleBlock.getFromAmPm() + "', " +
                scheduleBlock.getToHour() + ", " +
                scheduleBlock.getToMinute() + ", '" +
                scheduleBlock.getToAmPm() + "', '" +
                scheduleBlock.getDescription() + "')";

        try {
            Statement statement = connection.createStatement();
            int rowsAffected = statement.executeUpdate(insertQuery);

            if (rowsAffected > 0) {
                this.scheduleLabel.setText("Schedule block added");
            } else {
                this.scheduleLabel.setText("Invalid schedule block");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database-related exceptions
        }
    }
    public class ScheduleBlockPane extends Pane {
        public ScheduleBlockPane(String day, int fromHour, int fromMinute, String fromAmPm, int toHour, int toMinute, String toAmPm, String description) {
            // Create a colored rectangle to represent the schedule block
            Rectangle block = new Rectangle(145, 50); // Customize the size
            block.setFill(Color.LIGHTBLUE); // Customize the color

            // Create text labels with schedule information
            Text timeText = new Text(toHour + ":" + String.format("%02d", toMinute) + " " + toAmPm);
            Text descriptionText = new Text(description);

            // Set the position of text elements
            timeText.setLayoutX(10);
            timeText.setLayoutY(20);
            descriptionText.setLayoutX(10);
            descriptionText.setLayoutY(40);

            // Add elements to the custom Pane
            getChildren().addAll(block, timeText, descriptionText);
        }
    }

}
