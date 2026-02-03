package com.mycompany.java2wishlistapp;

import com.mycompany.model.Request;
import com.mycompany.model.Response;
import com.mycompany.model.WishlistItem;
import java.util.List;
import javafx.application.Platform;
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
    
    // Reference to the Parent List
    private FriendsListController parentController;
    
    // Setter for Parent
    public void setParentController(FriendsListController parent) {
        this.parentController = parent;
    }
    
    public void setData(String name, String email, int friendId) {
        this.friendId = friendId;
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
    private void handleUnfriend() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirm Unfriend");
        confirmAlert.setHeaderText(null);
        confirmAlert.setContentText("Are you sure you want to unfriend " + lblFriendName.getText() + "?");
        
        confirmAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                performUnfriend();
            }
        });
    }
    
    private void performUnfriend() {
        // Run in background to avoid freeze
        new Thread(() -> {
            try {
                TestClient client = App.getClient();
                if (client == null) return;
                
                int userId = client.getUser().getId();
                int[] unfriendData = new int[]{userId, friendId};
                
                // 1. Clear old
                client.currentresponse = null;
                
                // 2. Send
                Request request = new Request("REMOVE_FRIEND", unfriendData);
                client.send(request);
                
                // 3. Smart Wait
                int timeout = 20;
                while (client.getCurrentresponse() == null && timeout > 0) {
                    Thread.sleep(100);
                    timeout--;
                }
                
                Response response = client.getCurrentresponse();
                
                // 4. Update UI
                Platform.runLater(() -> {
                    if (response != null && response.isSuccess()) {
                        showAlert("Success", "Friend removed successfully.");
                        
                        // *** REFRESH THE PARENT LIST ***
                        if (parentController != null) {
                            parentController.loadFriends();
                        }
                    } else {
                        showAlert("Error", "Failed to remove friend.");
                    }
                });
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleViewWishes(javafx.event.ActionEvent event) {
        // Also wrapping this in a thread is good practice, though strictly navigation
        // depends on the fetch. Here is the threaded version:
        
        new Thread(() -> {
            try {
                TestClient client = App.getClient();
                client.currentresponse = null;
                
                client.send(new Request("GET_FRIEND_WISHLIST", friendId));
                
                int timeout = 20;
                while(client.getCurrentresponse() == null && timeout > 0) {
                    Thread.sleep(100);
                    timeout--;
                }
                
                Response response = client.getCurrentresponse();
                
                Platform.runLater(() -> {
                     if (response != null && response.isSuccess()) {
                        List<WishlistItem> wishlist = (List<WishlistItem>) response.getData();
                        navigateToFriendWishlist(event, wishlist);
                     } else {
                         showAlert("Error", "Could not load friend's wishlist");
                     }
                });
                
            } catch(Exception e) { e.printStackTrace(); }
        }).start();
    }
    
    private void navigateToFriendWishlist(javafx.event.ActionEvent event, List<WishlistItem> wishlist) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FriendWishlist.fxml"));
            Parent root = loader.load();
            
            FriendWishlistController controller = loader.getController();
            controller.setFriendInfo(lblFriendName.getText());
            controller.setWishlistData(wishlist, friendId);
            
            BorderPane mainLayout = (BorderPane) ((Node) event.getSource()).getScene().lookup("#mainBorderPane");
            if (mainLayout != null) {
                mainLayout.setCenter(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.show(); // show() is non-blocking, better for threaded contexts than showAndWait inside runLater
    }
}