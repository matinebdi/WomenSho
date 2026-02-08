package com.womenshop.dao;

import java.sql.*;

public class FinanceDAO {
    
    public void updateFinances(double income, double cost) throws SQLException {
        String sql = "UPDATE store_finances SET total_income = ?, total_cost = ? WHERE id = 1";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setDouble(1, income);
            pstmt.setDouble(2, cost);
            pstmt.executeUpdate();
        }
    }

    public void loadFinances() throws SQLException {
        String sql = "SELECT * FROM store_finances WHERE id = 1";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                com.womenshop.model.Product.income = rs.getDouble("total_income");
                com.womenshop.model.Product.cost = rs.getDouble("total_cost");
            }
        }
    }
}