/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java2wishlistapp;


import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class FriendCardController {

    @FXML private Circle friendCircle;
    @FXML private Label lblFriendName;
    @FXML private Label lblEmail;

    public void setData(String name, String email) {
        lblFriendName.setText(name);
        lblEmail.setText(email);
        
        // Set placeholder image
        try {
            Image im = new Image(getClass().getResourceAsStream("/images/profile_placeholder.png"));
            friendCircle.setFill(new ImagePattern(im));
        } catch (Exception e) { 
            // Ignore if image missing
        }
    }
    
    @FXML
    private void handleViewWishes(javafx.event.ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FriendWishlist.fxml"));
            Parent root = loader.load();

            // Pass the friend's name to the new screen
            FriendWishlistController controller = loader.getController();
            controller.setFriendInfo(lblFriendName.getText()); 

            // Switch the view
            BorderPane mainLayout = (BorderPane) ((Node) event.getSource()).getScene().lookup("#mainBorderPane");
            mainLayout.setCenter(root);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleUnfriend() {
        System.out.println("Unfriending: " + lblFriendName.getText());
        // TODO: Database logic to remove friend
    }
}