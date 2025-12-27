package dao;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import db.DatabaseConnection;
import model.SalesReport;
import model.TopProduct;

public class SalesDAO {
    
    public SalesReport getDailySalesReport(LocalDate date) {
    String sql = """
        SELECT
            summary_date AS report_date,
            SUM(total_revenue) AS total_revenue,
            COUNT(*) AS total_transactions,
            SUM(total_items_sold) AS total_items_sold
        FROM sales
        WHERE summary_date = ?
        GROUP BY summary_date
    """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setDate(1, Date.valueOf(date));
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new SalesReport(
                rs.getDate("report_date").toLocalDate(),
                rs.getDouble("total_revenue"),
                rs.getInt("total_transactions"),
                rs.getInt("total_items_sold"),
                "DAILY"
            );
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

    
    public SalesReport getMonthlySalesReport(int year, int month) {
    String sql = """
        SELECT
            YEAR(summary_date) AS year,
            MONTH(summary_date) AS month,
            SUM(total_revenue) AS total_revenue,
            COUNT(*) AS total_transactions,
            SUM(total_items_sold) AS total_items_sold
        FROM sales
        WHERE YEAR(summary_date) = ? AND MONTH(summary_date) = ?
        GROUP BY YEAR(summary_date), MONTH(summary_date)
    """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, year);
        ps.setInt(2, month);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new SalesReport(
                LocalDate.of(year, month, 1),
                rs.getDouble("total_revenue"),
                rs.getInt("total_transactions"),
                rs.getInt("total_items_sold"),
                "MONTHLY"
            );
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

public SalesReport getYearlySalesReport(int year) {
    String sql = """
        SELECT
            YEAR(summary_date) AS year,
            SUM(total_revenue) AS total_revenue,
            COUNT(*) AS total_transactions,
            SUM(total_items_sold) AS total_items_sold
        FROM sales
        WHERE YEAR(summary_date) = ?
        GROUP BY YEAR(summary_date)
    """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, year);
        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new SalesReport(
                LocalDate.of(year, 1, 1),
                rs.getDouble("total_revenue"),
                rs.getInt("total_transactions"),
                rs.getInt("total_items_sold"),
                "YEARLY"
            );
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

    
    public SalesReport getTotalSalesReport() {
    String sql = """
        SELECT
            SUM(total_revenue) AS total_revenue,
            COUNT(*) AS total_transactions,
            SUM(total_items_sold) AS total_items_sold,
            MIN(summary_date) AS first_date,
            MAX(summary_date) AS last_date
        FROM sales
    """;

    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ResultSet rs = ps.executeQuery();

        if (rs.next()) {
            return new SalesReport(
                rs.getDate("last_date").toLocalDate(),
                rs.getDouble("total_revenue"),
                rs.getInt("total_transactions"),
                rs.getInt("total_items_sold"),
                "ALL_TIME"
            );
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}

    
    
    public List<TopProduct> getTopSellingProducts(LocalDate startDate, LocalDate endDate, int limit) {
        String sql = """
            SELECT
                si.product_id,
                p.product_name,
                SUM(si.qty) as total_quantity_sold,
                SUM(si.total_amount) as total_revenue,
                COUNT(DISTINCT si.sales_id) as times_purchased
            FROM sale_items si
            LEFT JOIN products p ON si.product_id = p.id
            WHERE DATE(si.order_date) BETWEEN ? AND ?
            GROUP BY si.product_id, p.product_name
            ORDER BY total_quantity_sold DESC
            LIMIT ?
        """;

        List<TopProduct> topProducts = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(endDate));
            ps.setInt(3, limit);
            
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                topProducts.add(new TopProduct(
                    rs.getLong("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("total_quantity_sold"),
                    rs.getDouble("total_revenue"),
                    rs.getInt("times_purchased")
                ));
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting top selling products: " + e);
        }
        
        return topProducts;
    }
    
    // Get top selling products for today
    public List<TopProduct> getTopSellingProductsToday(int limit) {
        LocalDate today = LocalDate.now();
        return getTopSellingProducts(today, today, limit);
    }
    
    // Get top selling products for current month
    public List<TopProduct> getTopSellingProductsThisMonth(int limit) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfMonth = today.withDayOfMonth(1);
        return getTopSellingProducts(firstDayOfMonth, today, limit);
    }
    
    // Get top selling products for current year
    public List<TopProduct> getTopSellingProductsThisYear(int limit) {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfYear = today.withDayOfYear(1);
        return getTopSellingProducts(firstDayOfYear, today, limit);
    }
    
    // Get top selling products all time
    public List<TopProduct> getTopSellingProductsAllTime(int limit) {
        String sql = """
            SELECT
                si.product_id,
                p.product_name,
                SUM(si.qty) as total_quantity_sold,
                SUM(si.total_amount) as total_revenue,
                COUNT(DISTINCT si.sales_id) as times_purchased
            FROM sale_items si
            LEFT JOIN products p ON si.product_id = p.id
            GROUP BY si.product_id, p.product_name
            ORDER BY total_quantity_sold DESC
            LIMIT ?
        """;

        List<TopProduct> topProducts = new ArrayList<>();

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                topProducts.add(new TopProduct(
                    rs.getLong("product_id"),
                    rs.getString("product_name"),
                    rs.getInt("total_quantity_sold"),
                    rs.getDouble("total_revenue"),
                    rs.getInt("times_purchased")
                ));
            }
            
        } catch (SQLException e) {
            System.out.println("Error getting top selling products all time: " + e);
        }
        
        return topProducts;
    }
}