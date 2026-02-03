package com.mycompany.mywishlist;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import com.mycompany.network.ServerConnection;

public class ServerApp extends Application {

    private ServerConnection serverConnection;

    // UI Components that need to change state
    private Circle statusIndicator;
    private Label lblStatusText;
    private Button btnStart;
    private Button btnStop;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        serverConnection = new ServerConnection();

        // 1. Title Label
        Label titleLabel = new Label("iWish Server Manager");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 24));

        // 2. Status Indicator (The "LED" Light)
        statusIndicator = new Circle(8); 
        statusIndicator.setFill(Color.RED); // Default Red
        statusIndicator.setEffect(new DropShadow(10, Color.RED)); // Glow effect

        lblStatusText = new Label("OFFLINE");
        lblStatusText.setTextFill(Color.web("#ff6b6b")); // Soft Red
        lblStatusText.setFont(Font.font("Consolas", FontWeight.BOLD, 18));

        // Group the light and text
        HBox statusBox = new HBox(15, statusIndicator, lblStatusText);
        statusBox.setAlignment(Pos.CENTER);
        statusBox.setPadding(new Insets(20, 0, 20, 0));
        // Add a subtle background for the status area
        statusBox.setStyle("-fx-background-color: #2b2b2b; -fx-background-radius: 10; -fx-border-color: #444; -fx-border-radius: 10;");
        statusBox.setMaxWidth(250);

        // 3. Buttons
        btnStart = createStyledButton("Start Server", "#2ecc71", "#27ae60");
        btnStop = createStyledButton("Stop Server", "#e74c3c", "#c0392b");
        btnStop.setDisable(true); // Initially disabled

        // Button Actions
        btnStart.setOnAction(e -> handleStart());
        btnStop.setOnAction(e -> handleStop());

        HBox buttonBox = new HBox(20, btnStart, btnStop);
        buttonBox.setAlignment(Pos.CENTER);

        // 4. Main Layout
        VBox root = new VBox(25); // 25px spacing between elements
        root.getChildren().addAll(titleLabel, statusBox, buttonBox);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30));
        
        // Set Dark Background Color
        root.setBackground(new Background(new BackgroundFill(Color.web("#1e1e1e"), CornerRadii.EMPTY, Insets.EMPTY)));

        Scene scene = new Scene(root, 400, 350);
        primaryStage.setTitle("iWish Server Control");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void handleStart() {
        serverConnection.startServer();
        
        // Update UI
        statusIndicator.setFill(Color.LIMEGREEN);
        statusIndicator.setEffect(new DropShadow(15, Color.LIMEGREEN)); // Green Glow
        
        lblStatusText.setText("RUNNING");
        lblStatusText.setTextFill(Color.LIMEGREEN);
        
        btnStart.setDisable(true);
        btnStop.setDisable(false);
        
        // Change button styles to reflect state
        btnStart.setStyle("-fx-background-color: #555; -fx-text-fill: #aaa; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnStop.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
    }

    private void handleStop() {
        serverConnection.stopServer();
        
        // Update UI
        statusIndicator.setFill(Color.RED);
        statusIndicator.setEffect(new DropShadow(10, Color.RED)); // Red Glow
        
        lblStatusText.setText("OFFLINE");
        lblStatusText.setTextFill(Color.web("#ff6b6b"));
        
        btnStart.setDisable(false);
        btnStop.setDisable(true);
        
        // Reset button styles
        btnStart.setStyle("-fx-background-color: #2ecc71; -fx-text-fill: white; -fx-font-weight: bold; -fx-background-radius: 5;");
        btnStop.setStyle("-fx-background-color: #555; -fx-text-fill: #aaa; -fx-font-weight: bold; -fx-background-radius: 5;");
    }

    // Helper method to create beautiful buttons
    private Button createStyledButton(String text, String colorNormal, String colorHover) {
        Button btn = new Button(text);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btn.setPrefWidth(120);
        btn.setPrefHeight(40);
        
        String normalStyle = "-fx-background-color: " + colorNormal + "; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;";
        String hoverStyle = "-fx-background-color: " + colorHover + "; -fx-text-fill: white; -fx-background-radius: 5; -fx-cursor: hand;";
        
        btn.setStyle(normalStyle);
        
        // Add Hover Effects
        btn.setOnMouseEntered(e -> {
            if (!btn.isDisabled()) btn.setStyle(hoverStyle);
        });
        btn.setOnMouseExited(e -> {
            if (!btn.isDisabled()) btn.setStyle(normalStyle);
        });
        
        return btn;
    }

    @Override
    public void stop() {
        if(serverConnection != null) {
            serverConnection.stopServer();
        }
    }
}