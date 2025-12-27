package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import model.Orders;

public class SaleItemsDAO {
    
    public boolean insertSalesItem(Connection conn, long salesId, List<Orders> orders){
        String sqlQuery = """
                INSERT INTO sale_items 
                (sales_id, product_id, qty, price, total_amount, order_date)
                VALUES (?, ?, ?, ?, ?, ?)
                """;
        try (PreparedStatement ps = conn.prepareStatement(sqlQuery)){

            for(Orders o: orders){
                ps.setLong(1, salesId);
                ps.setLong(2, o.getProductId());
                ps.setInt(3, o.getQuantity());
                ps.setDouble(4, o.getPrice());
                ps.setDouble(5, o.getTotal());
                ps.setTimestamp(6, java.sql.Timestamp.valueOf(o.getOrderDate()));
                ps.addBatch();
            }

            ps.executeBatch();
            return true;

        } catch(SQLException e){
            System.out.println("Error inserting sale items: " + e);
            return false;
        }
    }
}
