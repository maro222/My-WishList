package com.mycompany.dao;

import com.mycompany.db.DatabaseHandler;
import com.mycompany.model.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FriendDAO {

    /**
     * Send a friend request
     * @param senderId ID of the user sending the request
     * @param receiverId ID of the user receiving the request
     * @return true if request was sent successfully
     */
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
        
        String sql = "INSERT INTO friends (sender_id, receiver_id, status) VALUES (?, ?, 0)";
        
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

    /**
     * Accept a friend request
     * @param senderId ID of the user who sent the request
     * @param receiverId ID of the user accepting the request
     * @return true if accepted successfully
     */
    public boolean acceptFriendRequest(int senderId, int receiverId) {
        System.out.println("=== acceptFriendRequest: sender=" + senderId + ", receiver=" + receiverId + " ===");
        
        String sql = "UPDATE friends SET status = 1 WHERE sender_id = ? AND receiver_id = ?";
        
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            
            boolean success = ps.executeUpdate() > 0;
            System.out.println("Friend request accepted: " + success);
            return success;
            
        } catch (SQLException e) {
            System.out.println("SQL ERROR in acceptFriendRequest:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Decline a friend request
     * @param senderId ID of the user who sent the request
     * @param receiverId ID of the user declining the request
     * @return true if declined successfully
     */
    public boolean declineFriendRequest(int senderId, int receiverId) {
        System.out.println("=== declineFriendRequest: sender=" + senderId + ", receiver=" + receiverId + " ===");
        
        String sql = "DELETE FROM friends WHERE sender_id = ? AND receiver_id = ?";
        
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, senderId);
            ps.setInt(2, receiverId);
            
            boolean success = ps.executeUpdate() > 0;
            System.out.println("Friend request declined: " + success);
            return success;
            
        } catch (SQLException e) {
            System.out.println("SQL ERROR in declineFriendRequest:");
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Remove a friend (works for both sender and receiver)
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return true if friendship was removed
     */
    public boolean removeFriend(int userId1, int userId2) {
        System.out.println("=== removeFriend: " + userId1 + " <-> " + userId2 + " ===");
        
        String sql = "DELETE FROM friends WHERE " +
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

    /**
     * Get all accepted friends for a user
     * @param userId User ID
     * @return List of friends
     */
    public List<User> getFriends(int userId) {
        System.out.println("=== getFriends for userId: " + userId + " ===");
        
        List<User> friends = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.email FROM users u " +
                    "INNER JOIN friends f ON " +
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

    /**
     * Get all pending friend requests (requests received by the user)
     * @param userId User ID
     * @return List of users who sent friend requests
     */
    public List<User> getPendingRequests(int userId) {
        System.out.println("=== getPendingRequests for userId: " + userId + " ===");
        
        List<User> pendingUsers = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.email FROM users u " +
                    "INNER JOIN friends f ON f.sender_id = u.id " +
                    "WHERE f.receiver_id = ? AND f.status = 0";
        
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                pendingUsers.add(user);
                System.out.println("Pending request " + count + ": from " + user.getUsername());
            }
            
            System.out.println("Total pending requests: " + count);
            
        } catch (SQLException e) {
            System.out.println("SQL ERROR in getPendingRequests:");
            e.printStackTrace();
        }
        
        return pendingUsers;
    }

    /**
     * Check if two users are friends
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return true if they are friends
     */
    public boolean areFriends(int userId1, int userId2) {
        String sql = "SELECT COUNT(*) FROM friends WHERE " +
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

    /**
     * Check if a friend request already exists
     * @param senderId Sender ID
     * @param receiverId Receiver ID
     * @return true if request exists
     */
    public boolean requestExists(int senderId, int receiverId) {
        String sql = "SELECT COUNT(*) FROM friends WHERE sender_id = ? AND receiver_id = ?";
        
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