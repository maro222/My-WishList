package com.mycompany.java2wishlistapp;

import com.mycompany.model.Request;
import com.mycompany.model.Response;
import com.mycompany.model.User;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

public class FriendsListController implements Initializable {
    
    @FXML
    private GridPane friendsGrid;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        loadFriends();
    }
    
    private void loadFriends() {
        try {
            TestClient client = App.getClient();
            if (client == null || client.getUser() == null) {
                System.out.println("Client or user not ready");
                return;
            }
            
            int userId = client.getUser().getId();
            Request request = new Request("GET_FRIENDS", userId);
            client.send(request);
            
            Thread.sleep(1000);
            
            Response response = client.getCurrentresponse();
            if (response == null || !response.isSuccess()) {
                System.out.println("Failed to load friends");
                return;
            }
            
            List<User> friends = (List<User>) response.getData();
            
            friendsGrid.getChildren().clear();
            int column = 0;
            int row = 1;
            
            for (User friend : friends) {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("FriendCard.fxml")
                );
                AnchorPane pane = loader.load();
                FriendCardController controller = loader.getController();
                
                // Pass friendId along with name and email
                controller.setData(
                        friend.getUsername(),
                        friend.getEmail(),
                        friend.getId()
                );
                
                friendsGrid.add(pane, column, row);
                column++;
                if (column == 2) {
                    column = 0;
                    row++;
                }
            }
            
            client.currentresponse = null;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}