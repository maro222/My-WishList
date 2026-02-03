package com.mycompany.java2wishlistapp;

import com.mycompany.model.Request;
import com.mycompany.model.Response;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;

public class ItemCardController {

    @FXML private ImageView imgItem;
    @FXML private Label lblName;
    @FXML private Label lblPrice;
    @FXML private ProgressBar progressBar;
    
    private int wishID; 
    
    private WishlistController parentController;

    public void setData(String name, double price, double collected, int itemID) {
        lblName.setText(name);
        lblPrice.setText(String.format("$%.2f", price)); // Use String.format for better currency display
        if (price > 0) {
            progressBar.setProgress(collected / price);
        } else {
            progressBar.setProgress(0.0);
        }
        if (collected >= (price - 0.01)) {
            // Completed -> Bright Green
            progressBar.setStyle("-fx-accent: #4cd137;"); 
        } else {
            // Incomplete -> Dark Black/Charcoal
            progressBar.setStyle("-fx-accent: #0096c9;"); 
        }
        this.wishID = itemID; 
    }
    
    public void setParentController(WishlistController parentController) {
        this.parentController = parentController;
    }
    
    @FXML
    private void handleDelete(ActionEvent event) {
        // Run network operations on a background thread to keep the UI responsive
        new Thread(() -> {
            try {
                TestClient client = App.getClient();
                Request request = new Request("DELETE_WISH", this.wishID);
                client.send(request); 
                Response response = client.getCurrentresponse();
                Platform.runLater(() -> {
                    if (response != null && response.isSuccess()) {
                        System.out.println("Item deleted successfully from DB.");
                        if (parentController != null) {
                            parentController.loadWishlistItems();
                        }
                    } else {
                        System.out.println("Something went wrong during deleting this item.");
                        showAlert("Deletion Failed", "Could not remove the item from your wishlist.");
                    }
                    client.currentresponse = null; 
                });
            } catch (Exception ex) {
                ex.printStackTrace();
                Platform.runLater(() -> showAlert("Connection Error", "Failed to communicate with the server."));
            }
        }).start();
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
