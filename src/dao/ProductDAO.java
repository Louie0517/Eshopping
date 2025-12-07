package dao;

import model.Product;
import db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // ===== Get all products =====
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM products";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setCategoryId(rs.getInt("category_id"));
                p.setBrandId(rs.getInt("brand_id"));
                p.setSupplierId(rs.getInt("supplier_id"));
                p.setUnit(rs.getString("unit"));
                p.setPrice(rs.getDouble("price"));
                p.setQuantity(rs.getInt("quantity"));
                p.setDateAdded(rs.getDate("date_added").toLocalDate());

                list.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

    // ===== Add a new product =====
    public boolean addProduct(Product p) {
        String sql = "INSERT INTO products(name, category_id, brand_id, supplier_id, unit, price, quantity, date_added) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setInt(2, p.getCategoryId());
            ps.setInt(3, p.getBrandId());
            ps.setInt(4, p.getSupplierId());
            ps.setString(5, p.getUnit());
            ps.setDouble(6, p.getPrice());
            ps.setInt(7, p.getQuantity());
            ps.setDate(8, Date.valueOf(p.getDateAdded()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== Update product =====
    public boolean updateProduct(Product p) {
        String sql = "UPDATE products SET name=?, category_id=?, brand_id=?, supplier_id=?, unit=?, price=?, quantity=?, date_added=? " +
                     "WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getName());
            ps.setInt(2, p.getCategoryId());
            ps.setInt(3, p.getBrandId());
            ps.setInt(4, p.getSupplierId());
            ps.setString(5, p.getUnit());
            ps.setDouble(6, p.getPrice());
            ps.setInt(7, p.getQuantity());
            ps.setDate(8, Date.valueOf(p.getDateAdded()));
            ps.setInt(9, p.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== Delete product =====
    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== Get product by ID (optional helper) =====
    public Product getProductById(int id) {
        String sql = "SELECT * FROM products WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setName(rs.getString("name"));
                p.setCategoryId(rs.getInt("category_id"));
                p.setBrandId(rs.getInt("brand_id"));
                p.setSupplierId(rs.getInt("supplier_id"));
                p.setUnit(rs.getString("unit"));
                p.setPrice(rs.getDouble("price"));
                p.setQuantity(rs.getInt("quantity"));
                p.setDateAdded(rs.getDate("date_added").toLocalDate());
                return p;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
