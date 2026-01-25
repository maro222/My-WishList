package com.mycompany.java2wishlistapp;

import com.mycompany.model.Request;
import com.mycompany.model.Response;
import com.mycompany.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;

public class SearchResultCardController {
    
    @FXML private Circle imgAvatar;
    @FXML private Label lblName;
    @FXML private Label lblEmail;
    @FXML private Button btnAdd;
    
    private User user;
    
    /**
     * Set user data - this is the method that should be called
     */
    public void setUserData(User user) {
        System.out.println("=== setUserData called for: " + user.getUsername() + " (ID: " + user.getId() + ") ===");
        this.user = user;
        lblName.setText(user.getUsername());
        lblEmail.setText(user.getEmail());
    }
    
    @FXML
    private void handleAddFriend(ActionEvent event) {
        System.out.println("=== handleAddFriend clicked ===");
        System.out.println("User object: " + (user != null ? user.getUsername() : "NULL"));
        
        if (user == null) {
            System.out.println("ERROR: user is null!");
            showAlert("Error", "User data not found.", Alert.AlertType.ERROR);
            return;
        }
        
        System.out.println("Sending friend request to: " + user.getUsername() + " (ID: " + user.getId() + ")");
        
        try {
            TestClient client = App.getClient();
            if (client == null || client.getUser() == null) {
                System.out.println("ERROR: Client or current user is null");
                showAlert("Error", "Not connected to server.", Alert.AlertType.ERROR);
                return;
            }
            
            int currentUserId = client.getUser().getId();
            int receiverId = user.getId();
            
            System.out.println("Sender ID: " + currentUserId + ", Receiver ID: " + receiverId);
            
            int[] friendRequestData = new int[]{currentUserId, receiverId};
            
            Request request = new Request("ADD_FRIEND", friendRequestData);
            System.out.println("Sending ADD_FRIEND request...");
            client.send(request);
            
            Thread.sleep(1000);
            
            Response response = client.getCurrentresponse();
            System.out.println("Response received: " + (response != null ? "YES" : "NULL"));
            
            if (response != null) {
                System.out.println("Response success: " + response.isSuccess());
                System.out.println("Response message: " + response.getMessage());
                
                if (response.isSuccess()) {
                    System.out.println("Friend request sent successfully!");
                    setButtonToPending();
                    showAlert("Success", "Friend request sent to " + user.getUsername() + "!", Alert.AlertType.INFORMATION);
                } else {
                    System.out.println("Failed to send friend request: " + response.getMessage());
                    showAlert("Info", response.getMessage(), Alert.AlertType.WARNING);
                }
            } else {
                System.out.println("ERROR: Null response from server");
                showAlert("Error", "No response from server.", Alert.AlertType.ERROR);
            }
            
            client.currentresponse = null;
            
        } catch (Exception e) {
            System.out.println("EXCEPTION in handleAddFriend:");
            e.printStackTrace();
            showAlert("Error", "An error occurred: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
    
    private void setButtonToPending() {
        btnAdd.setText("Request Sent");
        btnAdd.setDisable(true);
        btnAdd.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #757575; -fx-background-radius: 20;");
    }
    
    /**
     * Show an alert dialog with the given message
     */
    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}