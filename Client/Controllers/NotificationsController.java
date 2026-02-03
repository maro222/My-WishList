package com.mycompany.java2wishlistapp;

import com.mycompany.model.Notification;
import com.mycompany.model.Request;
import com.mycompany.model.Response;
//import com.mycompany.TestClient;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

public class NotificationsController implements Initializable {

    @FXML private VBox notificationList;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadNotificationsFromServer();
    }
    
    private void loadNotificationsFromServer() {
        try {
            TestClient client = App.getClient();
            if (client == null || client.getUser() == null) return;


            Request req = new Request("GET_NOTIFICATIONS", client.getUser().getId());
            client.send(req);
            
            Thread.sleep(1000); 
            Response res = client.getCurrentresponse();
            
            if (res != null && res.isSuccess()) {
                List<Notification> notifs = (List<Notification>) res.getData();
                
                notificationList.getChildren().clear(); // Clear old items
                
                if (notifs != null) {
                    for (Notification n : notifs) {
                        addNotificationItem("New Contribution", n.getMessage(), n.getTime(), true);
                    }
                }
            } else {
                System.out.println("Failed to load notifications or list is empty.");
            }
            
            if(client != null) client.currentresponse = null;

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private void addNotificationItem(String title, String msg, String time, boolean isSuccess) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("NotificationItem.fxml"));
            AnchorPane item = fxmlLoader.load();
            
            NotificationItemController controller = fxmlLoader.getController();
            controller.setData(title, msg, time, isSuccess);
            
            notificationList.getChildren().add(item);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleClearAll(ActionEvent event) {
        new Thread(() -> {
            try {
                TestClient client = App.getClient();
                if (client == null || client.getUser() == null) return;
                
                int myId = client.getUser().getId();
                
                client.currentresponse = null;

                Request req = new Request("CLEAR_ALL_NOTIFICATIONS", myId);
                client.send(req);
                
                int timeout = 20;
                while(client.getCurrentresponse() == null && timeout > 0) {
                    Thread.sleep(100);
                    timeout--;
                }
                
                Response res = client.getCurrentresponse();
                
                javafx.application.Platform.runLater(() -> {
                    if (res != null && res.isSuccess()) {
                        notificationList.getChildren().clear();
                        System.out.println("Notifications deleted from DB.");
                    } else {
                        System.out.println("Failed to delete notifications.");
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}