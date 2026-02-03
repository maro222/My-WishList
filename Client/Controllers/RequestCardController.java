package com.mycompany.java2wishlistapp;

import com.mycompany.model.Request;
import com.mycompany.model.Response;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class RequestCardController {

    @FXML private Label lblName;
    private int senderId;
    
    // Parent Reference
    private FriendRequestsController parent;

    public void setData(String username, int id) {
        lblName.setText(username);
        this.senderId = id;
    }

    public void setParentController(FriendRequestsController parent) {
        this.parent = parent;
    }

    @FXML
    private void handleAccept() {
        sendAction("ACCEPT_FRIEND");
    }

    @FXML
    private void handleDecline() {
        sendAction("DECLINE_FRIEND");
    }
    
    private void sendAction(String actionType) {
        new Thread(() -> {
            try {
                TestClient client = App.getClient();
                int myId = client.getUser().getId();
                int[] data = {senderId, myId}; // [sender, receiver]
                
                client.currentresponse = null;
                client.send(new Request(actionType, data));
                
                // Wait
                while(client.getCurrentresponse() == null) Thread.sleep(50);
                Response res = client.getCurrentresponse();

                Platform.runLater(() -> {
                    if (res != null && res.isSuccess()) {
                        if(parent != null) parent.loadRequests();
                    }
                });
            } catch(Exception e) { e.printStackTrace(); }
        }).start();
    }
}