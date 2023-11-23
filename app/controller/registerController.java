package app.controller;

import app.dbConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class registerController {

    @FXML
    private Button registerButton;

    @FXML
    private TextField registerUsernameField;

    @FXML
    private PasswordField registerPasswordField;

    @FXML
    private Label registerLabel;



    public void registerButtonAction(ActionEvent event) {
        // Get the user input from the username and password fields
        String username = registerUsernameField.getText();
        String password = registerPasswordField.getText();
        // Insert the user into the database
        if (validateRegister(username, password)) {
            loadMainPage();
        } else {
            // Registration failed, handle the error (e.g., duplicate username)
            registerLabel.setText("User taken");
        }
    }

    private boolean validateRegister(String username, String password) {

        dbConnection connectNow = new dbConnection();
        Connection connection = connectNow.connect();
        if (checkUsername(username))
        {
            registerLabel.setText("Username taken");
        }
        else {
            String query = "INSERT INTO users (username, password) VALUES ('" + username + "', '" + password + "')";
            try {
                Statement statement = connection.createStatement();
                int inserted = statement.executeUpdate(query);

                return inserted > 0;

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }

    private boolean checkUsername(String username) {
        dbConnection connectNow = new dbConnection();
        Connection connection = connectNow.connect();

        String query = "SELECT COUNT(*) FROM users WHERE username = '" + username + "'";
        try {
            Statement statement = connection.createStatement();
            ResultSet queryResult = statement.executeQuery(query);
            queryResult.next();
            int count = queryResult.getInt(1);

            return count > 0; //username exists

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void backToLoginPage(ActionEvent event)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/view/loginView.fxml"));
        Parent root = null;
        try {
            root = loader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Scene scene = new Scene(root,  520, 400);
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }
    private void loadMainPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/view/mainpage.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            Stage registerStage = (Stage) registerButton.getScene().getWindow();
            registerStage.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}