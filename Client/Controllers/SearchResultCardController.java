package com.mycompany.java2wishlistapp;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.shape.Circle;

public class SearchResultCardController {

    @FXML private Circle imgAvatar;
    @FXML private Label lblName;
    @FXML private Label lblEmail;
    @FXML private Button btnAdd;
    
    // Hold user ID for DB operations
    private int userId;

    public void setData(String name, String email, boolean isRequestPending) {
        lblName.setText(name);
        lblEmail.setText(email);
        
        // Handle the "Request Sent" state
        if (isRequestPending) {
            setButtonToPending();
        }
    }

    @FXML
    private void handleAddFriend(ActionEvent event) {
        System.out.println("Sending friend request to: " + lblName.getText());
        
        // TODO: Call Database to insert Friend Request
        // boolean success = DB.sendRequest(currentUserId, this.userId);
        
        // If successful, update UI immediately
        setButtonToPending();
    }
    
    private void setButtonToPending() {
        btnAdd.setText("Request Sent");
        btnAdd.setDisable(true); // Disable button so they can't click again
        btnAdd.setStyle("-fx-background-color: #e0e0e0; -fx-text-fill: #757575; -fx-background-radius: 20;");
    }
}