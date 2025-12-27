package dao;

import model.Product;
import db.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProductDAO {

    public List<Product> getProductsSorted(String orderBy) {
        List<Product> list = new ArrayList<>();
       
        Set<String> validOrderBy = Set.of(
            "p.selling_price ASC", "p.selling_price DESC",
            "p.product_name ASC", "p.product_name DESC",
            "p.stock ASC", "p.stock DESC",
            "p.id ASC"
        );
        
        if (!validOrderBy.contains(orderBy)) {
            orderBy = "p.id ASC"; 
        }
        
        String sql = "SELECT " +
                    "p.id, " +
                    "p.product_name, " +
                    "p.selling_price, " +
                    "p.stock, " +
                    "p.image, " +
                    "c.name AS category_name, " +
                    "b.name AS brand_name " +
                    "FROM products p " +
                    "JOIN categories c ON p.category_id = c.id " +
                    "JOIN brands b ON p.brand_id = b.id " +
                    "ORDER BY " + orderBy;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setProductName(rs.getString("product_name"));
                p.setSellingPrice(rs.getDouble("selling_price"));
                p.setStock(rs.getInt("stock"));
                p.setImagePath(rs.getString("image"));
                p.setCategoryName(rs.getString("category_name"));
                p.setBrandName(rs.getString("brand_name"));
                list.add(p);
            }

        } catch (SQLException e) {
            System.out.println("Error sorting products: " + e.getMessage());
            e.printStackTrace();
        }

        return list;
    }


    public List<Product> getProductsdetails(){
         List<Product> list = new ArrayList<>();
         String sqlQuery = "SELECT p.id, p.product_name, c.name AS category_name, b.name AS brand_name, p.selling_price, p.stock, p.image " +
                            "FROM products AS p " +
                            "INNER JOIN categories AS c ON p.category_id = c.id " +
                            "INNER JOIN brands AS b ON p.brand_id = b.id " +
                            "ORDER BY p.product_name";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
            ResultSet rs = st.executeQuery(sqlQuery)) {

            while (rs.next()) {
                Product p = new Product();
                p.setId(rs.getInt("id"));
                p.setProductName(rs.getString("product_name"));
                p.setCategoryName(rs.getString("category_name"));
                p.setBrandName(rs.getString("brand_name"));
                p.setSellingPrice(rs.getDouble("selling_price"));
                p.setStock(rs.getInt("stock"));
                p.setImagePath(rs.getString("image"));

                list.add(p);
            }
             
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

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
                p.setProductName(rs.getString("product_name"));
                p.setCategoryId(rs.getInt("category_id"));
                p.setBrandId(rs.getInt("brand_id"));
                p.setSupplierId(rs.getInt("supplier_id"));
                p.setUnit(rs.getString("unit"));
                p.setPrice(rs.getDouble("price"));
                p.setStock(rs.getInt("stock"));
                p.setMarkUp(rs.getDouble("markup"));
                p.setSellingPrice(rs.getDouble("selling_price"));
                p.setImagePath(rs.getString("image"));
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
        String sql = "INSERT INTO products(product_name, category_id, brand_id, supplier_id, unit, price, stock, markup, image ,date_added) " +
                     "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, p.getProductName());
            ps.setInt(2, p.getCategoryId());
            ps.setInt(3, p.getBrandId());
            ps.setInt(4, p.getSupplierId());
            ps.setString(5, p.getUnit());
            ps.setDouble(6, p.getPrice());
            ps.setInt(7, p.getStock());
            ps.setDouble(8, p.getMarkUp());
            // img
            ps.setString(9, p.getImagePath());
            ps.setDate(10, Date.valueOf(p.getDateAdded()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== Update product =====
    public boolean updateProduct(Product p) {
        String sql = "UPDATE products SET product_name=?, category_id=?, brand_id=?, supplier_id=?, unit=?, price=?, stock=?, markup=?,date_added=?, image=? " +
                     "WHERE id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, p.getProductName());
            ps.setInt(2, p.getCategoryId());
            ps.setInt(3, p.getBrandId());
            ps.setInt(4, p.getSupplierId());
            ps.setString(5, p.getUnit());
            ps.setDouble(6, p.getPrice());
            ps.setInt(7, p.getStock());
            ps.setDouble(8, p.getMarkUp());
            ps.setDate(9, Date.valueOf(p.getDateAdded()));
            ps.setString(10, p.getImagePath());
            ps.setInt(11, p.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println(e);
            e.printStackTrace();
            return false;
        }
    }

    // ===== Delete product =====
    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE id = ?";

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
                p.setProductName(rs.getString("product_name"));
                p.setCategoryId(rs.getInt("category_id"));
                p.setBrandId(rs.getInt("brand_id"));
                p.setSupplierId(rs.getInt("supplier_id"));
                p.setUnit(rs.getString("unit"));
                p.setPrice(rs.getDouble("price"));
                p.setStock(rs.getInt("stock"));
                p.setDateAdded(rs.getDate("date_added").toLocalDate());
                return p;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    


}
