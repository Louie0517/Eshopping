package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Orders;
import db.DatabaseConnection;

public class OrdersDAO {

    public boolean cancelOrderStatus(Long orderId){
        String sqlQuery = "UPDATE order_items SET status = 'Cancelled' WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sqlQuery)){

            ps.setLong(1, orderId);

            System.out.println("Executing SQL: " + sqlQuery);
            System.out.println("Parameters: status='Cancelled', id=" + orderId);
            
            int rowsAffected = ps.executeUpdate();
            
            System.out.println("Rows affected: " + rowsAffected);
            System.out.println("Success: " + (rowsAffected > 0));

            return ps.executeUpdate() > 0;

         }catch(SQLException e){
            System.out.println("Error at cancel status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public boolean updateOrderStatus(Long orderId, String newStatus){
        String sqlQuery = "UPDATE order_items SET status = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sqlQuery)){

            ps.setString(1, newStatus);
            ps.setLong(2, orderId);

            return ps.executeUpdate() > 0;

         }catch(SQLException e){
            System.out.println("Error at updating status: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public List<Orders> getOrderByStatus(Long userId, String status) {
        List<Orders> list = new ArrayList<>();
        
        String checkQuery = "SELECT COUNT(*) as total FROM order_items WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkQuery)) {
            checkPs.setLong(1, userId);
            ResultSet checkRs = checkPs.executeQuery();
            if (checkRs.next()) {
                System.out.println("Total orders for userId " + userId + ": " + checkRs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        String statusCheckQuery = "SELECT COUNT(*) as total FROM order_items WHERE user_id = ? AND status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement statusPs = conn.prepareStatement(statusCheckQuery)) {
            statusPs.setLong(1, userId);
            statusPs.setString(2, status);
            ResultSet statusRs = statusPs.executeQuery();
            if (statusRs.next()) {
                System.out.println("Orders with status '" + status + "' for userId " + userId + ": " + statusRs.getInt("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        String sqlQuery = """
                SELECT o.id AS order_id,
                u.username, u.phone_no, u.address,
                p.product_name, p.image, o.status, o.total_amount,
                o.order_date, o.quantity, p.price
                FROM order_items AS o 
                INNER JOIN products AS p ON o.product_id = p.id
                INNER JOIN users AS u ON o.user_id = u.id
                WHERE o.user_id = ? AND o.status = ?
                ORDER BY o.order_date DESC
                """;
                
        try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sqlQuery)){

            ps.setLong(1, userId);
            ps.setString(2, status);
            
            System.out.println("Executing query for userId: " + userId + ", status: '" + status + "'");

            ResultSet rs = ps.executeQuery();
            int count = 0;
            while(rs.next()){
                Orders o = new Orders();
                o.setId(rs.getInt("order_id"));
                o.setUsername(rs.getString("username"));
                o.setPhoneNumber(rs.getString("phone_no"));
                o.setAddress(rs.getString("address"));
                o.setProductName(rs.getString("product_name"));
                o.setImagePath(rs.getString("image"));
                o.setStatus(rs.getString("status"));
                o.setTotal(rs.getDouble("total_amount"));
                o.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                o.setQuantity(rs.getInt("quantity"));
                o.setPrice(rs.getDouble("price"));
                list.add(o);
                count++;
            }
            System.out.println("Pending orders found: " + count);
        } catch(SQLException e){
            System.out.println("Error at order by status: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    public List<Orders> getAllOrdersByStatus(String status) {
        List<Orders> list = new ArrayList<>();
        String sqlQuery = """
                SELECT o.id AS order_id,
                u.username, u.phone_no, u.address, u.role,
                p.product_name, p.image, o.status, o.total_amount,
                o.order_date, o.quantity, p.price
                FROM order_items AS o 
                INNER JOIN products AS p ON o.product_id = p.id
                INNER JOIN users AS u ON o.user_id = u.id
                WHERE o.status = ? AND u.role = 'Buyer'
                ORDER BY o.order_date DESC
                """;
                
        try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sqlQuery)){

            ps.setString(1, status);
            
            System.out.println("Executing admin query for status: '" + status + "'");

            ResultSet rs = ps.executeQuery();
            int count = 0;
            while(rs.next()){
                Orders o = new Orders();
                o.setId(rs.getInt("order_id"));
                o.setUsername(rs.getString("username"));
                o.setPhoneNumber(rs.getString("phone_no"));
                o.setAddress(rs.getString("address"));
                o.setProductName(rs.getString("product_name"));
                o.setImagePath(rs.getString("image"));
                o.setStatus(rs.getString("status"));
                o.setTotal(rs.getDouble("total_amount"));
                o.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                o.setQuantity(rs.getInt("quantity"));
                o.setPrice(rs.getDouble("price"));
                list.add(o);
                count++;
            }
            System.out.println("Total orders found with status '" + status + "': " + count);
        } catch(SQLException e){
            System.out.println("Error at getAllOrdersByStatus: " + e.getMessage());
            e.printStackTrace();
        }
        return list;
    }

    

    public int countOrdersPerStatus(String status){
        int count = 0;
        String sqlQuery = "SELECT COUNT(*) FROM order_items WHERE status = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

                ps.setString(1, status);

                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    count = rs.getInt(1);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }

        return count; 
    }


    public List<Orders> getOrderDetails(Long userId) {
        List<Orders> list = new ArrayList<>();

        String sqlQuery = "SELECT p.product_name, o.status, o.total_amount, o.order_date, o.quantity, p.price " +
                          "FROM order_items AS o " +
                          "INNER JOIN products AS p ON o.product_id = p.id "+
                          "WHERE o.user_id=?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

            ps.setLong(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Orders o = new Orders();
                    o.setProductName(rs.getString("product_name"));
                    o.setStatus(rs.getString("status"));
                    o.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());
                    o.setQuantity(rs.getInt("quantity"));
                    o.setPrice(rs.getDouble("price"));
                    list.add(o);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list; 
    }

    public boolean productHasOrders(int productId) {
        String sql = "SELECT 1 FROM order_items WHERE product_id = ? LIMIT 1";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); 
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public Orders getOrderById(Long orderId) {
         String sql = "SELECT p.product_name, o.quantity, p.price, o.status, u.email " +
                 "FROM order_items o " +
                 "JOIN products AS p ON o.product_id = p.id " +
                 "JOIN users AS u ON o.user_id = u.id " +
                 "WHERE o.id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setLong(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Orders order = new Orders();
                order.setProductName(rs.getString("product_name"));
                order.setQuantity(rs.getInt("quantity"));
                order.setPrice(rs.getDouble("price"));
                order.setStatus(rs.getString("status"));
                order.setEmail(rs.getString("email"));
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public List<Orders> getAllOrders(Long userId) {
        List<Orders> list = new ArrayList<>();

        String sqlQuery =
            "SELECT o.id, o.quantity, o.status, o.order_date, " +
            "p.selling_price, p.product_name, p.image, b.name AS brand_name " +
            "FROM order_items AS o " +
            "INNER JOIN products AS p ON o.product_id = p.id " +
            "INNER JOIN brands AS b ON p.brand_id = b.id " +
            "WHERE o.user_id = ? " +
            "ORDER BY o.order_date DESC";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

            ps.setLong(1, userId); 
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Orders o = new Orders();
                    o.setId(rs.getInt("id"));
                    o.setQuantity(rs.getInt("quantity"));
                    o.setStatus(rs.getString("status"));
                    o.setPrice(rs.getDouble("selling_price"));
                    o.setProductName(rs.getString("product_name"));
                    o.setImagePath(rs.getString("image"));
                    o.setBrandName(rs.getString("brand_name"));
                    o.setOrderDate(rs.getTimestamp("order_date").toLocalDateTime());

                    list.add(o);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return list;
    }

}