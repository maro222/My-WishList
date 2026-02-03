/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java2wishlistapp;
/**
 * FXML Controller class
 *
 * @author Ahmed Yehia
 */
import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class ProfileController implements Initializable {

    @FXML private Circle profileCircle;
    @FXML private TextField txtUsername;
    @FXML private TextField txtEmail;
    private Label lblBalance;
    @FXML
    private TextField txtPhone;
    @FXML
    private TextField txtPhone1;
    @FXML
    private Button btnSave;
    
    TestClient client = App.getClient();

    @Override
    public void initialize(URL url, ResourceBundle rb) {  
        // 2. Set Profile Image inside the Circle
        // Note: Ensure you have a 'profile_placeholder.png' in your images folder
        
        
        
        try {
            txtUsername.setText(client.getUser().getUsername());
            txtEmail.setText(client.getUser().getEmail());
            Image im = new Image(getClass().getResourceAsStream("/images/profile_placeholder.png"));
            profileCircle.setFill(new ImagePattern(im));
        } catch (Exception e) {
            System.out.println("Profile image not found, using default color.");
        }
    }
    
    public void handleSave() {
        // Code to update user info in database
        
        System.out.println("Saving profile...");
    }
}
