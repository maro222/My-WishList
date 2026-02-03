/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.dao;



import com.mycompany.db.DatabaseHandler;
import com.mycompany.model.Contribution;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ContributionDAO {


    public List<Contribution> getContributionsByWishId(int wishId) {
        List<Contribution> contributions = new ArrayList<>();
        String sql = "SELECT c.id, c.payer_id, c.wish_id, c.amount, u.username " +
                    "FROM contributions c " +
                    "JOIN users u ON c.payer_id = u.id " +
                    "WHERE c.wish_id = ?";

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, wishId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Contribution contribution = new Contribution();
                contribution.setId(rs.getInt("id"));
                contribution.setPayerId(rs.getInt("payer_id"));
                contribution.setWishId(rs.getInt("wish_id"));
                contribution.setAmount(rs.getDouble("amount"));
                contribution.setPayerUsername(rs.getString("username"));
                contributions.add(contribution);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contributions;
    }


    public List<Contribution> getContributionsByPayerId(int payerId) {
        List<Contribution> contributions = new ArrayList<>();
        String sql = "SELECT c.id, c.payer_id, c.wish_id, c.amount, " +
                    "i.name AS item_name, u.username AS receiver_username " +
                    "FROM contributions c " +
                    "JOIN wishlist w ON c.wish_id = w.id " +
                    "JOIN items i ON w.item_id = i.id " +
                    "JOIN users u ON w.user_id = u.id " +
                    "WHERE c.payer_id = ?";

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payerId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Contribution contribution = new Contribution();
                contribution.setId(rs.getInt("id"));
                contribution.setPayerId(rs.getInt("payer_id"));
                contribution.setWishId(rs.getInt("wish_id"));
                contribution.setAmount(rs.getDouble("amount"));
                contribution.setItemName(rs.getString("item_name"));
                contribution.setReceiverUsername(rs.getString("receiver_username"));
                contributions.add(contribution);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return contributions;
    }


    public Contribution getContributionById(int contributionId) {
        String sql = "SELECT c.id, c.payer_id, c.wish_id, c.amount, u.username " +
                    "FROM contributions c " +
                    "JOIN users u ON c.payer_id = u.id " +
                    "WHERE c.id = ?";

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, contributionId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Contribution contribution = new Contribution();
                contribution.setId(rs.getInt("id"));
                contribution.setPayerId(rs.getInt("payer_id"));
                contribution.setWishId(rs.getInt("wish_id"));
                contribution.setAmount(rs.getDouble("amount"));
                contribution.setPayerUsername(rs.getString("username"));
                return contribution;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public Contribution getContributionByPayerAndWish(int payerId, int wishId) {
        String sql = "SELECT * FROM contributions WHERE payer_id = ? AND wish_id = ?";

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payerId);
            ps.setInt(2, wishId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Contribution contribution = new Contribution();
                contribution.setId(rs.getInt("id"));
                contribution.setPayerId(rs.getInt("payer_id"));
                contribution.setWishId(rs.getInt("wish_id"));
                contribution.setAmount(rs.getDouble("amount"));
                return contribution;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }


    public double getTotalContributedByUser(int payerId) {
        String sql = "SELECT SUM(amount) FROM contributions WHERE payer_id = ?";

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payerId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }


    public double getTotalReceivedByUser(int receiverId) {
        String sql = "SELECT SUM(c.amount) FROM contributions c " +
                    "JOIN wishlist w ON c.wish_id = w.id " +
                    "WHERE w.user_id = ?";

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, receiverId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getDouble(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }


    public boolean hasUserContributed(int payerId, int wishId) {
        String sql = "SELECT COUNT(*) FROM contributions WHERE payer_id = ? AND wish_id = ?";

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, payerId);
            ps.setInt(2, wishId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }


    public int getContributorCount(int wishId) {
        String sql = "SELECT COUNT(*) FROM contributions WHERE wish_id = ?";

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, wishId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getInt(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }


    public boolean deleteContribution(int contributionId) {
        String sql = "DELETE FROM contributions WHERE id = ?";

        try (Connection conn = DatabaseHandler.connect();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, contributionId);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}