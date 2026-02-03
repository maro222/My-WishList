package com.mycompany.java2wishlistapp;

import com.mycompany.model.Request;
import com.mycompany.model.Response;
import com.mycompany.model.User;
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

public class FriendsListController implements Initializable {
    
    @FXML
    private GridPane friendsGrid;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadFriends();
    }
    
    // PUBLIC so the Child Card can call it to refresh the list
    public void loadFriends() {
        // Run in background thread to prevent UI freezing
        new Thread(() -> {
            try {
                TestClient client = App.getClient();
                if (client == null || client.getUser() == null) {
                    System.out.println("Client or user not ready");
                    return;
                }
                
                int userId = client.getUser().getId();
                
                // 1. Clear previous response
                client.currentresponse = null;
                
                // 2. Send Request
                Request request = new Request("GET_FRIENDS", userId);
                client.send(request);
                
                // 3. Smart Wait (Polling)
                int timeout = 20; // 2 seconds max
                while (client.getCurrentresponse() == null && timeout > 0) {
                    Thread.sleep(100);
                    timeout--;
                }
                
                Response response = client.getCurrentresponse();
                
                // 4. Update UI on JavaFX Thread
                Platform.runLater(() -> {
                    if (response == null || !response.isSuccess()) {
                        System.out.println("Failed to load friends or timeout");
                        return;
                    }
                    
                    List<User> friends = (List<User>) response.getData();
                    displayFriends(friends);
                });
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
    
    private void displayFriends(List<User> friends) {
        friendsGrid.getChildren().clear();
        int column = 0;
        int row = 1;
        
        try {
            for (User friend : friends) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("FriendCard.fxml"));
                AnchorPane pane = loader.load();
                
                FriendCardController controller = loader.getController();
                
                // Pass Data
                controller.setData(
                        friend.getUsername(),
                        friend.getEmail(),
                        friend.getId()
                );
                
                // *** CRITICAL STEP: Pass reference to THIS controller ***
                controller.setParentController(this);
                
                friendsGrid.add(pane, column, row);
                column++;
                if (column == 2) { // Assuming 2 columns per row
                    column = 0;
                    row++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}