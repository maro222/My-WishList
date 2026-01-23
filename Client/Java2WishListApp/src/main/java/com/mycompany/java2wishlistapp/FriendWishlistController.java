/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java2wishlistapp;

import com.mycompany.java2wishlistapp.FriendItemCardController;
import java.io.IOException;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

public class FriendWishlistController {

    @FXML private Label lblFriendName;
    @FXML private GridPane gridPane;

    // This method is called by the Previous Controller (FriendCardController)
    public void setFriendInfo(String name) {
        lblFriendName.setText(name);
        loadItems();
    }
    
    private void loadItems() {
        // Mock Data: In real app, query DB using friend's ID
        int numberOfItems = 5; 
        int column = 0;
        int row = 1;

        try {
            for (int i = 0; i < numberOfItems; i++) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FriendItemCard.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                FriendItemCardController controller = fxmlLoader.getController();
                // Mock data: Name, Total Price, Collected So Far
                controller.setData("Gift " + (i+1), 300, 100 + (i*50));

                gridPane.add(anchorPane, column, row);
                
                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        // Logic to go BACK to Friends List
        try {
             FXMLLoader loader = new FXMLLoader(getClass().getResource("FriendsList.fxml"));
             Parent root = loader.load();
             
             // Look up the Main Dashboard BorderPane to swap the center content
             BorderPane mainLayout = (BorderPane) ((Node) event.getSource()).getScene().lookup("#mainBorderPane");
             if (mainLayout != null) {
                 mainLayout.setCenter(root);
             }
             
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}