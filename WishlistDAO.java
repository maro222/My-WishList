package dao;

import db.DatabaseHandler;
import model.Item;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {
    private NotificationDAO notificationDAO = new NotificationDAO();

    // Req 4: Add to Wishlist
    public boolean addToWishlist(int userId, int itemId) {
        String sql = "INSERT INTO wishlist (user_id, item_id, paid_amount) VALUES (?, ?, 0)";
        try (Connection conn = DatabaseHandler.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, itemId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Req 4: Remove from Wishlist
    public boolean removeWish(int userId, int wishId) {
        String sql = "DELETE FROM wishlist WHERE id = ? AND user_id = ?";
        try (Connection conn = DatabaseHandler.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, wishId);
            ps.setInt(2, userId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            return false;
        }
    }

    // Req 6: View Wishlist
    public List<Item> getWishlist(int userId) {
        List<Item> items = new ArrayList<>();
        String sql = "SELECT w.id as wish_id, i.id as item_id, i.name, i.price, w.paid_amount " +
                "FROM wishlist w JOIN items i ON w.item_id = i.id WHERE w.user_id = ?";
        try (Connection conn = DatabaseHandler.connect();
                PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                items.add(new Item(
                        rs.getInt("wish_id"), rs.getInt("item_id"),
                        rs.getString("name"), rs.getDouble("price"),
                        rs.getDouble("paid_amount")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return items;
    }

    // Req 7, 8, 9: Contribute & Notify
   public boolean contribute(int payerId, int wishId, double amount) {
    Connection conn = null;
    try {
        conn = DatabaseHandler.connect();
        conn.setAutoCommit(false);
        
        System.out.println("=== contribute called: payerId=" + payerId + ", wishId=" + wishId + ", amount=" + amount + " ===");
        
        // 1. Get Wish Details (Owner & Price)
        String checkSql = "SELECT w.user_id, w.paid_amount, i.price, i.name " +
                         "FROM wishlist w JOIN items i ON w.item_id = i.id WHERE w.id = ?";
        PreparedStatement psCheck = conn.prepareStatement(checkSql);
        psCheck.setInt(1, wishId);
        ResultSet rs = psCheck.executeQuery();
        
        if (!rs.next()) {
            System.out.println("Wishlist item not found");
            conn.rollback();
            return false;
        }
        
        int ownerId = rs.getInt("user_id");
        double currentPaid = rs.getDouble("paid_amount");
        double price = rs.getDouble("price");
        String itemName = rs.getString("name");
        
        System.out.println("Item: " + itemName + ", Price: " + price + ", Already paid: " + currentPaid);
        
        // Calculate remaining amount needed
        double remainingAmount = price - currentPaid;
        
        System.out.println("Remaining amount needed: " + remainingAmount);
        
        // Check if already fully paid
        if (remainingAmount <= 0) {
            System.out.println("Item is already fully paid");
            conn.rollback();
            return false;
        }
        
        // Cap the contribution to the remaining amount
        double actualContribution = Math.min(amount, remainingAmount);
        
        System.out.println("Requested amount: " + amount + ", Actual contribution: " + actualContribution);
        
        // If user tried to contribute more than needed, inform them
        if (actualContribution < amount) {
            System.out.println("WARNING: Contribution capped to remaining amount (" + actualContribution + ")");
        }
        
        // 2. Update Wishlist
        String updateSql = "UPDATE wishlist SET paid_amount = paid_amount + ? WHERE id = ?";
        PreparedStatement psUpdate = conn.prepareStatement(updateSql);
        psUpdate.setDouble(1, actualContribution);
        psUpdate.setInt(2, wishId);
        int updated = psUpdate.executeUpdate();
        
        if (updated == 0) {
            System.out.println("Failed to update wishlist");
            conn.rollback();
            return false;
        }
        
        // 3. Record Contribution
        String insertSql = "INSERT INTO contributions (payer_id, wish_id, amount) VALUES (?, ?, ?)";
        PreparedStatement psInsert = conn.prepareStatement(insertSql);
        psInsert.setInt(1, payerId);
        psInsert.setInt(2, wishId);
        psInsert.setDouble(3, actualContribution);
        psInsert.executeUpdate();
        
        System.out.println("Contribution recorded: " + actualContribution + " EGP");
        
        // 4. Notifications
        // Notify Owner about contribution
        String msgToOwner = "A friend contributed " + actualContribution + " EGP to your wish: " + itemName;
        notificationDAO.addNotification(ownerId, msgToOwner);
        
        // Check if completed (now fully paid)
        double newTotal = currentPaid + actualContribution;
        if (newTotal >= price) {
            System.out.println("Item is now fully paid!");
            
            String completedMsg = "Congratulations! Your wish '" + itemName + "' is fully paid!";
            notificationDAO.addNotification(ownerId, completedMsg);
            
            // Notify Payer (Buyer)
            String msgToBuyer = "The item '" + itemName + "' you contributed to is now fully paid.";
            notificationDAO.addNotification(payerId, msgToBuyer);
        }
        
        conn.commit();
        System.out.println("Contribution successful");
        return true;
        
    } catch (SQLException e) {
        System.out.println("SQL ERROR in contribute:");
        e.printStackTrace();
        try {
            if (conn != null) {
                conn.rollback();
                System.out.println("Transaction rolled back");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return false;
    } finally {
        try {
            if (conn != null) {
                conn.setAutoCommit(true);
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
           
    }
