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
import java.sql.Statement;
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
        if (!usernameTextField.getText().isBlank() && !passwordField.getText().isBlank()) {
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
        dbConnection connectNow = new dbConnection();
        Connection connection = connectNow.connect();

        String query = "SELECT count(1) FROM users WHERE username = '" +usernameTextField.getText() + "' AND password = '" + passwordField.getText() + "'";
        try {
            Statement statement = connection.createStatement();
            ResultSet queryResult = statement.executeQuery(query);

            while(queryResult.next())
            {
                if (queryResult.getInt(1) == 1)
                {
                    loadMainPage();
                }
                else
                {
                    loginMessageLabel.setText("Invalid login. Please try again.");
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

    private void loadMainPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/app/view/mainpage.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setMaximized(true);
            Stage loginStage = (Stage) loginButton.getScene().getWindow();
            loginStage.close();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}