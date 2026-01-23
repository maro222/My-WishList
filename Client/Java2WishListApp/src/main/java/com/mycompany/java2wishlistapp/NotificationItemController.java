/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java2wishlistapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
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
        
        // Change color based on type
        if (isSuccess) {
            iconContainer.setStyle("-fx-background-color: #4cd137;"); // Green for gifts
            // imgIcon.setImage(new Image(...)); // Set Gift Icon
        } else {
            iconContainer.setStyle("-fx-background-color: #4facfe;"); // Blue for info
        }
    }
}