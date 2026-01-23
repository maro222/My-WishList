/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java2wishlistapp;

import com.mycompany.model.Request;
import com.mycompany.model.Response;
import com.mycompany.model.WishlistItem;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
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

    @FXML
    private GridPane gridPane;
    @FXML
    private Button newitem_btn;
    
    TestClient client;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Mock Data: Imagine this coming from your Database
        
        client = App.getClient();
        Request request = new Request("GET_USER_WISHLIST", client.getUser());
        
        try {
            client.send(request);
            Thread.sleep(1000);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
        Response response = client.getCurrentresponse();
        //List<WishlistItem> list = (List<WishlistItem>) response.getData();
        
        
        int numberOfItems = 8; 
        int column = 0;
        int row = 1;

        try {
            for (int i = 0; i < numberOfItems; i++) {
                // 1. Load the Item Card FXML
                FXMLLoader fxmlLoader = new FXMLLoader();
                fxmlLoader.setLocation(getClass().getResource("ItemCard.fxml"));
                AnchorPane anchorPane = fxmlLoader.load();

                // 2. Pass Data to the Controller
                ItemCardController itemController = fxmlLoader.getController();
                // (Simulating different prices/progress for demo)
                itemController.setData("Item " + (i+1), 200 + (i*50), 50 + (i*20));

                // 3. Add to Grid
                gridPane.add(anchorPane, column, row); // (child, column, row)
                
                // 4. Grid Logic (3 columns per row)
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
            
            Scene scene = new Scene(parent);
            Stage stage = new Stage();
            
            // This makes the window "Modal" (cannot click background)
            stage.initModality(Modality.APPLICATION_MODAL); 
            stage.setTitle("Add New Wish");
            stage.setScene(scene);
            stage.showAndWait(); // Wait until the popup closes
            
            // Optional: Refresh the grid here if an item was added
            // loadWishlistItems();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}