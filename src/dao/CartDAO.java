package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;

import db.DatabaseConnection;
import model.Cart;
import model.Orders;

public class CartDAO {

    public List<Cart> getCartByUser(Long userId) {
        List<Cart> cartList = new ArrayList<>();
        
        System.out.println("CartDAO: Querying cart for user_id = " + userId);

        String sql = """
            SELECT 
                c.cart_id,
                c.user_id,
                c.product_id,
                c.quantity,
                p.product_name,
                p.selling_price,
                p.image
            FROM cart c
            INNER JOIN products p ON c.product_id = p.id
            WHERE c.user_id = ?
        """;

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, userId);
            ResultSet rs = ps.executeQuery();
            
            int count = 0;
            while (rs.next()) {
                count++;
                Cart cart = new Cart();
                cart.setCartId(rs.getLong("cart_id"));
                cart.setUserId(rs.getInt("user_id"));
                cart.setProdId(rs.getInt("product_id"));
                cart.setQty(rs.getInt("quantity"));
                cart.setProdName(rs.getString("product_name"));
                cart.setSellingPrice(rs.getDouble("selling_price"));
                cart.setImgPath(rs.getString("image"));

                cartList.add(cart);
                System.out.println("Found item #" + count + ": " + cart.getProdName() + " (qty: " + cart.getQty() + ")");
            }
            
            System.out.println("Total cart items retrieved: " + cartList.size());

        } catch (SQLException e) {
            System.out.println("SQL Error in getCartByUser: " + e.getMessage());
            e.printStackTrace();
        }

        return cartList;
    }   

    public int countCartItems(Long userId){
        int count = 0;
        String sqlQuery = "SELECT COUNT(*) AS count FROM cart WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlQuery)){
                
                ps.setLong(1, userId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    count = rs.getInt("count");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        return count;
    }


    public boolean addCart(Cart c) {
        String sqlQuery = "INSERT INTO cart (user_id, product_id, quantity, buy_date) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery)) {
                
            ps.setInt(1, c.getUserId());        
            ps.setInt(2, c.getProductId());     
            ps.setInt(3, c.getQty());         
            ps.setDate(4, java.sql.Date.valueOf(c.getBuyDate())); 

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateQty(Cart c){
        String sqlQuery = "UPDATE cart SET quantity=? WHERE user_id=? AND product_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

                ps.setInt(1, c.getQty());
                ps.setInt(2, c.getUserId());
                ps.setInt(3, c.getProductId());

                return ps.executeUpdate() > 0;

        } catch (SQLException e) {
                System.out.println(e);
                e.printStackTrace();
                return false;
            }

    }

    public boolean deleteCart(Long cartId){
        String sqlQuery = "DELETE FROM cart WHERE cart_id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

                ps.setLong(1, cartId);
                return ps.executeUpdate() > 0;

        }   catch (SQLException e) {
                System.out.println(e);
                e.printStackTrace();
                return false;
        }
    }

    public boolean checkout(Long userId, List<Cart> items) {

        String stockQuery = "SELECT stock FROM products WHERE id=?";
        String insertOrderQuery = """
            INSERT INTO order_items
            (product_id, user_id, quantity, total_amount, status, order_date)
            VALUES (?, ?, ?, ?, ?, ?)
        """;
        String reduceStock = "UPDATE products SET stock = stock - ? WHERE id=?";
        String clearCartSql = "DELETE FROM cart WHERE user_id=?";

        String upsertSales = """
            INSERT INTO sales (summary_date, created_at, total_revenue, total_transactions, total_items_sold)
            VALUES (?, ?, ?, 1, ?)
            ON DUPLICATE KEY UPDATE
                total_revenue = total_revenue + ?,
                total_transactions = total_transactions + 1,
                total_items_sold = total_items_sold + ?
            """;


        String getSalesIdSql = "SELECT sales_id FROM sales WHERE summary_date = ?";

        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            /* ---------------- STOCK VALIDATION ---------------- */
            for (Cart c : items) {
                try (PreparedStatement ps = conn.prepareStatement(stockQuery)) {
                    ps.setInt(1, c.getProductId());
                    ResultSet rs = ps.executeQuery();

                    if (!rs.next() || c.getQty() > rs.getInt("stock")) {
                        conn.rollback();
                        JOptionPane.showMessageDialog(null, "Insufficient stock");
                        return false;
                    }
                }
            }

            /* ---------------- TOTALS ---------------- */
            double totalAmount = 50; // shipping once
            int totalItems = 0;

            for (Cart c : items) {
                totalAmount += c.getSubtotal();
                totalItems += c.getQty();
            }

            LocalDateTime now = LocalDateTime.now();
            LocalDate today = now.toLocalDate();

            /* ---------------- INSERT ORDERS + UPDATE STOCK ---------------- */
            try (
                PreparedStatement psOrder = conn.prepareStatement(insertOrderQuery);
                PreparedStatement psStock = conn.prepareStatement(reduceStock)
            ) {
                for (Cart c : items) {
                    psOrder.setInt(1, c.getProductId());
                    psOrder.setLong(2, userId);
                    psOrder.setInt(3, c.getQty());
                    psOrder.setDouble(4, c.getSubtotal());
                    psOrder.setString(5, "Pending");
                    psOrder.setTimestamp(6, Timestamp.valueOf(now));
                    psOrder.addBatch();

                    psStock.setInt(1, c.getQty());
                    psStock.setInt(2, c.getProductId());
                    psStock.addBatch();
                }
                psOrder.executeBatch();
                psStock.executeBatch();
            }

            /* ---------------- UPSERT SALES (DAILY) ---------------- */
            try (PreparedStatement ps = conn.prepareStatement(upsertSales)) {
                ps.setDate(1, Date.valueOf(today));
                ps.setTimestamp(2, Timestamp.valueOf(now));
                ps.setDouble(3, totalAmount);        // For INSERT
                ps.setInt(4, totalItems);            // For INSERT
                ps.setDouble(5, totalAmount);        // For UPDATE (duplicate values)
                ps.setInt(6, totalItems);            // For UPDATE (duplicate values)
                ps.executeUpdate();
            }

            /* ---------------- GET SALES ID (ALWAYS WORKS) ---------------- */
            long salesId;
            try (PreparedStatement ps = conn.prepareStatement(getSalesIdSql)) {
                ps.setDate(1, Date.valueOf(today));
                ResultSet rs = ps.executeQuery();
                if (!rs.next()) {
                    conn.rollback();
                    throw new SQLException("Sales ID not found");
                }
                salesId = rs.getLong("sales_id");
            }

            /* ---------------- INSERT SALE ITEMS ---------------- */
            List<Orders> orders = new ArrayList<>();
            for (Cart c : items) {
                Orders o = new Orders();
                o.setProductId(c.getProductId());
                o.setQuantity(c.getQty());
                o.setPrice(c.getSubtotal());
                o.setTotal(c.getSubtotal());
                o.setOrderDate(now);
                orders.add(o);
            }

            SaleItemsDAO saleItemsDAO = new SaleItemsDAO();
            if (!saleItemsDAO.insertSalesItem(conn, salesId, orders)) {
                conn.rollback();
                return false;
            }

            /* ---------------- CLEAR CART ---------------- */
            try (PreparedStatement ps = conn.prepareStatement(clearCartSql)) {
                ps.setLong(1, userId);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (Exception e) {
            try { if (conn != null) conn.rollback(); } catch (SQLException ignored) {}
            e.printStackTrace();
            return false;
        } finally {
            try { if (conn != null) conn.close(); } catch (SQLException ignored) {}
        }
    }

}



