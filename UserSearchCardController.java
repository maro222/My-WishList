package com.mycompany.java2wishlistapp;

import com.mycompany.model.Request;
import com.mycompany.model.Response;
import com.mycompany.model.User;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class UserSearchCardController {
    
    @FXML private Label lblUsername;
    @FXML private Label lblEmail;
    @FXML private Button btnAddFriend;
    
    private User user;
    
    public void setUserData(User user) {
        this.user = user;
        lblUsername.setText(user.getUsername());
        lblEmail.setText(user.getEmail());
    }
    
    @FXML
    private void handleAddFriend() {
        try {
            TestClient client = App.getClient();
            if (client == null || client.getUser() == null) {
                showAlert("Error", "Server not connected.");
                return;
            }
            
            int currentUserId = client.getUser().getId();
            int[] friendRequestData = new int[]{currentUserId, user.getId()};
            
            Request request = new Request("ADD_FRIEND", friendRequestData);
            client.send(request);
            
            Thread.sleep(1000);
            
            Response response = client.getCurrentresponse();
            
            if (response != null && response.isSuccess()) {
                showAlert("Success", "Friend request sent to " + user.getUsername());
                btnAddFriend.setDisable(true);
                btnAddFriend.setText("Request Sent");
            } else {
                showAlert("Error", response != null ? response.getMessage() : "Failed to send friend request");
            }
            
            client.currentresponse = null;
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred while sending friend request.");
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