package com.mycompany.java2wishlistapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class NotificationItemController {

    @FXML private Label lblTitle;
    @FXML private Label lblMessage;
    @FXML private Label lblTime;
    @FXML private ImageView imgIcon;
    @FXML private VBox iconContainer;

    public void setData(String title, String message, String time, boolean isSuccess) {
        lblTitle.setText(title);
        lblMessage.setText(message);
        lblTime.setText(time);
        
        // Dynamic styling based on notification type
        if (isSuccess) {
            // Green for money/gifts
            iconContainer.setStyle("-fx-background-color: #4cd137; -fx-background-radius: 5;"); 
        } else {
            // Blue for standard info
            iconContainer.setStyle("-fx-background-color: #4facfe; -fx-background-radius: 5;"); 
        }
    }
}