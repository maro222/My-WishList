package com.mycompany.java2wishlistapp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import com.mycompany.model.Request;
import com.mycompany.model.Response;
import com.mycompany.model.User;
import com.mycompany.model.Item;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.List;


public class TestClient extends Thread{
    

    Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private volatile boolean running = true;
    Response currentresponse;
    User user;
    
    
    public TestClient(){
        try{
            socket = new Socket("127.0.0.1", 5005);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
        }catch (IOException e){
            closeEveryThing();
        }
    }
    
    @Override
    public void run(){
        
            listenToServer();
    }
    
    
    private void listenToServer() {
        try {
            while (running) {
                Response response = (Response) in.readObject();
                handleResponse(response);
            }
        } catch (Exception e) {
            System.out.println("Server disconnected");
            running = false;
        } finally {
            closeEveryThing();
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
    
    private void handleResponse(Response response) {
        System.out.println("Server: " + response.getMessage());
        this.currentresponse = response;
        // Later:
        // if(response.getType().equals("NOTIFICATION")) { ... }
    }

    public Response getCurrentresponse() {
        return currentresponse;
    }

    public void send(Request request) throws Exception {     //specific button that should be implemented for others
        out.writeObject(request);
        out.flush();
    }
    
    
    public void closeEveryThing(){
        try{
            running = false;
            if (socket != null)
                socket.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}