package com.mycompany.dao;

import com.mycompany.model.WishlistItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {
    
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/iwish_db", "root", "0000"
        );
    }
    
    // Add item to wishlist
    public boolean addToWishlist(int userId, int itemId) {
        String sql = "INSERT INTO wishlist (user_id, item_id, paid_amount) VALUES (?, ?, 0)";  // Changed to paid_amount
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get wishlist for a specific user
    public List<WishlistItem> getUserWishlist(int userId) {
        System.out.println("=== getUserWishlist called with userId: " + userId + " ===");
        List<WishlistItem> list = new ArrayList<>();
        String sql = "SELECT w.id as wishlist_id, i.id as item_id, i.name, i.price, w.paid_amount " +  // Changed to paid_amount
                     "FROM wishlist w JOIN items i ON w.item_id = i.id WHERE w.user_id = ?";
        
        System.out.println("SQL: " + sql);
        
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            System.out.println("Connected to database successfully");
            ps.setInt(1, userId);
            System.out.println("Executing query with userId: " + userId);
            
            ResultSet rs = ps.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                int wishlistId = rs.getInt("wishlist_id");
                String name = rs.getString("name");
                double price = rs.getDouble("price");
                double collected = rs.getDouble("paid_amount");  // Changed to paid_amount
                
                System.out.println("Found row " + count + ": ID=" + wishlistId + ", Name=" + name + ", Price=" + price + ", Collected=" + collected);
                
                WishlistItem item = new WishlistItem(
                        wishlistId,
                        userId,
                        name,
                        price,
                        collected
                );
                list.add(item);
                System.out.println("Added item to list: " + item.getName());
            }
            
            System.out.println("Total items found: " + count);
            System.out.println("List size: " + list.size());
            
        } catch (SQLException e) {
            System.out.println("SQL ERROR in getUserWishlist:");
            e.printStackTrace();
        }
        
        System.out.println("Returning list with " + list.size() + " items");
        return list;
    }
    
    // Get friend's wishlist
    public List<WishlistItem> getFriendWishlist(int friendId) {
        System.out.println("=== getFriendWishlist called with friendId: " + friendId + " ===");
        return getUserWishlist(friendId);
    }
    
    // Contribute to a wishlist item
    public boolean contribute(int wishlistId, int userId, double amount) {
        String sql = "UPDATE wishlist SET paid_amount = paid_amount + ? WHERE id = ?";  // Changed to paid_amount
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDouble(1, amount);
            ps.setInt(2, wishlistId);
            int updated = ps.executeUpdate();
            
            // TODO: Optionally deduct from user's balance in users table
            
            return updated > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}