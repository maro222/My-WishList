package com.mycompany.java2wishlistapp;

import com.mycompany.model.Request;
import com.mycompany.model.User;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {

    private static Scene scene;
    private static TestClient client;

    @Override
    public void start(Stage stage) throws IOException {
        scene = new Scene(loadFXML("Login"), 900, 600);
        stage.setScene(scene);
//        stage.setMaximized(true);        
        stage.setResizable(false);
        stage.setTitle("i-Wish Application");
        stage.show();
        
        startClientBackend();
    }
    
    public static TestClient getClient(){
        return client;
    }
    
    private void startClientBackend() {
        // Run in a new thread so we don't block the UI
        new Thread(() -> {
            try {
                client = new TestClient();
                client.start();

                System.out.println("Client started, sending login request...");
                
            } catch (Exception e) {
                System.out.println("Client cannot login");
            }
        }).start();
    }

    @Override
    public void stop() throws Exception {
        // Best Practice: Close connection when app closes
        if (client != null) {
            client.closeEveryThing();
        }
        super.stop();
    }

    static void setRoot(String fxml) throws IOException {
        scene.setRoot(loadFXML(fxml));
    }

    private static Parent loadFXML(String fxml) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource(fxml + ".fxml"));
        return fxmlLoader.load();
    }

    public static void main(String[] args) {
        launch();
    }
}