package com.mycompany.java2wishlistapp;

import com.mycompany.model.Request;
import com.mycompany.model.Response;
import com.mycompany.model.WishlistItem;
import java.io.IOException;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class FriendCardController {
    
    @FXML private Circle friendCircle;
    @FXML private Label lblFriendName;
    @FXML private Label lblEmail;
    
    private int friendId;
    
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
    
    // Overloaded method to include friendId
    public void setData(String name, String email, int friendId) {
        this.friendId = friendId;
        setData(name, email);
        System.out.println("Friend card created - Name: " + name + ", ID: " + friendId);
    }
    
    @FXML
    private void handleViewWishes(javafx.event.ActionEvent event) {
        System.out.println("=== handleViewWishes called for friend: " + lblFriendName.getText() + " (ID: " + friendId + ") ===");
        
        try {
            TestClient client = App.getClient();
            if (client == null) {
                System.out.println("ERROR: Client is null");
                showAlert("Error", "Server not connected.");
                return;
            }
            
            System.out.println("Sending GET_FRIEND_WISHLIST request for friendId: " + friendId);
            
            // Request friend's wishlist from server
            Request request = new Request("GET_FRIEND_WISHLIST", friendId);
            client.send(request);
            
            Thread.sleep(1000);
            
            Response response = client.getCurrentresponse();
            
            System.out.println("Response received: " + (response != null ? "Yes" : "NULL"));
            if (response != null) {
                System.out.println("Response success: " + response.isSuccess());
                System.out.println("Response message: " + response.getMessage());
                System.out.println("Response data: " + response.getData());
            }
            
            if (response == null || !response.isSuccess()) {
                System.out.println("ERROR: Failed to get wishlist");
                showAlert("Error", "Failed to load friend's wishlist.");
                return;
            }
            
            List<WishlistItem> wishlist = (List<WishlistItem>) response.getData();
            System.out.println("Wishlist size: " + (wishlist != null ? wishlist.size() : "null"));
            
            if (wishlist != null) {
                for (WishlistItem item : wishlist) {
                    System.out.println("  - " + item.getName() + ": $" + item.getTotalPrice() + " (collected: $" + item.getCollectedAmount() + ")");
                }
            }
            
            // Load FriendWishlist view
            System.out.println("Loading FriendWishlist.fxml...");
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FriendWishlist.fxml"));
            Parent root = loader.load();
            
            // Pass the friend's info and wishlist data to the controller
            FriendWishlistController controller = loader.getController();
            System.out.println("Controller obtained: " + (controller != null));
            
            controller.setFriendInfo(lblFriendName.getText());
            System.out.println("About to call setWishlistData...");
            controller.setWishlistData(wishlist, friendId);
            System.out.println("setWishlistData called successfully");
            
            // Switch the view
            BorderPane mainLayout = (BorderPane) ((Node) event.getSource()).getScene().lookup("#mainBorderPane");
            mainLayout.setCenter(root);
            
            // Clear response
            client.currentresponse = null;
            
            System.out.println("=== View switched successfully ===");
            
        } catch (Exception e) {
            System.out.println("EXCEPTION in handleViewWishes:");
            e.printStackTrace();
            showAlert("Error", "Could not load friend's wishlist.");
        }
    }
    
    @FXML
    private void handleUnfriend() {
        // Show confirmation dialog
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Unfriend");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to unfriend " + lblFriendName.getText() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                unfriendUser();
            }
        });
    }
    
    private void unfriendUser() {
        try {
            TestClient client = App.getClient();
            if (client == null || client.getUser() == null) {
                showAlert("Error", "Server not connected.");
                return;
            }
            
            int userId = client.getUser().getId();
            int[] unfriendData = new int[]{userId, friendId};
            
            Request request = new Request("REMOVE_FRIEND", unfriendData);
            client.send(request);
            
            Thread.sleep(1000);
            
            Response response = client.getCurrentresponse();
            
            if (response != null && response.isSuccess()) {
                showAlert("Success", "Friend removed successfully.");
                
                // Refresh the friends list
                javafx.application.Platform.runLater(() -> {
                    refreshFriendsList();
                });
            } else {
                showAlert("Error", "Failed to remove friend.");
            }
            
            // Clear response
            client.currentresponse = null;
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while removing friend.");
        }
    }
    
    private void refreshFriendsList() {
        try {
            // Reload the FriendsList view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FriendsList.fxml"));
            Parent root = loader.load();
            
            // Get the main BorderPane and update center
            BorderPane mainLayout = (BorderPane) lblFriendName.getScene().lookup("#mainBorderPane");
            if (mainLayout != null) {
                mainLayout.setCenter(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}