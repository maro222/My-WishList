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
        String sql = "INSERT INTO friends (sender_id, receiver_id, status) VALUES (?, ?, 0)";
        
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

    /**
     * Accept a friend request
     * @param senderId ID of the user who sent the request
     * @param receiverId ID of the user accepting the request
     * @return true if accepted successfully
     */
    public boolean acceptFriendRequest(int senderId, int receiverId) {
        String sql = "UPDATE friends SET status = 1 WHERE sender_id = ? AND receiver_id = ?";
        
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

    /**
     * Decline a friend request
     * @param senderId ID of the user who sent the request
     * @param receiverId ID of the user declining the request
     * @return true if declined successfully
     */
    public boolean declineFriendRequest(int senderId, int receiverId) {
        String sql = "DELETE FROM friends WHERE sender_id = ? AND receiver_id = ?";
        
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

    /**
     * Remove a friend (works for both sender and receiver)
     * @param userId1 First user ID
     * @param userId2 Second user ID
     * @return true if friendship was removed
     */
    public boolean removeFriend(int userId1, int userId2) {
        String sql = "DELETE FROM friends WHERE " +
                    "(sender_id = ? AND receiver_id = ?) OR " +
                    "(sender_id = ? AND receiver_id = ?)";
        
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, userId1);
            ps.setInt(2, userId2);
            ps.setInt(3, userId2);
            ps.setInt(4, userId1);
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
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
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                friends.add(user);
            }
            
        } catch (SQLException e) {
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
        List<User> pendingUsers = new ArrayList<>();
        String sql = "SELECT u.id, u.username, u.email FROM users u " +
                    "INNER JOIN friends f ON f.sender_id = u.id " +
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
            e.printStackTrace();
        }
        
        return false;
    }
}