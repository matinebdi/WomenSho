package com.womenshop.dao;

import com.womenshop.model.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    public void addProduct(Product p) throws SQLException {
        String query = "INSERT INTO products (name, purchase_price, sell_price, nb_items, category) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, p.getName());
                pstmt.setDouble(2, p.getPurchasePrice());
                pstmt.setDouble(3, p.getSellPrice());
                pstmt.setInt(4, p.getNbItems());

                String category = "";
                if (p instanceof Clothes) category = "CLOTHES";
                else if (p instanceof Shoes) category = "SHOES";
                else category = "ACCESSORY";

                pstmt.setString(5, category);
                pstmt.executeUpdate();
            }

            // Get the generated ID using a separate query
            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT MAX(id) FROM products")) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    p.setId(id);

                    // If it's Clothes or Shoes, insert into sub-tables using same connection
                    if (p instanceof Clothes) {
                        insertClothes(conn, id, ((Clothes) p).getSize());
                    } else if (p instanceof Shoes) {
                        insertShoes(conn, id, ((Shoes) p).getShoeSize());
                    }
                }
            }

            conn.commit();
        }
    }

    private void insertClothes(Connection conn, int id, int size) throws SQLException {
        String sql = "INSERT INTO clothes (product_id, size_val) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setInt(2, size);
            pstmt.executeUpdate();
        }
    }

    private void insertShoes(Connection conn, int id, int shoeSize) throws SQLException {
        String sql = "INSERT INTO shoes (product_id, shoe_size) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setInt(2, shoeSize);
            pstmt.executeUpdate();
        }
    }

    // This method updates stock and prices in DB
    public void updateProduct(Product p) throws SQLException {
        String sql = "UPDATE products SET nb_items = ?, discount_price = ?, total_sold = ?, total_bought = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, p.getNbItems());
            pstmt.setDouble(2, p.getDiscountPrice());
            pstmt.setInt(3, p.getTotalSold());
            pstmt.setInt(4, p.getTotalBought());
            pstmt.setInt(5, p.getId());
            pstmt.executeUpdate();
        }
    }

    // Method to fetch all products from the database using LEFT JOINs
    public List<Product> getAllProducts() throws SQLException {
        List<Product> products = new ArrayList<>();
        String query = "SELECT p.*, c.size_val, s.shoe_size " +
                       "FROM products p " +
                       "LEFT JOIN clothes c ON p.id = c.product_id " +
                       "LEFT JOIN shoes s ON p.id = s.product_id";

        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                Product p = null;
                String category = rs.getString("category");
                String name = rs.getString("name");
                double pPrice = rs.getDouble("purchase_price");
                double sPrice = rs.getDouble("sell_price");

                // Polymorphic object creation based on category column
                switch (category) {
                    case "CLOTHES":
                        p = new Clothes(name, pPrice, sPrice, rs.getInt("size_val"));
                        break;
                    case "SHOES":
                        p = new Shoes(name, pPrice, sPrice, rs.getInt("shoe_size"));
                        break;
                    case "ACCESSORY":
                        p = new Accessory(name, pPrice, sPrice);
                        break;
                }

                if (p != null) {
                    p.setId(rs.getInt("id"));
                    p.setNbItems(rs.getInt("nb_items"));
                    p.setDiscountPrice(rs.getDouble("discount_price"));
                    p.setTotalSold(rs.getInt("total_sold"));
                    p.setTotalBought(rs.getInt("total_bought"));
                    products.add(p);
                }
            }
        }
        return products;
    }

    public void deleteProduct(int id, int stock) throws SQLException {
        // Validation rule: cannot delete if stock > 0
        if (stock > 0) {
            throw new IllegalArgumentException("Product cannot be deleted: stock still available");
        }
        
        String sql = "DELETE FROM products WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        }
    }
}