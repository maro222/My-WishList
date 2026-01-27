/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java2wishlistapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ItemCardController {

    @FXML private ImageView imgItem;
    @FXML private Label lblName;
    @FXML private Label lblPrice;
    @FXML private ProgressBar progressBar;

    // Method to set data for this specific card
    public void setData(String name, double price, double collected) {
        lblName.setText(name);
        lblPrice.setText("$ " + price);
        progressBar.setProgress(collected / price);
        
        // Example image (ensure you have a placeholder)
        // Image image = new Image(getClass().getResourceAsStream("/images/gift_icon.png"));
        // imgItem.setImage(image);
    }
    
    @FXML
    private void handleDelete() {
        System.out.println("Remove item clicked!");
        // Logic to delete from DB
    }
}
