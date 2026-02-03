package com.mycompany.dao;

import com.mycompany.db.DatabaseHandler;
import com.mycompany.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import java.sql.ResultSet;

public class FriendDAO {


    public boolean sendFriendRequest(int senderId, int receiverId) {
        System.out.println("=== sendFriendRequest: " + senderId + " -> " + receiverId + " ===");
        
        // Check if already friends or request exists (in either direction)
        if (areFriends(senderId, receiverId)) {
            System.out.println("Users are already friends");
            return false;
        }
        
        if (requestExists(senderId, receiverId) || requestExists(receiverId, senderId)) {
            System.out.println("Friend request already exists");
            return false;
        }
        
        String sql = "INSERT INTO friendships (sender_id, receiver_id, status) VALUES (?, ?, 0)";
        
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            
            boolean success = ps.executeUpdate() > 0;
            System.out.println("Friend request sent: " + success);
            return success;
            
        } catch (SQLException e) {
            System.out.println("SQL ERROR in sendFriendRequest:");
            e.printStackTrace();
            return false;
        }
    }
    
    
    


    public boolean removeFriend(int userId1, int userId2) {
        System.out.println("=== removeFriend: " + userId1 + " <-> " + userId2 + " ===");
        
        String sql = "DELETE FROM friendships WHERE " +
                    "(sender_id = ? AND receiver_id = ?) OR " +
                    "(sender_id = ? AND receiver_id = ?)";
        
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId1);
            ps.setInt(2, userId2);
            ps.setInt(3, userId2);
            ps.setInt(4, userId1);
            
            boolean success = ps.executeUpdate() > 0;
            System.out.println("Friend removed: " + success);
            return success;
            
        } catch (SQLException e) {
            System.out.println("SQL ERROR in removeFriend:");
            e.printStackTrace();
            return false;
        }
    }
    
    

    public boolean acceptFriendRequest(int senderId, int receiverId) {
        String sql = "UPDATE friendships SET status = 1 WHERE sender_id = ? AND receiver_id = ?";
        
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    public boolean declineFriendRequest(int senderId, int receiverId) {
        String sql = "DELETE FROM friendships WHERE sender_id = ? AND receiver_id = ?";
        
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    


    public List<User> getFriends(int userId) {
        System.out.println("=== getFriends for userId: " + userId + " ===");
        
        List<User> friends = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.email FROM users u " +
                    "INNER JOIN friendships f ON " +
                    "((f.sender_id = ? AND f.receiver_id = u.id) OR " +
                    "(f.receiver_id = ? AND f.sender_id = u.id)) " +
                    "WHERE f.status = 1";
        
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            
            ResultSet rs = ps.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                friends.add(user);
                System.out.println("Friend " + count + ": " + user.getUsername());
            }
            
            System.out.println("Total friends: " + count);
            
        } catch (SQLException e) {
            System.out.println("SQL ERROR in getFriends:");
            e.printStackTrace();
        }
        
        return friends;
    }
    
    
    

    public List<User> getPendingRequests(int userId) {
        List<User> pendingUsers = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.email FROM users u " +
                    "INNER JOIN friendships f ON f.sender_id = u.id " +
                    "WHERE f.receiver_id = ? AND f.status = 0";
        
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                pendingUsers.add(user);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return pendingUsers;
    }


    public boolean areFriends(int userId1, int userId2) {
        String sql = "SELECT COUNT(*) FROM friendships WHERE " +
                    "((sender_id = ? AND receiver_id = ?) OR " +
                    "(sender_id = ? AND receiver_id = ?)) AND status = 1";
        
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId1);
            ps.setInt(2, userId2);
            ps.setInt(3, userId2);
            ps.setInt(4, userId1);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.out.println("SQL ERROR in areFriends:");
            e.printStackTrace();
        }
        
        return false;
    }


    public boolean requestExists(int senderId, int receiverId) {
        String sql = "SELECT COUNT(*) FROM friendships WHERE sender_id = ? AND receiver_id = ?";
        
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            System.out.println("SQL ERROR in requestExists:");
            e.printStackTrace();
        }
        
        return false;
    }
}