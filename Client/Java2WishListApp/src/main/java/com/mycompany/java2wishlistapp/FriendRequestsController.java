/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java2wishlistapp;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class FriendRequestsController implements Initializable {

    @FXML
    private VBox requestsList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mock Data: 4 pending requests
        int pendingRequests = 4;

        try {
            for (int i = 0; i < pendingRequests; i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("RequestCard.fxml"));
                AnchorPane card = fxmlLoader.load();

                RequestCardController controller = fxmlLoader.getController();
                controller.setData("User " + (i + 1));

                requestsList.getChildren().add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}