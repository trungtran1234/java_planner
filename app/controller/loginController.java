package app.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
public class loginController {

    @FXML
    private Button loginButton;

    @FXML
    private Button registerButton;

    @FXML
    private TextField usernameTextField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label loginMessageLabel;

    public void loginButtonAction(ActionEvent event) {
        if (!usernameTextField.getText().isBlank() && !passwordField.getText().isBlank())
        {
            validateLogin();
        }
        else if (usernameTextField.getText().isBlank() && usernameTextField.getText().isBlank()) {
            loginMessageLabel.setText("Please enter username and password");
        }
        else
        {
            loginMessageLabel.setText("Please enter password");
        }
    }

    public void validateLogin()
    {
        app.dbConnection connection = new app.dbConnection();
        Connection connectDB = connection.getConnection();

        String verifyLogin = "SELECT count(1) FROM user WHERE username = ? AND password = ?";

        try {
            PreparedStatement preparedStatement = connectDB.prepareStatement(verifyLogin);
            preparedStatement.setString(1, usernameTextField.getText());
            preparedStatement.setString(2, passwordField.getText());

            ResultSet result = preparedStatement.executeQuery();

            while (result.next()) {
                if (result.getInt(1) == 1) {
                    loginMessageLabel.setText("Welcome!");
                } else {
                    loginMessageLabel.setText("Invalid login");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void goToRegisterPage(ActionEvent event)
    {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/view/registerView.fxml"));
        Parent root;
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
}
