
package com.mycompany.model;

import java.io.Serializable;

public class WishlistItem implements Serializable {
    private static final long serialVersionUID = 1L;

    private int id;
    private int userId;        // Owner of the wishlist item
    private String name;
    private double totalPrice;
    private double collectedAmount;

    public WishlistItem() {}

    public WishlistItem(int id, int userId, String name, double totalPrice, double collectedAmount) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.totalPrice = totalPrice;
        this.collectedAmount = collectedAmount;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public double getCollectedAmount() { return collectedAmount; }
    public void setCollectedAmount(double collectedAmount) { this.collectedAmount = collectedAmount; }
}

