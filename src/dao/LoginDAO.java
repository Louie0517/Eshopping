package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.DatabaseConnection;

public class LoginDAO {

    public boolean isBuyer(String email){
        String sqlQuerry = "SELECT role FROM users WHERE role = 'Buyer' LIMIT 1";
        try(Connection conn = DatabaseConnection.getConnection();
        PreparedStatement ps = conn.prepareStatement(sqlQuerry)){
            ps.setString(1, email);
            try(ResultSet rs = ps.executeQuery()){
                if(rs.next()){
                    String role = rs.getString("role");
                    return "Buyer".equalsIgnoreCase(role);
                }
            }
        } catch (SQLException e){
            e.getMessage();
        }
        return false;
    }
}
