package com.mycompany.java2wishlistapp;

import com.mycompany.model.WishlistItem;
import java.io.IOException;
import java.util.List;
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
    
    private List<WishlistItem> wishlistItems;
    private int friendId;
    
    // This method is called by the Previous Controller (FriendCardController)
    public void setFriendInfo(String name) {
        lblFriendName.setText(name + "'s Wishlist");
        System.out.println("Friend name set to: " + name);
    }
    
    // New method to receive wishlist data
    public void setWishlistData(List<WishlistItem> items, int friendId) {
        this.wishlistItems = items;
        this.friendId = friendId;
        
        System.out.println("setWishlistData called with friendId: " + friendId);
        System.out.println("Wishlist items received: " + (items != null ? items.size() : "null"));
        
        if (items != null) {
            for (WishlistItem item : items) {
                System.out.println("Item: " + item.getName() + " - $" + item.getTotalPrice());
            }
        }
        
        loadItems();
    }
    
    private void loadItems() {
        System.out.println("loadItems() called");
        
        gridPane.getChildren().clear();
        
        if (wishlistItems == null) {
            System.out.println("wishlistItems is NULL");
            Label emptyLabel = new Label("No items in wishlist yet.");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            gridPane.add(emptyLabel, 0, 1);
            return;
        }
        
        if (wishlistItems.isEmpty()) {
            System.out.println("wishlistItems is EMPTY");
            Label emptyLabel = new Label("No items in wishlist yet.");
            emptyLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
            gridPane.add(emptyLabel, 0, 1);
            return;
        }
        
        System.out.println("Loading " + wishlistItems.size() + " items");
        
        int column = 0;
        int row = 1;
        
        try {
            for (WishlistItem item : wishlistItems) {
                System.out.println("Loading card for: " + item.getName());
                
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("FriendItemCard.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();
                
                FriendItemCardController controller = fxmlLoader.getController();
                
                // Pass real data using the correct getter methods
                controller.setData(
                    item.getName(),              // Item name
                    item.getTotalPrice(),        // Target/Total price
                    item.getCollectedAmount()    // Amount collected so far
                );
                
                // Pass the wishlist item ID for contributions
                controller.setWishlistItemId(item.getId());
                
                gridPane.add(anchorPane, column, row);
                System.out.println("Added card at column=" + column + ", row=" + row);
                
                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
            }
            
            System.out.println("Finished loading all items");
            
        } catch (IOException e) {
            System.out.println("ERROR loading FriendItemCard.fxml");
            e.printStackTrace();
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
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