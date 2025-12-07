package db; // Package declaration

import java.sql.*; // Import JDBC classes: Connection, DriverManager, PreparedStatement, ResultSet, etc.

public class DatabaseConnection { // Class that handles database connections
    private static final String URL = "jdbc:mysql://localhost:3306/ecomm"; // MySQL connection string
    private static final String USER = "root"; // Database username
    private static final String PASSWORD = "Brentcasas@0517"; // Database password (change if needed)

    public static Connection getConnection() { // Method to get a connection object
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Load MySQL JDBC driver
            return DriverManager.getConnection(URL, USER, PASSWORD); // Connect to DB and return Connection
        } catch (Exception e) { // Catch driver or connection errors
             System.err.println("❌ Database connection failed: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
