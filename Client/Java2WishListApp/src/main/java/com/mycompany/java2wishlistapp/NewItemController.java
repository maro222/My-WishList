/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java2wishlistapp;

import java.io.File;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class NewItemController {

    @FXML private ImageView imgPreview;
    @FXML private TextField txtName;
    @FXML private TextField txtPrice;
    
    private File selectedImageFile;

    @FXML
    private void handleBrowseImage(MouseEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Product Image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );
        
        // Show open dialog
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        selectedImageFile = fileChooser.showOpenDialog(stage);
        
        if (selectedImageFile != null) {
            Image image = new Image(selectedImageFile.toURI().toString());
            imgPreview.setImage(image);
        }
    }

    @FXML
    private void handleSave(ActionEvent event) {
        String name = txtName.getText();
        String priceText = txtPrice.getText();
        
        // Basic Validation
        if (name.isEmpty() || priceText.isEmpty()) {
            showAlert("Validation Error", "Please fill in all fields.");
            return;
        }
        
        try {
            double price = Double.parseDouble(priceText);
            
            // TODO: Here you would call your Database/Server logic to save the item
            System.out.println("Saving: " + name + " for $" + price);
            if(selectedImageFile != null) {
                System.out.println("Image: " + selectedImageFile.getPath());
            }

            // Close the window
            closeWindow(event);
            
        } catch (NumberFormatException e) {
            showAlert("Invalid Input", "Price must be a number.");
        }
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        closeWindow(event);
    }
    
    private void closeWindow(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}