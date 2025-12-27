package dao;

import db.DatabaseConnection;
import model.InventoryOverview;
import model.InventoryLog;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InventoryDAO {
  
    public List<InventoryOverview> getInventoryOverview() {
        List<InventoryOverview> list = new ArrayList<>();
        String sql = """
            SELECT 
                p.id as product_id,
                p.product_name,
                p.stock,
                COALESCE(i.updated_at, NOW()) as updated_at
            FROM products p
            LEFT JOIN inventory i ON p.id = i.product_id
            ORDER BY p.product_name
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                InventoryOverview item = new InventoryOverview();
                item.setProductId(rs.getInt("product_id"));
                item.setName(rs.getString("product_name"));
                item.setStock(rs.getInt("stock"));
                
                Timestamp ts = rs.getTimestamp("updated_at");
                if (ts != null) {
                    item.setUpdatedAt(ts.toLocalDateTime());
                }
                
                list.add(item);
            }

        } catch (SQLException e) {
            System.out.println("Error getting inventory overview: " + e);
            e.printStackTrace();
        }

        return list;
    }
    
    public boolean syncInventory() {
        String sql = """
            INSERT INTO inventory (product_id, stock)
            SELECT id, stock FROM products
            ON DUPLICATE KEY UPDATE 
                stock = VALUES(stock),
                updated_at = CURRENT_TIMESTAMP
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {
            
            st.executeUpdate(sql);
            return true;

        } catch (SQLException e) {
            System.out.println("Error syncing inventory: " + e);
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean initializeInventory() {
        String sql = """
            INSERT INTO inventory (product_id, stock)
            SELECT id, stock FROM products
            WHERE id NOT IN (SELECT product_id FROM inventory)
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement()) {
            
            int rows = st.executeUpdate(sql);
            System.out.println("Initialized " + rows + " products in inventory table");
            return true;

        } catch (SQLException e) {
            System.out.println("Error initializing inventory: " + e);
            e.printStackTrace();
            return false;
        }
    }

    public boolean addStock(int productId, int quantity, int userId, String reason) {
        String updateProduct = "UPDATE products SET stock = stock + ? WHERE id = ?";
        String updateInventory = """
            INSERT INTO inventory (product_id, stock)
            SELECT id, stock FROM products WHERE id = ?
            ON DUPLICATE KEY UPDATE 
                stock = (SELECT stock FROM products WHERE id = ?),
                updated_at = CURRENT_TIMESTAMP
        """;
        String insertLog = "INSERT INTO inventory_logs (product_id, user_id, change_qty, action, reason) VALUES (?, ?, ?, 'IN', ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement(updateProduct)) {
                ps.setInt(1, quantity);
                ps.setInt(2, productId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(updateInventory)) {
                ps.setInt(1, productId);
                ps.setInt(2, productId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(insertLog)) {
                ps.setInt(1, productId);
                ps.setInt(2, userId);
                ps.setInt(3, quantity);
                ps.setString(4, reason);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Error adding stock: " + e);
            e.printStackTrace();
            return false;
        }
    }
    
    public boolean removeStock(int productId, int quantity, int userId, String reason) {
        String checkStock = "SELECT stock FROM products WHERE id = ?";
        String updateProduct = "UPDATE products SET stock = stock - ? WHERE id = ?";
        String updateInventory = """
            INSERT INTO inventory (product_id, stock)
            SELECT id, stock FROM products WHERE id = ?
            ON DUPLICATE KEY UPDATE 
                stock = (SELECT stock FROM products WHERE id = ?),
                updated_at = CURRENT_TIMESTAMP
        """;
        String insertLog = "INSERT INTO inventory_logs (product_id, user_id, change_qty, action, reason) VALUES (?, ?, ?, 'OUT', ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            conn.setAutoCommit(false);

            int currentStock = 0;
            try (PreparedStatement ps = conn.prepareStatement(checkStock)) {
                ps.setInt(1, productId);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    currentStock = rs.getInt("stock");
                }
            }

            if (currentStock < quantity) {
                conn.rollback();
                System.out.println("Insufficient stock. Available: " + currentStock + ", Requested: " + quantity);
                return false;
            }

            try (PreparedStatement ps = conn.prepareStatement(updateProduct)) {
                ps.setInt(1, quantity);
                ps.setInt(2, productId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(updateInventory)) {
                ps.setInt(1, productId);
                ps.setInt(2, productId);
                ps.executeUpdate();
            }

            try (PreparedStatement ps = conn.prepareStatement(insertLog)) {
                ps.setInt(1, productId);
                ps.setInt(2, userId);
                ps.setInt(3, quantity);
                ps.setString(4, reason);
                ps.executeUpdate();
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            System.out.println("Error removing stock: " + e);
            e.printStackTrace();
            return false;
        }
    }
    
    public List<InventoryLog> getInventoryLogs(int productId) {
        List<InventoryLog> logs = new ArrayList<>();
        String sql = """
            SELECT 
                il.id,
                il.product_id,
                p.product_name,
                il.user_id,
                u.username,
                il.change_qty,
                il.action,
                il.reason,
                il.created_at
            FROM inventory_logs il
            LEFT JOIN products p ON il.product_id = p.id
            LEFT JOIN users u ON il.user_id = u.id
            WHERE il.product_id = ?
            ORDER BY il.created_at DESC
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, productId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                InventoryLog log = new InventoryLog();
                log.setId(rs.getInt("id"));
                log.setProductId(rs.getInt("product_id"));
                log.setProductName(rs.getString("product_name"));
                log.setUserId(rs.getInt("user_id"));
                log.setUsername(rs.getString("username"));
                log.setChangeQty(rs.getInt("change_qty"));
                log.setAction(rs.getString("action"));
                log.setReason(rs.getString("reason"));
                
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) {
                    log.setCreatedAt(ts.toLocalDateTime());
                }
                
                logs.add(log);
            }

        } catch (SQLException e) {
            System.out.println("Error getting inventory logs: " + e);
            e.printStackTrace();
        }

        return logs;
    }
    
    public List<InventoryLog> getAllInventoryLogs() {
        List<InventoryLog> logs = new ArrayList<>();
        String sql = """
            SELECT 
                il.id,
                il.product_id,
                p.product_name,
                il.user_id,
                u.username,
                il.change_qty,
                il.action,
                il.reason,
                il.created_at
            FROM inventory_logs il
            LEFT JOIN products p ON il.product_id = p.id
            LEFT JOIN users u ON il.user_id = u.id
            ORDER BY il.created_at DESC
            LIMIT 100
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            while (rs.next()) {
                InventoryLog log = new InventoryLog();
                log.setId(rs.getInt("id"));
                log.setProductId(rs.getInt("product_id"));
                log.setProductName(rs.getString("product_name"));
                log.setUserId(rs.getInt("user_id"));
                log.setUsername(rs.getString("username"));
                log.setChangeQty(rs.getInt("change_qty"));
                log.setAction(rs.getString("action"));
                log.setReason(rs.getString("reason"));
                
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) {
                    log.setCreatedAt(ts.toLocalDateTime());
                }
                
                logs.add(log);
            }

        } catch (SQLException e) {
            System.out.println("Error getting all inventory logs: " + e);
            e.printStackTrace();
        }

        return logs;
    }
    
    public InventoryStats getInventoryStats() {
        String sql = """
            SELECT 
                SUM(CASE WHEN action = 'IN' THEN change_qty ELSE 0 END) as total_incoming,
                SUM(CASE WHEN action = 'OUT' THEN change_qty ELSE 0 END) as total_outgoing,
                COUNT(CASE WHEN action = 'IN' THEN 1 END) as incoming_count,
                COUNT(CASE WHEN action = 'OUT' THEN 1 END) as outgoing_count
            FROM inventory_logs
            WHERE DATE(created_at) = CURDATE()
        """;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            if (rs.next()) {
                return new InventoryStats(
                    rs.getInt("total_incoming"),
                    rs.getInt("total_outgoing"),
                    rs.getInt("incoming_count"),
                    rs.getInt("outgoing_count")
                );
            }

        } catch (SQLException e) {
            System.out.println("Error getting inventory stats: " + e);
            e.printStackTrace();
        }

        return new InventoryStats(0, 0, 0, 0);
    }
    
    public static class InventoryStats {
        public int totalIncoming;
        public int totalOutgoing;
        public int incomingCount;
        public int outgoingCount;
        
        public InventoryStats(int totalIncoming, int totalOutgoing, int incomingCount, int outgoingCount) {
            this.totalIncoming = totalIncoming;
            this.totalOutgoing = totalOutgoing;
            this.incomingCount = incomingCount;
            this.outgoingCount = outgoingCount;
        }
    }
}