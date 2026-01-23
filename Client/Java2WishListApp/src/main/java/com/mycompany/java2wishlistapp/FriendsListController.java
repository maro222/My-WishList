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
import javafx.scene.layout.GridPane;

public class FriendsListController implements Initializable {

    @FXML
    private GridPane friendsGrid;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mock Data
        int numberOfFriends = 6; 

        int column = 0;
        int row = 1;

        try {
            for (int i = 0; i < numberOfFriends; i++) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("FriendCard.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                FriendCardController controller = fxmlLoader.getController();
                controller.setData("Friend " + (i+1), "friend" + (i+1) + "@example.com");

                // Add to Grid
                friendsGrid.add(anchorPane, column, row);
                
                // Logic for 2 columns per row (since cards are wide)
                column++;
                if (column == 2) {
                    column = 0;
                    row++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}