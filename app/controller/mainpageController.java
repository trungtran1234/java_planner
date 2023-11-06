package app.controller;

import app.dbConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Button;


import java.awt.*;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

import java.io.IOException;
import java.sql.*;
import app.dbConnection;

public class mainpageController implements Initializable{
    @FXML
    private ChoiceBox<String> startTimeSelector;

    @FXML
    private ChoiceBox<String> endTimeSelector;

    @FXML
    private ChoiceBox<String> amOrPmStart;

    @FXML
    private ChoiceBox<String> amOrPmEnd;

    @FXML
    private ChoiceBox<String> dayOfWeek;

    @FXML
    private Button enterScheduleButton;

    private String[] options = {"1:00", "2:00", "3:00", "4:00", "5:00", "6:00", "7:00", "8:00", "9:00", "10:00", "11:00", "12:00"};

    private String[] amOrPm = {"AM", "PM"};

    private String[] weekDay = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"};



    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        startTimeSelector.getItems().addAll(options);
        endTimeSelector.getItems().addAll(options);
        amOrPmStart.getItems().addAll(amOrPm);
        amOrPmEnd.getItems().addAll(amOrPm);
        dayOfWeek.getItems().addAll(weekDay);
    }


    public void enterScheduleButtonAction(ActionEvent event) {
        // Get the user input fields
        String startTime = startTimeSelector.getValue();
        String endTime = endTimeSelector.getValue();
        String startTimeAmOrPm = amOrPmStart.getValue();
        String endTimeAmOrPm = amOrPmEnd.getValue();

        String day = dayOfWeek.getValue();
        String startTimeResult = startTime + " " + startTimeAmOrPm;
        String endTimeResult = endTime + " " + endTimeAmOrPm;




        updateEventStatus(day, startTimeResult);
    }

    public void updateEventStatus(String day, String startTimeResult) {
        dbConnection connectNow = new dbConnection();
        Connection connection = connectNow.connect();
        // Assuming you have a table named 'schedule' in your database
        String sql = "UPDATE schedule SET is_event_scheduled = 1 WHERE day_of_week = ? AND time_slot = ?";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, day);
            statement.setString(2, startTimeResult);
            statement.executeUpdate();
            System.out.println("changed");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("SQL Error: " + e.getMessage());

            // Handle the exception
        }
    }




}
