/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java2wishlistapp;

import com.mycompany.model.Request;
import com.mycompany.model.Response;
import java.io.File;
import javafx.application.Platform;
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
    
    private boolean itemAddedSuccessfully = false; // Flag to track success
    private File selectedImageFile;
    TestClient client = App.getClient();

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
        String name = txtName.getText().trim();
        String priceText = txtPrice.getText().trim();
        
        if (name.isEmpty() || priceText.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Validation Error", "Please fill in all fields.");
            return;
        }
        
        try {
            double price = Double.parseDouble(priceText);
            if (price < 0) {
                 showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price cannot be negative.");
                 return;
            }
            
            String userId = String.valueOf(client.getUser().getId());
            
            // Data payload: {name, price, userId}
            String[] itemDetails = {name, priceText, userId};
            Request request = new Request("INSERT_NEW_WISH", itemDetails);
            
            // Send request in a background thread
            new Thread(() -> {
                try {
                    client.send(request);
                    Response response = client.getCurrentresponse() ;
                    Platform.runLater(() -> {
                        if (response != null && response.isSuccess()) {
                            itemAddedSuccessfully = true; // Set flag on success
                            closeWindow(event); // Close window automatically
                        } else {
                            String message = (response != null) ? response.getMessage() : "Server communication error.";
                            showAlert(Alert.AlertType.ERROR, "Save Failed", message);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    Platform.runLater(() -> showAlert(Alert.AlertType.ERROR, "Error", "Could not connect to the server."));
                }
            }).start();
            
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid Input", "Price must be a valid number.");
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
    
    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
    public boolean isItemAddedSuccessfully() {
        return itemAddedSuccessfully;
    }


}