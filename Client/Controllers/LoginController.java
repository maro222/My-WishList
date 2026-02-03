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
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LoginController {

    @FXML private TextField txtLoginUser;
    @FXML private PasswordField txtLoginPass;
    
    @FXML private TextField txtRegUser;
    @FXML private TextField txtRegEmail;
    @FXML private PasswordField txtRegPass;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab loginTab;

@FXML
    private void handleLogin(ActionEvent event) {
        String user = txtLoginUser.getText();
        String pass = txtLoginPass.getText();
        
        if (user.isEmpty() || pass.isEmpty()) {
            showAlert("Error", "Please enter username and password.");
            return;
        }

        Request request = new Request("LOGIN", new User(user, "", pass));
        TestClient client = App.getClient();
        
        new Thread(() -> {
            try {
                // Clear old response
                client.currentresponse = null;
                
                // Send
                client.send(request);
                
                // 3. Wait safely in background (Polling)
                // Wait up to 2 seconds
                int timeout = 20; 
                while (client.getCurrentresponse() == null && timeout > 0) {
                    Thread.sleep(100); // This sleep is OK because it's not on the UI thread
                    timeout--;
                }
                
                Response response = client.getCurrentresponse();

                javafx.application.Platform.runLater(() -> {


                    if (response != null && response.isSuccess()) {
                        System.out.println("Login Successful!");
                        client.setUser((User) response.getData());
                        openDashboard(event);
                    } else {
                        showAlert("Login Failed", "Invalid Credentials or Server Timeout.");
                    }
                });

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start(); 
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
        
        new Thread(() -> {
            try {
                client.currentresponse = null;
                
                client.send(request);
                
                int timeout = 20; 
                while (client.getCurrentresponse() == null && timeout > 0) {
                    Thread.sleep(100); 
                    timeout--;
                }
                
                Response response = client.getCurrentresponse();
                
                javafx.application.Platform.runLater(() -> {
                    
                    if (response != null && response.isSuccess()) {
                        showAlert("Registration Successful", "Your account has been created.");
                        
                        // Clear fields
                        txtRegEmail.clear();
                        txtRegPass.clear();
                        txtRegUser.clear();
                        
                        if (mainTabPane != null && loginTab != null) {
                            mainTabPane.getSelectionModel().select(loginTab);
                        }
                    } else {
                        String errorMsg = (response != null) ? response.getMessage() : "Server timeout.";
                        showAlert("Registration Failed", errorMsg);
                    }
                });
                
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start(); 
    }
    
    // --- NAVIGATION HELPER ---
    
    private void openDashboard(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MainDashboard.fxml"));
            
            // Get the current stage (window) from the button that was clicked
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            
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