package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import db.DatabaseConnection;
import model.Product;


public class SearchDAO {
    
  
    public List<Product> search(String keyword) throws Exception {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT p.id, p.product_name, c.name AS category_name, "+
                    "b.name AS brand_name, s.name AS supplier_name, "+
                    "p.unit, p.price, p.stock, p.markup, p.selling_price, p.image ,p.date_added "+
                    "FROM products AS p "+
                    "INNER JOIN categories AS c ON p.category_id = c.id " +
                    "INNER JOIN brands AS b ON p.brand_id = b.id " +
                    "INNER JOIN suppliers s ON p.supplier_id = s.id " +
                    "WHERE LOWER(p.product_name) LIKE LOWER(?) " +
                     "   OR LOWER(c.name) LIKE LOWER(?) " +
                     "   OR LOWER(b.name) LIKE LOWER(?) " +
                     "   OR LOWER(s.name) LIKE LOWER(?) " +
                     "ORDER BY p.product_name";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            String searchPattern = "%" + keyword + "%";
            stmt.setString(1, searchPattern);
            stmt.setString(2, searchPattern);
            stmt.setString(3, searchPattern);
            stmt.setString(4, searchPattern);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Product product = new Product();
                    product.setId(rs.getInt("id"));
                    product.setProductName(rs.getString("product_name"));
                    product.setCategoryName(rs.getString("category_name"));
                    product.setBrandName(rs.getString("brand_name"));
                    product.setSupplierName(rs.getString("supplier_name"));
                    product.setUnit(rs.getString("unit"));
                    product.setPrice(rs.getDouble("price"));
                    product.setStock(rs.getInt("stock"));
                    product.setMarkUp(rs.getDouble("markup"));
                    product.setSellingPrice(rs.getDouble("selling_price"));
                    product.setImagePath(rs.getString("image"));
                    product.setDateAdded(rs.getDate("date_added").toLocalDate());
                    
                    products.add(product);
                }
            }
        }
        
        return products;
    }
}