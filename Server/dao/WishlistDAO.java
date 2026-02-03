package com.mycompany.dao;

import com.mycompany.db.DatabaseHandler;
import com.mycompany.model.Contribution;
import com.mycompany.model.WishlistItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {

    // Add item to wishlist
    public boolean addToWishlist(int userId, int itemId) {
        String sql = "INSERT INTO wishlist (user_id, item_id, paid_amount) VALUES (?, ?, 0)";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean isItemInWishlist(int userId, int itemId) {
        String sql = "SELECT COUNT(*) FROM wishlist WHERE user_id = ? AND item_id = ?";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, itemId);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Return false if an error occurred or no entry was found
    }
    
    public List<WishlistItem> getUserWishlist(int userId) {
        System.out.println("=== getUserWishlist called with userId: " + userId + " ===");
        List<WishlistItem> list = new ArrayList<>();
        String sql = "SELECT w.id as wishlist_id, i.id as item_id, i.name, i.price, w.paid_amount " +  // Changed to paid_amount
                     "FROM wishlist w JOIN items i ON w.item_id = i.id WHERE w.user_id = ?";
        
        System.out.println("SQL: " + sql);
        
        try (Connection conn = DatabaseHandler.connect();
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
    
    
    public boolean deleteWishItem(int wishlistId) {
        String sql = "DELETE FROM wishlist WHERE id = ?";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, wishlistId);
            

            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    
    
    

    // Get friend's wishlist
    public List<WishlistItem> getFriendWishlist(int friendId) {
        return getUserWishlist(friendId); // Same logic, just another user
    }

    public boolean contribute(Contribution c) {
        Connection conn = null;
        PreparedStatement psHistory = null;
        PreparedStatement psUpdate = null;
        
        String insertHistorySql = "INSERT INTO contributions (payer_id, wish_id, amount) VALUES (?, ?, ?)";
        String updateWishlistSql = "UPDATE wishlist SET paid_amount = paid_amount + ? WHERE id = ?";

        try {
            conn = DatabaseHandler.connect();
            // Disable auto-commit to handle transaction
            conn.setAutoCommit(false); 

            psHistory = conn.prepareStatement(insertHistorySql);
            psHistory.setInt(1, c.getPayerId());
            psHistory.setInt(2, c.getWishId());
            psHistory.setDouble(3, c.getAmount());
            psHistory.executeUpdate();

            psUpdate = conn.prepareStatement(updateWishlistSql);
            psUpdate.setDouble(1, c.getAmount());
            psUpdate.setInt(2, c.getWishId());
            int rowsAffected = psUpdate.executeUpdate();

            if (rowsAffected > 0) {
                conn.commit(); // Save both changes
                return true;
            } else {
                conn.rollback(); // Undo if wishlist update failed
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            try { if(psHistory!=null) psHistory.close(); } catch(Exception e){}
            try { if(psUpdate!=null) psUpdate.close(); } catch(Exception e){}
            try { if(conn!=null) conn.close(); } catch(Exception e){}
        }
    }
    
    public WishlistItem getWishItemById(int wishlistId) {
        String sql = "SELECT w.id, w.user_id, w.paid_amount, i.name, i.price " +
                     "FROM wishlist w JOIN items i ON w.item_id = i.id " +
                     "WHERE w.id = ?";

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, wishlistId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new WishlistItem(
                    rs.getInt("id"),           
                    rs.getInt("user_id"),      
                    rs.getString("name"),     
                    rs.getDouble("price"),     
                    rs.getDouble("paid_amount")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }
    
    public List<Integer> getDistinctContributors(int wishId) {
        List<Integer> ids = new ArrayList<>();
        // "DISTINCT" ensures we don't send 5 notifications to the same person if they paid 5 times
        String sql = "SELECT DISTINCT payer_id FROM contributions WHERE wish_id = ?";
        
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, wishId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                ids.add(rs.getInt("payer_id"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ids;
    }
}
