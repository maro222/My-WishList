/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package com.mycompany.java2wishlistapp;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.shape.Circle;

public class RequestCardController {

    @FXML private Circle imgAvatar;
    @FXML private Label lblName;
    
    // We might need a reference to the main container to remove this card after clicking
    private AnchorPane cardRoot;

    public void setData(String name) {
        lblName.setText(name);
        // Set image logic here...
    }

    @FXML
    private void handleAccept() {
        System.out.println("Accepted: " + lblName.getText());
        // TODO: Update DB -> status = 'accepted'
        // TODO: Remove this card from the view
    }

    @FXML
    private void handleDecline() {
        System.out.println("Declined: " + lblName.getText());
        // TODO: Update DB -> delete request
    }
}