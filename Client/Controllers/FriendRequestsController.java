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
import javafx.scene.layout.VBox;

public class FriendRequestsController implements Initializable {

    @FXML private VBox requestsList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadRequests();
    }

    public void loadRequests() {
        new Thread(() -> {
            try {
                TestClient client = App.getClient();
                client.currentresponse = null;
                
                Request request = new Request("GET_REQUEST_LIST", client.getUser());
                client.send(request);
                
                // Smart Wait
                int timeout = 20;
                while(client.getCurrentresponse() == null && timeout > 0) {
                    Thread.sleep(100);
                    timeout--;
                }
                
                Response response = client.getCurrentresponse();

                Platform.runLater(() -> {
                    if (response != null && response.isSuccess()) {
                        List<User> friends = (List<User>) response.getData();
                        displayRequests(friends);
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
    
    private void displayRequests(List<User> friends) {
        requestsList.getChildren().clear();
        try {
            for (User user : friends) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("RequestCard.fxml"));
                AnchorPane card = fxmlLoader.load();

                RequestCardController controller = fxmlLoader.getController();
                controller.setData(user.getUsername(), user.getId());
                
                // PASS THIS CONTROLLER 
                controller.setParentController(this);

                requestsList.getChildren().add(card);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}