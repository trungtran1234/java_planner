package app.controller;

import app.dbConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Pair;
import javafx.collections.FXCollections;



import java.sql.*;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private Spinner<Integer> fromHourSpinner;
    @FXML
    private Spinner<Integer> fromMinuteSpinner;
    @FXML
    private ComboBox<String> fromAmPmBox;
    @FXML
    private ColorPicker colorPicker;
    @FXML
    private Label weekLabel;
    private LocalDate currentDate = LocalDate.now();
    @FXML
    private Label mondayLabel;
    @FXML
    private Label tuesdayLabel;
    @FXML
    private Label wednesdayLabel;
    @FXML
    private Label thursdayLabel;
    @FXML
    private Label fridayLabel;
    @FXML
    private Label saturdayLabel;
    @FXML
    private Label sundayLabel;
    @FXML
    private Label alertLabel;
    @FXML
    private Label currentDateLabel;
    private LocalDate startOfWeekForDB;
    private LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);


    public void initialize() throws SQLException {

        displaySchedules();
        updateDateLabels();
        updateWeekLabel();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy");
        currentDateLabel.setText("Today is " + currentDate.format(formatter));
        //from
        fromAmPmBox.setValue("AM");
        fromAmPmBox.setVisibleRowCount(2);
        fromAmPmBox.getItems().add("AM");
        fromAmPmBox.getItems().add("PM");
        fromAmPmBox.setEditable(false);

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
        SpinnerValueFactory<Integer> hourValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 12);
        SpinnerValueFactory<Integer> minuteValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        this.hourSpinner.setValueFactory(hourValues);
        this.minuteSpinner.setValueFactory(minuteValues);
        SpinnerValueFactory<Integer> fromHourValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, 7);
        SpinnerValueFactory<Integer> fromMinuteValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, 0);
        this.fromHourSpinner.setValueFactory(fromHourValues);
        this.fromMinuteSpinner.setValueFactory(fromMinuteValues);

        colorPicker.setValue(Color.LIGHTBLUE);
    }
    public static class ScheduleBlock {
        private final String day;
        private final String fromTime;  // "HH:mm AM/PM" format
        private final String toTime;    // "HH:mm AM/PM" format
        private final String description;
        private final String color;

        // Constructor
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

    // Action for Submitting Schedule Block
    public void SubmitScheduleBlock(ActionEvent event) throws SQLException {
        String selectedDay = dayBox.getValue();
        String fromTime = formatTime(fromHourSpinner.getValue(), fromMinuteSpinner.getValue(), fromAmPmBox.getValue());
        String toTime = formatTime(hourSpinner.getValue(), minuteSpinner.getValue(), amPmBox.getValue());
        String description = descriptionTextArea.getText();
        Color selectedColor = colorPicker.getValue();
        String color = String.format("#%02x%02x%02x",
                (int) (selectedColor.getRed() * 255),
                (int) (selectedColor.getGreen() * 255),
                (int) (selectedColor.getBlue() * 255));
        int fromRowIndex = calculateRowIndex(fromTime);
        int toRowIndex = calculateRowIndex(toTime);

        if (calculateRowIndex(toTime) == (calculateRowIndex(fromTime))) {
            alertLabel.setText("Invalid Time");
            return;
        }
        alertLabel.setText("");

        //backend
        ScheduleBlock scheduleBlock = new ScheduleBlock(selectedDay, fromTime, toTime, description, color);
        addToDatabase(scheduleBlock);

        //frontend
        if (toRowIndex < fromRowIndex) {
            String nextDay = selectedDay;
            switch (selectedDay) {
                case "Monday" -> nextDay = "Tuesday";
                case "Tuesday" -> nextDay = "Wednesday";
                case "Wednesday" -> nextDay = "Thursday";
                case "Thursday" -> nextDay = "Friday";
                case "Friday" -> nextDay = "Saturday";
                case "Saturday" -> nextDay = "Sunday";
                case "Sunday" -> nextDay = "Monday";
            }

            // If the toTime is on the next day, create two panes
            // First pane: fromTime to the end of the day (6:45 AM)
            ScheduleBlockPane eveningPane = new ScheduleBlockPane(selectedDay, fromTime, "6:45 AM", description, color, true, fromTime, toTime);
            eveningPane.addScheduleBlockPane();

            // Second pane: from the start of the next day (7:00 AM) to toTime
            ScheduleBlockPane morningPane = new ScheduleBlockPane(nextDay, "7:00 AM", toTime, description, color, true, fromTime, toTime);
            morningPane.addScheduleBlockPane();
        } else {
            // Create a single pane for the schedule block
            ScheduleBlockPane scheduleBlockPane = new ScheduleBlockPane(selectedDay, fromTime, toTime, description, color, false, fromTime, toTime);
            scheduleBlockPane.addScheduleBlockPane();
        }
    }
    private String formatTime(int hour, int minute, String amPm) {
        return String.format("%02d:%02d %s", hour, minute, amPm);
    }
    private void updateDateLabels() {
        LocalDate startOfWeek = currentDate.with(DayOfWeek.MONDAY);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd\nEEEE");

        Label[] dayLabels = {mondayLabel, tuesdayLabel, wednesdayLabel, thursdayLabel, fridayLabel, saturdayLabel, sundayLabel};
        for (int i = 0; i < 7; i++) {
            LocalDate date = startOfWeek.plusDays(i);
            dayLabels[i].setText(date.format(formatter));
        }
    }
    private void updateWeekLabel() {
        startOfWeek = currentDate.with(DayOfWeek.MONDAY);
        startOfWeekForDB = startOfWeek;
        LocalDate endOfWeek = currentDate.with(DayOfWeek.SUNDAY);
        DateTimeFormatter Formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formattedDate = startOfWeek.format(Formatter) + " - " + endOfWeek.format(Formatter);
        weekLabel.setText(formattedDate);

    }
    // Action for accessing Previous Week
    public void prevWeek(ActionEvent event) throws SQLException {
        currentDate = currentDate.minusWeeks(1);
        updateWeekLabel();
        updateDateLabels();
        displaySchedules();
    }
    // Action for accessing Next Week
    public void nextWeek(ActionEvent event) throws SQLException {
        currentDate = currentDate.plusWeeks(1);
        updateWeekLabel();
        updateDateLabels();
        displaySchedules();
    }
    public void addToDatabase(ScheduleBlock scheduleBlock) throws SQLException {
        dbConnection connectNow = new dbConnection();
        Connection connection = connectNow.connect();

        String insertQuery = "INSERT INTO schedules (day, start_time, end_time, description, color, entry_date) VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            preparedStatement.setString(1, scheduleBlock.getDay());
            preparedStatement.setString(2, scheduleBlock.getFromTime());
            preparedStatement.setString(3, scheduleBlock.getToTime());
            preparedStatement.setString(4, scheduleBlock.getDescription());
            preparedStatement.setString(5, scheduleBlock.getColor());
            DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            preparedStatement.setString(6, startOfWeekForDB.format(dbFormatter));
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public class ScheduleBlockPane extends Pane {

        private Button editTimeButton; // Button for editing time

        private Label descriptionLabel; // Instance variable for description
        private Button deleteButton;
        private Button editButton;
        // Instance variables for schedule information
        private String day;
        private String fromTime;
        private String toTime;
        private String ogFromTime;
        private String ogToTime;
        private String description;
        private String color;
        private boolean wrap;
        private static int columnIndex;
        private static int rowIndex;
        private static int rowSpan;

        public ScheduleBlockPane(String day, String fromTime, String toTime, String description, String color, boolean wrap, String ogFromTime, String ogToTime) {
            // Initialize instance variables
            this.day = day;
            this.fromTime = fromTime;
            this.toTime = toTime;
            this.description = description;
            this.color = color;
            this.wrap = wrap;
            this.ogFromTime = ogFromTime;
            this.ogToTime = ogToTime;

            // Create UI elements to display the schedule block information
            descriptionLabel = new Label("Event: " + description + "\n\n" + ogFromTime + " - " + ogToTime);
            setupDescriptionLabel();
            setupDeleteButton();
            setupEditButton();
            setupEditTimeButton();

            // Add the UI elements to the pane
            getChildren().addAll(descriptionLabel, deleteButton, editButton);

            // Calculate the position of the pane in the grid
            calculatePosition(day, fromTime, toTime, description, color);
        }

        private void setupDescriptionLabel() {
            descriptionLabel.setStyle("-fx-font-weight: bold;");
            descriptionLabel.setWrapText(true);
            descriptionLabel.setMaxWidth(70);
            descriptionLabel.setLayoutX(5);
            descriptionLabel.setLayoutY(20);
            setStyle("-fx-background-color: " + color + ";");
            setPrefHeight(80);
        }

        private void setupEditTimeButton() {
            editTimeButton = new Button("Edit Time");
            editTimeButton.setPrefSize(100, 20);
            editTimeButton.setLayoutX(0); // Top left corner
            editTimeButton.setLayoutY(editButton.getLayoutY() + editButton.getHeight() + 25);
            editTimeButton.setOnAction(e -> handleEditTime());
            editTimeButton.setStyle("-fx-background-color: " + color + "; -fx-text-fill: Black; -fx-font-size: 10px;-fx-border-width: 1; +  // Set the border width to 1px;\"-fx-border-insets: -10;\" ");  // Style to match the pane
            getChildren().add(editTimeButton); // Add the button to the pane
        }

        private void handleEditTime() {
            // Create the dialog
            Dialog<Pair<String, String>> dialog = new Dialog<>();
            dialog.setTitle("Edit Time");
            dialog.setHeaderText("Update the event time");

            // Set the button types
            ButtonType saveButtonType = new ButtonType("Save", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

            // Create spinners and combo boxes for the fromTime and toTime
            SpinnerValueFactory<Integer> fromHourValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, getHour(fromTime));
            Spinner<Integer> fromHourSpinner = new Spinner<>(fromHourValues);
            fromHourSpinner.setEditable(true);

            SpinnerValueFactory<Integer> fromMinuteValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, getMinute(fromTime));
            Spinner<Integer> fromMinuteSpinner = new Spinner<>(fromMinuteValues);
            fromMinuteSpinner.setEditable(true);

            ComboBox<String> fromAmPmCombo = new ComboBox<>(FXCollections.observableArrayList("AM", "PM"));
            fromAmPmCombo.setValue(getAmPm(fromTime));

            SpinnerValueFactory<Integer> toHourValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 12, getHour(toTime));
            Spinner<Integer> toHourSpinner = new Spinner<>(toHourValues);
            toHourSpinner.setEditable(true);

            SpinnerValueFactory<Integer> toMinuteValues = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, getMinute(toTime));
            Spinner<Integer> toMinuteSpinner = new Spinner<>(toMinuteValues);
            toMinuteSpinner.setEditable(true);
            
            ComboBox<String> toAmPmCombo = new ComboBox<>(FXCollections.observableArrayList("AM", "PM"));
            toAmPmCombo.setValue(getAmPm(toTime));

            // Layout for the dialog content
            GridPane gridPane = new GridPane();
            gridPane.setHgap(10);
            gridPane.setVgap(10);
            gridPane.add(new Label("From time:"), 0, 0);
            gridPane.add(fromHourSpinner, 1, 0);
            gridPane.add(fromMinuteSpinner, 2, 0);
            gridPane.add(fromAmPmCombo, 3, 0);
            gridPane.add(new Label("To time:"), 0, 1);
            gridPane.add(toHourSpinner, 1, 1);
            gridPane.add(toMinuteSpinner, 2, 1);
            gridPane.add(toAmPmCombo, 3, 1);

            dialog.getDialogPane().setContent(gridPane);

            // Convert the result to a pair of strings when the save button is clicked
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == saveButtonType) {
                    String newFromTime = formatTime(fromHourSpinner.getValue(), fromMinuteSpinner.getValue(), fromAmPmCombo.getValue());
                    String newToTime = formatTime(toHourSpinner.getValue(), toMinuteSpinner.getValue(), toAmPmCombo.getValue());
                    return new Pair<>(newFromTime, newToTime);
                }
                return null;
            });

            // Show the dialog and process the result
            Optional<Pair<String, String>> result = dialog.showAndWait();

            result.ifPresent(newTimes -> {
                try {
                    handleDelete(wrap);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                // Update the times in the UI and possibly in the database
                fromTime = newTimes.getKey(); // Update the instance variable
                toTime = newTimes.getValue(); // Update the instance variable

                try {
                    submitSchedule(fromTime,toTime);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }

                // Update the database with the new times
                // updateTimesInDatabase(fromTime, toTime); // Implement this method
            });
        }


        public void submitSchedule(String newFromTime, String newToTime) throws SQLException {
            String fromTime = newFromTime;
            String toTime = newToTime;

            int fromRowIndex = calculateRowIndex(fromTime);
            int toRowIndex = calculateRowIndex(toTime);

            if (calculateRowIndex(toTime) == (calculateRowIndex(fromTime))) {
                alertLabel.setText("Invalid Time");
                return;
            }
            alertLabel.setText("");

            //backend
            ScheduleBlock scheduleBlock = new ScheduleBlock(day, fromTime, toTime, description, color);
            addToDatabase(scheduleBlock);

            //frontend
            if (toRowIndex < fromRowIndex) {
                String nextDay = day;
                switch (day) {
                    case "Monday" -> nextDay = "Tuesday";
                    case "Tuesday" -> nextDay = "Wednesday";
                    case "Wednesday" -> nextDay = "Thursday";
                    case "Thursday" -> nextDay = "Friday";
                    case "Friday" -> nextDay = "Saturday";
                    case "Saturday" -> nextDay = "Sunday";
                    case "Sunday" -> nextDay = "Monday";
                }

                // If the toTime is on the next day, create two panes
                // First pane: fromTime to the end of the day (6:45 AM)
                ScheduleBlockPane eveningPane = new ScheduleBlockPane(day, fromTime, "6:45 AM", description, color, true, fromTime, toTime);
                eveningPane.addScheduleBlockPane();

                // Second pane: from the start of the next day (7:00 AM) to toTime
                ScheduleBlockPane morningPane = new ScheduleBlockPane(nextDay, "7:00 AM", toTime, description, color, true, fromTime, toTime);
                morningPane.addScheduleBlockPane();
            } else {
                // Create a single pane for the schedule block
                ScheduleBlockPane scheduleBlockPane = new ScheduleBlockPane(day, fromTime, toTime, description, color, false, fromTime, toTime);
                scheduleBlockPane.addScheduleBlockPane();
            }

        }



        private int getHour(String time) {
            // Extract the hour part from the time string
            return Integer.parseInt(time.split(":")[0]);
        }

        private int getMinute(String time) {
            // Extract the minute part from the time string
            return Integer.parseInt(time.split(":")[1].split(" ")[0]);
        }

        private String getAmPm(String time) {
            // Extract the AM/PM part from the time string
            return time.split(" ")[1];
        }




        private void setupDeleteButton() {
            deleteButton = new Button("X");
            deleteButton.setLayoutX(75);
            deleteButton.setLayoutY(3);
            deleteButton.setOnAction(e -> {
                try {
                    handleDelete(wrap);
                } catch (SQLException ex) {
                    throw new RuntimeException(ex);
                }
            });
            deleteButton.setStyle("-fx-background-color: " + color + "; -fx-text-fill: black;");
        }

        private void setupEditButton() {
            editButton = new Button("Edit Descr");
            editButton.setPrefSize(100, 20); // Adjust the size as needed
            editButton.setLayoutX(0); // 5 pixels from the left edge
            editButton.setLayoutY(this.getPrefHeight() + editButton.getPrefHeight());
            editButton.setOnAction(e -> handleEdit());
            editButton.setStyle("-fx-background-color: " + color + "; -fx-text-fill: Black; -fx-font-size: 10px;-fx-border-width: 1; +  // Set the border width to 1px;\"-fx-border-insets: -10;\" ");  // Same color as the pane
        }

        private void calculatePosition(String day, String fromTime, String toTime, String description, String color) {
            columnIndex = calculateColumnIndex(day);
            rowIndex = calculateRowIndex(fromTime);
            rowSpan = calculateRowSpan(day, fromTime, toTime, description, color);
        }

        private void handleEdit() {
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Edit Description");
            dialog.setHeaderText("Update the event description");
            dialog.setContentText("Description:");

            Optional<String> result = dialog.showAndWait();
            result.ifPresent(this::updateDescription);
        }

        private void updateDescription(String newDescription) {
            try {
                // First, update the description in the database
                updateDescriptionInDatabase(newDescription);

                // If the database update is successful, update the instance variable and UI
                this.description = newDescription;
                descriptionLabel.setText("Event: " + newDescription + "\n\n" + ogFromTime + " - " + ogToTime);
            } catch (SQLException ex) {
                ex.printStackTrace();
                // Handle exception - you might want to show an error message to the user
            }
        }
        private void updateDescriptionInDatabase(String newDescription) throws SQLException {
            dbConnection connectNow = new dbConnection();
            Connection connection = connectNow.connect();

            String updateQuery = "UPDATE schedules SET description = ? WHERE day = ? AND start_time = ? AND end_time = ? AND COALESCE(description, '') = ? AND color = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
                preparedStatement.setString(1, newDescription);
                preparedStatement.setString(2, day);
                preparedStatement.setString(3, fromTime);
                preparedStatement.setString(4, toTime);
                preparedStatement.setString(5, this.description != null ? this.description : "");
                preparedStatement.setString(6, color);

                preparedStatement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
                // You might want to handle this exception more gracefully
            }
        }


        private void handleDelete(boolean wrap) throws SQLException {
            // Remove from database
            if (wrap)
            {
                removeFromDatabase(day, ogFromTime, ogToTime, description, color);
            }
            else {
                removeFromDatabase(day, fromTime, toTime, description, color);
            }

            // Remove this pane from the grid
            gridPane.getChildren().remove(this);
        }
        public void addScheduleBlockPane()
        {
            gridPane.add(this, columnIndex, rowIndex, 1, rowSpan);
        }
    }
    private int calculateColumnIndex(String selectedDay) {
        return switch (selectedDay) {
            case "Monday" -> 1;
            case "Tuesday" -> 2;
            case "Wednesday" -> 3;
            case "Thursday" -> 4;
            case "Friday" -> 5;
            case "Saturday" -> 6;
            case "Sunday" -> 7;
            default -> 1; // Default value
        };
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
        // Round the minutes to the nearest 15-minute increment
        if (minute > 50) {
            minute = 0;
            hour++;
        }
        else
        {
            minute = roundToNearest15(minute);
        }

        // Calculate the row index based on the time
        int rowIndex;
        if (hour >= 7 && hour < 12) {
            rowIndex = (hour - 7) * 4 + minute / 15 + 1;
        } else if (hour >= 12 && hour < 24) {
            rowIndex = 21 + (hour - 12) * 4 + minute / 15;
        } else {
            rowIndex = 69 + hour * 4 + minute / 15;
        }

        if (hour == 0 && minute == 0) {
            rowIndex = 69;
        }

        return rowIndex;
    }

    private int roundToNearest15(int minute) {
        return (int) (Math.round(minute / 15.0) * 15) % 60;
    }
    public int calculateRowSpan(String selectedDay, String fromTime, String toTime, String description, String color) {
        int fromRowIndex = calculateRowIndex(fromTime);
        int toRowIndex = calculateRowIndex(toTime);
        String nextDay = selectedDay;
        switch (selectedDay) {
            case "Monday" -> nextDay = "Tuesday";
            case "Tuesday" -> nextDay = "Wednesday";
            case "Wednesday" -> nextDay = "Thursday";
            case "Thursday" -> nextDay = "Friday";
            case "Friday" -> nextDay = "Saturday";
            case "Saturday" -> nextDay = "Sunday";
            case "Sunday" -> nextDay = "Monday";
        }

        // Calculate the row span
        int rowSpan;

        // Check if the event wraps around to the next day (toTime is earlier than fromTime)
        if (toRowIndex < fromRowIndex) {
            // Calculate rowSpan for the part of the event before midnight
            int endOfDayRowIndex = calculateRowIndex("11:59 PM"); // Assuming planner ends at 11:59 PM
            rowSpan = endOfDayRowIndex - fromRowIndex + 1;

            // Add another pane for the part of the event after midnight
            ScheduleBlockPane nextDayPane = new ScheduleBlockPane(nextDay, "7:00 AM", toTime, description, color, false, fromTime, toTime);
            nextDayPane.addScheduleBlockPane();
        } else {
            rowSpan = toRowIndex - fromRowIndex;
        }

        // Adjust for cases where fromTime and toTime are in the same 15-minute block
        if (rowSpan <= 0) {
            rowSpan = 1; // Minimum span to show the event
        }

        return rowSpan;
    }

    public List<ScheduleBlock> loadSchedulesFromDatabase(String startDate) throws SQLException {
        List<ScheduleBlock> schedules = new ArrayList<>();
        dbConnection connectNow = new dbConnection();
        Connection connection = connectNow.connect();

        String query = "SELECT day, start_time, end_time, description, color FROM schedules WHERE entry_date = ?"; // Modify as per your table structure

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            preparedStatement.setString(1, startDate);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    String day = resultSet.getString("day");
                    String startTime = resultSet.getString("start_time");
                    String endTime = resultSet.getString("end_time");
                    String description = resultSet.getString("description");
                    String color = resultSet.getString("color");

                    ScheduleBlock scheduleBlock = new ScheduleBlock(day, startTime, endTime, description, color);
                    schedules.add(scheduleBlock);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return schedules;
    }
    public void displaySchedules() throws SQLException {
        DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        List<ScheduleBlock> schedules = loadSchedulesFromDatabase(startOfWeek.format(dbFormatter));

        gridPane.getChildren().removeIf(node -> node instanceof ScheduleBlockPane);
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
                        schedule.getColor(),
                        true,
                        schedule.getFromTime(),
                        schedule.getToTime()
                );
                eveningPane.addScheduleBlockPane(); // Add pane for the evening part

                ScheduleBlockPane morningPane = new ScheduleBlockPane(
                        schedule.getDay(),
                        "7:00 AM", // Assuming this is the start of your day
                        schedule.getToTime(),
                        schedule.getDescription(),
                        schedule.getColor(),
                        true,
                        schedule.getFromTime(),
                        schedule.getToTime()

                );
                morningPane.addScheduleBlockPane(); // Add pane for the morning part
            } else {
                // Schedule does not wrap around
                ScheduleBlockPane pane = new ScheduleBlockPane(
                        schedule.getDay(),
                        schedule.getFromTime(),
                        schedule.getToTime(),
                        schedule.getDescription(),
                        schedule.getColor(),
                        false,
                        schedule.getFromTime(),
                        schedule.getToTime()

                );
                pane.addScheduleBlockPane();
            }
        }
    }
    private void removeFromDatabase(String day, String fromTime, String toTime, String description, String color) throws SQLException {
        dbConnection connectNow = new dbConnection();
        Connection connection = connectNow.connect();

        String deleteQuery = "DELETE FROM schedules WHERE day = ? AND start_time = ? AND end_time = ? AND COALESCE(description, '') = ? AND color = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setString(1, day);
            preparedStatement.setString(2, fromTime);
            preparedStatement.setString(3, toTime);
            preparedStatement.setString(4, description != null ? description : "");
            preparedStatement.setString(5, color);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
