package com.mycompany.java2wishlistapp;

import com.mycompany.model.Request;
import com.mycompany.model.Response;
import com.mycompany.model.User;
import java.io.IOException;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class AddFriendController {
    
    @FXML private TextField txtSearch;
    @FXML private VBox searchResultsBox;
    
    @FXML
    private void handleSearch(ActionEvent event) {
        String searchQuery = txtSearch.getText().trim();
        
        if (searchQuery.isEmpty()) {
            showAlert("Error", "Please enter a username or email to search.");
            return;
        }
        
        try {
            TestClient client = App.getClient();
            if (client == null) {
                showAlert("Error", "Server not connected.");
                return;
            }
            
            System.out.println("Searching for: " + searchQuery);
            
            Request request = new Request("SEARCH_USER", searchQuery);
            client.send(request);
            
            Thread.sleep(1000);
            
            Response response = client.getCurrentresponse();
            
            if (response == null || !response.isSuccess()) {
                showAlert("Error", "Search failed.");
                return;
            }
            
            List<User> users = (List<User>) response.getData();
            displaySearchResults(users);
            
            client.currentresponse = null;
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "An error occurred during search.");
        }
    }
    
    private void displaySearchResults(List<User> users) {
        searchResultsBox.getChildren().clear();
        
        if (users == null || users.isEmpty()) {
            Label noResults = new Label("No users found.");
            noResults.setStyle("-fx-font-size: 14px; -fx-text-fill: gray;");
            searchResultsBox.getChildren().add(noResults);
            return;
        }
        
        TestClient client = App.getClient();
        int currentUserId = client.getUser().getId();
        
        for (User user : users) {
            // Don't show current user in results
            if (user.getId() == currentUserId) {
                continue;
            }
            
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("UserSearchCard.fxml"));
                Node card = loader.load();
                
                UserSearchCardController controller = loader.getController();
                controller.setUserData(user);
                
                searchResultsBox.getChildren().add(card);
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FriendsList.fxml"));
            Parent root = loader.load();
            
            BorderPane mainLayout = (BorderPane) ((Node) event.getSource()).getScene().lookup("#mainBorderPane");
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