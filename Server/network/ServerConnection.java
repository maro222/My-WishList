package com.mycompany.network;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ServerConnection {

    private ServerSocket serverSocket;
    private boolean isRunning = false;
    
    // 1. Create a list to keep track of active clients
    private List<ClientHandler> activeClients = new ArrayList<>();

    public void startServer() {
        try {
            serverSocket = new ServerSocket(5005);
            isRunning = true;
            System.out.println("Server started on port 5005");

            new Thread(() -> {
                while (isRunning) {
                    try {
                        Socket socket = serverSocket.accept();
                        System.out.println("Client connected: " + socket.getInetAddress());
                        
                        ClientHandler handler = new ClientHandler(socket);
                        activeClients.add(handler);
                        handler.start();
                        
                    } catch (IOException e) {
                        if (isRunning) {
                            System.out.println("Error accepting client");
                        }
                    }
                }
            }).start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopServer() {
        isRunning = false;
        try {
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }

            for (ClientHandler client : activeClients) {
                client.closeEverything(); // You need to implement this method in ClientHandler
            }
            activeClients.clear(); // Clear the list
            
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Server stopped");
    }
}