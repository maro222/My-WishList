/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java2wishlistapp;

import com.mycompany.model.Contribution;
import com.mycompany.model.Request;
import com.mycompany.model.Response;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextInputDialog;
import java.util.Optional;
import javafx.scene.control.Alert;

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
                
            progressBar.setStyle("-fx-accent: #4cd137;"); 
        } else {
            // Reset to default (Blue) just in case
            progressBar.setStyle("-fx-accent: #1a1a1a;"); 
        }
    }
    // Method to store the wishlist item ID
    public void setWishlistItemId(int id) {
        this.wishlistItemId = id;
    }

    
    
    @FXML
    private void handleContribute() {
        // Calculate remaining amount for the hint
        double remaining = totalAmount - collectedAmount;

        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Contribute");
        dialog.setHeaderText("Contribute to " + lblName.getText());
        dialog.setContentText("Enter Amount ($) (Max: " + remaining + "):");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(amountStr -> {
            try {
                double amount = Double.parseDouble(amountStr);
                
                if (amount <= 0) {
                    showAlert("Invalid Amount", "Amount must be positive.");
                    return;
                }
                
                TestClient client = App.getClient();
                if (client == null || client.getUser() == null) {
                    showAlert("Error", "Not connected to server.");
                    return;
                }

                // 3. Create Contribution Object
                // ID is 0 (DB sets it), Payer is Me, Wish is this item, Amount is input
                int myId = client.getUser().getId();
                Contribution contribution = new Contribution(0, myId, this.wishlistItemId, amount);

                Request req = new Request("CONTRIBUTE", contribution);
                client.send(req);

                Thread.sleep(500); 
                Response res = client.getCurrentresponse();

                if (res != null && res.isSuccess()) {
                    showAlert("Success", "Contribution successful!");
                    
                    this.collectedAmount += amount;
                    lblCollected.setText("$ " + this.collectedAmount);
                    
                    double newProgress = this.collectedAmount / this.totalAmount;
                    progressBar.setProgress(newProgress);
                    
                    // --- COLOR LOGIC ---
                    if (this.collectedAmount >= this.totalAmount) {
                        btnContribute.setDisable(true);
                        btnContribute.setText("Completed");
                        
                        // Switch to Green immediately
                        progressBar.setStyle("-fx-accent: #4cd137;"); 
                    } else {
                        // Keep it Black
                         progressBar.setStyle("-fx-accent: #1a1a1a;"); 
                    }
                } else {
                    String errorMsg = (res != null) ? res.getMessage() : "No response from server";
                    showAlert("Failed", errorMsg);
                }
                
                // Clear response buffer
                if(client != null) client.currentresponse = null;

            } catch (NumberFormatException e) {
                showAlert("Error", "Invalid number format.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    
}