package com.mycompany.dao;

import com.mycompany.db.DatabaseHandler;
import com.mycompany.model.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO {


    public boolean register(User user) {
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, username);
            ps.setString(2, password);
            
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    

    public List<User> searchUsers(String query) {
        System.out.println("=== searchUsers with query: " + query + " ===");
        
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, username, email FROM users WHERE username LIKE ? LIMIT 10";
        
        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String usernamePattern = query + "%";  // Username starts with query
            
            
            ps.setString(1, usernamePattern);
          
            
            System.out.println("Username pattern: " + usernamePattern);
         
            ResultSet rs = ps.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                users.add(user);
                
                System.out.println("Found user " + count + ": " + user.getUsername() + " (" + user.getEmail() + ")");
            }
            
            System.out.println("Total users found: " + count);
            
        } catch (SQLException e) {
            System.out.println("SQL ERROR in searchUsers:");
            e.printStackTrace();
        }
        
        return users;
    }
    
    
    public User NewPassword(){
        return null;
    }
    
    public User getUserById(int id){
        
        String sql = "SELECT * from users where id = ?";
        try(Connection conn = DatabaseHandler.connect();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()){
                    return new User(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("email"),
                        rs.getString("password")
                );
            }
            
            
        } catch (SQLException ex) {
            Logger.getLogger(UserDAO.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("SQL ERROR in returning User By Id :");
        }
        
        System.out.println("User not found");
        return null;
    }
}
