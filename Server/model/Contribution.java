/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.model;

import java.io.Serializable;


/**
 * Model class representing a contribution to a wishlist item
 */
public class Contribution implements Serializable {
    private int id;
    private int payerId;
    private int wishId;
    private double amount;
    
    private static final long serialVersionUID = 1L;
    // Additional fields for joined queries
    private String payerUsername;
    private String receiverUsername;
    private String itemName;

    // Constructors
    public Contribution() {
    }

    public Contribution(int id, int payerId, int wishId, double amount) {
        this.id = id;
        this.payerId = payerId;
        this.wishId = wishId;
        this.amount = amount;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPayerId() {
        return payerId;
    }

    public void setPayerId(int payerId) {
        this.payerId = payerId;
    }

    public int getWishId() {
        return wishId;
    }

    public void setWishId(int wishId) {
        this.wishId = wishId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getPayerUsername() {
        return payerUsername;
    }

    public void setPayerUsername(String payerUsername) {
        this.payerUsername = payerUsername;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    @Override
    public String toString() {
        return "Contribution{" +
                "id=" + id +
                ", payerId=" + payerId +
                ", wishId=" + wishId +
                ", amount=" + amount +
                ", payerUsername='" + payerUsername + '\'' +
                ", receiverUsername='" + receiverUsername + '\'' +
                ", itemName='" + itemName + '\'' +
                '}';
    }
}