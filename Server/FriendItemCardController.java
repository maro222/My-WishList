/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java2wishlistapp;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;

public class FriendItemCardController {

    @FXML private Label lblName;
    @FXML private Label lblPrice;
    @FXML private Label lblCollected;
    @FXML private ProgressBar progressBar;
    @FXML private Button btnContribute;
    
    private double totalAmount;
    private double collectedAmount;
    private int wishlistItemId;

    public void setData(String name, double price, double collected) {
        this.totalAmount = price;
        this.collectedAmount = collected;
        
        lblName.setText(name);
        lblPrice.setText("$ " + price);
        lblCollected.setText("$ " + collected);
        progressBar.setProgress(collected / price);
        
        // If fully funded, disable button and change text
        if (collected >= price) {
            btnContribute.setText("Completed");
            btnContribute.setDisable(true);
            btnContribute.setStyle("-fx-background-color: #a0a0a0;"); // Grey out
        }
    }
    // Method to store the wishlist item ID
    public void setWishlistItemId(int id) {
        this.wishlistItemId = id;
    }
    @FXML
    private void handleContribute() {
        // Built-in JavaFX Dialog for simple input
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Contribute");
        dialog.setHeaderText("Contribute to " + lblName.getText());
        dialog.setContentText("Enter Amount ($):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                System.out.println("User paying: $" + amount);
                // TODO: JDBC Code -> Update item_amount in DB -> Deduct from User Balance
            } catch (NumberFormatException e) {
                System.out.println("Invalid amount entered");
            }
        });
    }
}