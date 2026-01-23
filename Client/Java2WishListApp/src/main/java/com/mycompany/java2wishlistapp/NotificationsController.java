/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java2wishlistapp;

import com.mycompany.java2wishlistapp.NotificationItemController;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class NotificationsController implements Initializable {

    @FXML private VBox notificationList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mock Data
        addNotification("Wish Fulfilled!", "Your 'Apple Watch' goal has been reached!", "10 mins ago", true);
        addNotification("Contribution", "Sarah paid $20 towards your 'Gaming Chair'.", "1 hour ago", false);
        addNotification("Friend Request", "You are now friends with Mohamed.", "Yesterday", false);
    }
    
    private void addNotification(String title, String msg, String time, boolean isSuccess) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NotificationItem.fxml"));
            AnchorPane item = fxmlLoader.load();
            
            NotificationItemController controller = fxmlLoader.getController();
            controller.setData(title, msg, time, isSuccess);
            
            notificationList.getChildren().add(item);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClearAll(ActionEvent event) {
        notificationList.getChildren().clear();
        System.out.println("Notifications cleared.");
    }
}