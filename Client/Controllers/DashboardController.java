package com.mycompany.java2wishlistapp;

import com.mycompany.model.Request;
import com.mycompany.model.Response;
import com.mycompany.model.User;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;

public class DashboardController implements Initializable {

    @FXML private BorderPane mainBorderPane;
    //@FXML private TextField txtGlobalSearch;
    @FXML private TextField txtHomeSearch;
    @FXML private FlowPane resultsContainer;
    @FXML private Label username;
    @FXML private Button btnNotifications; 
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize as needed
        TestClient client = App.getClient();
        username.setText(client.getUser().getUsername());
        checkNotificationStatus();
    }    

    @FXML
    public void handleShowProfile() {
        loadPage("Profile");
    }

    @FXML
    public void handleShowMyWishlist() {
        loadPage("Wishlist");
    }

    @FXML
    public void handleShowFriends() {
        loadPage("FriendsList");
    }
    
    @FXML
    public void handleLogout(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("i-Wish Login");
            stage.centerOnScreen();
            stage.show();
            System.out.println("User signed out successfully.");
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading Login.fxml. Check the file path.");
        }
    }

    public void loadPage(String pageName) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(pageName + ".fxml"));
            mainBorderPane.setCenter(root);
        } catch (IOException ex) {
            System.out.println("Error loading view: " + pageName);
            ex.printStackTrace();
        }
    }
    
    @FXML    
    public void handleShowRequests() {
        loadPage("FriendRequests");
    }
    
    @FXML    
    public void handleShowNotifications() {
        loadPage("Notifications");
    }
    
    @FXML
    public void handleShowHome(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("MainDashboard.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error: Could not load Dashboard.fxml");
        }
    }
    
    @FXML
    public void handleHomeSearch(ActionEvent event) {
        String query = txtHomeSearch.getText().trim();
        resultsContainer.getChildren().clear();
        
        if (query.isEmpty()) {
            System.out.println("Search query is empty");
            return;
        }

        System.out.println("=== Starting search for: " + query + " ===");
        
        try {
            TestClient client = App.getClient();
            if (client == null) {
                System.out.println("ERROR: Client is null");
                return;
            }
            
            System.out.println("Client obtained, user: " + client.getUser().getUsername());
            
            // Send search request
            Request request = new Request("SEARCH_USER", query);
            System.out.println("Sending SEARCH_USER request...");
            client.send(request);
            
            System.out.println("Request sent, waiting for response...");
            Thread.sleep(1000);
            
            Response response = client.getCurrentresponse();
            System.out.println("Response received: " + (response != null ? "YES" : "NULL"));
            
            if (response == null) {
                System.out.println("ERROR: Response is null");
                return;
            }
            
            System.out.println("Response success: " + response.isSuccess());
            System.out.println("Response message: " + response.getMessage());
            
            if (!response.isSuccess()) {
                System.out.println("Search failed");
                return;
            }
            
            List<User> users = (List<User>) response.getData();
            System.out.println("Users received: " + (users != null ? users.size() : "NULL"));
            
            if (users == null || users.isEmpty()) {
                System.out.println("No users found matching: " + query);
                // Show "No results" message
                Label noResults = new Label("No users found");
                noResults.setStyle("-fx-font-size: 16px; -fx-text-fill: gray;");
                resultsContainer.getChildren().add(noResults);
                client.currentresponse = null;
                return;
            }
            
            // Get current user ID to exclude from results
            int currentUserId = client.getUser().getId();
            System.out.println("Current user ID: " + currentUserId);
            
            // Display results
            int cardsDisplayed = 0;
            for (User user : users) {
                System.out.println("Processing user: " + user.getUsername() + " (ID: " + user.getId() + ")");
                
                // Don't show current user in results
                if (user.getId() == currentUserId) {
                    System.out.println("Skipping current user");
                    continue;
                }
                
                try {
                    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("SearchResultCard.fxml"));
                    Parent card = fxmlLoader.load();
                    
                    SearchResultCardController controller = fxmlLoader.getController();
                    controller.setUserData(user);  // Use setUserData, not setData
                    
                    resultsContainer.getChildren().add(card);
                    cardsDisplayed++;
                    System.out.println("Card added for: " + user.getUsername());
                    
                } catch (IOException e) {
                    System.out.println("ERROR loading card:");
                    e.printStackTrace();
                }
            }
            
            System.out.println("Total cards displayed: " + cardsDisplayed);
            
            // Clear response
            client.currentresponse = null;
            System.out.println("=== Search complete ===");
            
        } catch (Exception e) {
            System.out.println("EXCEPTION during search:");
            e.printStackTrace();
        }
    }
    
    private void checkNotificationStatus() {
        try {
            TestClient client = App.getClient();
            if (client == null || client.getUser() == null) return;

            // 1. Send the "Check" request
            Request req = new Request("CHECK_UNREAD", client.getUser().getId());
            client.send(req);

            // 2. Wait for response
            Thread.sleep(200); // Short wait is fine here
            Response res = client.getCurrentresponse();

            if (res != null && res.isSuccess()) {
                boolean hasUnread = (boolean) res.getData();

                if (hasUnread) {
                    // STYLE 1: Turn the icon/text Red
                    btnNotifications.setStyle("-fx-text-fill: red; -fx-background-color: transparent;");
                    
                    // OR STYLE 2: If using an ImageView inside the button, swap the image
                    // ImageView view = (ImageView) btnNotifications.getGraphic();
                    // view.setImage(new Image(getClass().getResourceAsStream("/images/bell_red.png")));
                } else {
                    // Default Style (White/Grey)
                    btnNotifications.setStyle("-fx-text-fill: #a0a0a0; -fx-background-color: transparent;");
                }
            }
            client.currentresponse = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}