package com.mycompany.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Notification implements Serializable {
    private int id;
    private int userId;
    private String message;
    private Timestamp createdAt;

    public Notification(int id, int userId, String message, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.message = message;
        this.createdAt = createdAt;
    }

    
    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getMessage() { return message; }
    public String getTime() { return createdAt.toString(); }
}