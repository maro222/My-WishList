/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java2wishlistapp;

import com.mycompany.model.Request;
import com.mycompany.model.Response;
import com.mycompany.model.User;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtLoginUser;
    @FXML private PasswordField txtLoginPass;
    
    @FXML private TextField txtRegUser;
    @FXML private TextField txtRegEmail;
    @FXML private PasswordField txtRegPass;

    @FXML
    private void handleLogin(ActionEvent event) {
        // 1. Get Data
        String user = txtLoginUser.getText();
        String pass = txtLoginPass.getText();
        
        // 2. Validate
        if (user.isEmpty() || pass.isEmpty()) {
            showAlert("Error", "Please enter username and password.");
            return;
        }
        

        Request request = new Request("LOGIN", new User(user, "", pass));
        TestClient client = App.getClient();
        if (client == null) {
            showAlert("Error", "Server not connected.");
            return;
        }
        
        try {
            client.send(request);
            Thread.sleep(1000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        Response response = client.getCurrentresponse(); // Simulating success for now
        
        if (response.isSuccess()) {
            javafx.application.Platform.runLater(() -> {  
                System.out.println("Login Successful!");
                client.setUser((User) response.getData());
                openDashboard(event);
            
            });            
        } else {
            showAlert("Login Failed", "Invalid Credentials.");
        }
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String user = txtRegUser.getText();
        String email = txtRegEmail.getText();
        String pass = txtRegPass.getText();
        
        if (user.isEmpty() || email.isEmpty() || pass.isEmpty()) {
             showAlert("Error", "All fields are required.");
             return;
        }
        
        Request request = new Request("REGISTER", new User(user, email, pass));
        TestClient client = App.getClient();
        
        try {
            client.send(request);
            Thread.sleep(1000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        Response response = client.getCurrentresponse();
        
        if (response.isSuccess()) {
            javafx.application.Platform.runLater(() -> {  
                System.out.println("Login Successful!");
                openDashboard(event);
            
            });            
        } else {
            showAlert("Login Failed", "Invalid Credentials.");
        }
        
        
        openDashboard(event); 
    }
    
    // --- NAVIGATION HELPER ---
    
    private void openDashboard(ActionEvent event) {
        try {
            // Load the Dashboard FXML
            Parent root = FXMLLoader.load(getClass().getResource("MainDashboard.fxml"));
            
            // Get the current stage (window) from the button that was clicked
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
            // Set the new scene
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen(); // Centers the window on the screen
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load Dashboard.fxml");
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}