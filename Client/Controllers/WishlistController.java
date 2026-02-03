package com.mycompany.java2wishlistapp;

import com.mycompany.model.Request;
import com.mycompany.model.Response;
import com.mycompany.model.WishlistItem;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.control.Button;

public class WishlistController implements Initializable {

    @FXML private GridPane gridPane;
    @FXML private Button newitem_btn;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadWishlistItems();
    }
    
    // PUBLIC so NewItemController can call it if needed
    public void loadWishlistItems() {
        new Thread(() -> {
            try {
                TestClient client = App.getClient();
                if (client == null) return;

                // 1. Clear old data
                client.currentresponse = null;

                // 2. Send Request
                Request request = new Request("GET_USER_WISHLIST", client.getUser());
                client.send(request);
                
                // 3. SMART WAIT (Polling)
                int timeout = 20; // Wait max 2 seconds
                while (client.getCurrentresponse() == null && timeout > 0) {
                    Thread.sleep(100);
                    timeout--;
                }
                
                Response response = client.getCurrentresponse();
                
                // 4. Update UI
                Platform.runLater(() -> {
                    if (response != null && response.isSuccess()) {
                        List<WishlistItem> list = (List<WishlistItem>) response.getData();
                        displayItems(list);
                    } else {
                        System.out.println("Failed to load wishlist");
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }

    private void displayItems(List<WishlistItem> list) {
        gridPane.getChildren().clear(); 
        if (list == null || list.isEmpty()) {
            System.out.println("Wishlist is empty or server returned null data.");
            // Optional: You could add a Label here saying "No items found"
            return; 
        }
                
        int column = 0;
        int row = 1;

        try {
            for (WishlistItem item : list) {
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("ItemCard.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                ItemCardController itemController = fxmlLoader.getController();

                itemController.setData(item.getName(), item.getTotalPrice(), item.getCollectedAmount(), item.getId());

                // Pass THIS controller so the card can call loadWishlistItems() on delete
                itemController.setParentController(this); 

                gridPane.add(anchorPane, column, row);

                column++;
                if (column == 3) {
                    column = 0;
                    row++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @FXML
    private void handleNewItem(javafx.event.ActionEvent event) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NewItemDialog.fxml"));
            Parent parent = fxmlLoader.load();
            
            NewItemController newItemController = fxmlLoader.getController();
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setTitle("Add New Wish");
            stage.setScene(new Scene(parent));
            
            stage.showAndWait(); 
            
            if (newItemController.isItemAddedSuccessfully()) {
                loadWishlistItems(); // Refresh!
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}