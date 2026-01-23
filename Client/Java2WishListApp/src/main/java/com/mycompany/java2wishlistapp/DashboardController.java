/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java2wishlistapp;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;


import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import java.io.IOException;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
public class DashboardController implements Initializable {

    @FXML
    private BorderPane mainBorderPane; 
    @FXML
    private Label lblPageTitle;
    @FXML
    private Label user_name;
    @FXML
    private Button btnProfile;
    @FXML
    private Button btnMyWishlist;
    @FXML
    private Button btnFriends;
    @FXML
    private Button btnRequests;
    @FXML
    private Button btnLogout;
    @FXML
    private VBox contentArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // OPTIONAL: Load the Profile view by default when the app starts
//        loadPage("Profile"); 
        TestClient client = App.getClient();
        user_name.setText(client.getUser().getUsername());
    }    

    // --- BUTTON ACTIONS ---
    
    @FXML
    private void handleShowProfile(ActionEvent event) {
        loadPage("Profile"); // This name must match your FXML file name exactly
    }

    @FXML
    private void handleShowMyWishlist(ActionEvent event) {
        // You can create a Wishlist.fxml later and uncomment this
        // loadPage("Wishlist");
        loadPage("Wishlist");
    }

    @FXML
    private void handleShowFriends(ActionEvent event) {
         loadPage("FriendsList");
    }
    
    @FXML
    private void handleLogout(ActionEvent event) {
        try {
            // 1. Load the Login FXML file
            // Note: Ensure "Login.fxml" is in the same directory (package) as this class
            FXMLLoader loader = new FXMLLoader(getClass().getResource("Login.fxml"));
            Parent root = loader.load();

            // 2. Get the current Stage (Window) from the event source (the button)
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // 3. Create the new Scene
            Scene scene = new Scene(root);
            
            // 4. Set the Scene to the Stage
            stage.setScene(scene);
            stage.setTitle("i-Wish Login"); // Optional: Update the window title
            stage.centerOnScreen(); // Nice touch: center the window
            stage.show();
            
            System.out.println("User signed out successfully.");
            
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error loading Login.fxml. Check the file path.");
        }
    }

    // --- HELPER METHOD TO SWITCH VIEWS ---
    
    private void loadPage(String pageName) {
        try {
            // 1. Load the FXML file
            Parent root = FXMLLoader.load(getClass().getResource(pageName + ".fxml"));
            
            // 2. Set it to the CENTER of the BorderPane
            mainBorderPane.setCenter(root);
            
        } catch (IOException ex) {
//            Logger.getLogger(DashboardController.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Error loading view: " + pageName);
        }
    }
    
    @FXML    
    private void handleShowRequests(ActionEvent event) {
        // Logic to switch back to Login Scene
        loadPage("FriendRequests");
    }
    
    @FXML    
    private void handleShowNotifications(ActionEvent event) {
        // Logic to switch back to Login Scene
        loadPage("Notifications");
    }
}
