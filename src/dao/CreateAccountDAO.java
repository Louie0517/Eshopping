package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import db.DatabaseConnection;
import model.CreateAccount;
import util.DateUtil;

public class CreateAccountDAO {
    
    public boolean addUser(CreateAccount user){
        String sql = 
        "INSERT INTO users(username, password, email, phone_no, birthday, address, gender, role, biography, profile_path) " + 
        "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try(Connection con = DatabaseConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql)){
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail());
                ps.setString(4, user.getPhone());
                ps.setDate(5, java.sql.Date.valueOf(user.getDateOfBirth()));
                ps.setString(6, user.getAddress());
                ps.setString(7, user.getGender());
                ps.setString(8, user.getRole());
                ps.setString(9, user.getBio());
                ps.setString(10, user.getProfilePicPath());
                
                return ps.executeUpdate() > 0;
            } catch (SQLException e) {
                e.printStackTrace();
                  return false;
            }
    }

    public boolean isEmailExists(String email){
        String sqlQuerry = "SELECT 1 FROM users WHERE email = ? LIMIT 1";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuerry)){
                ps.setString(1, email);
                ResultSet rs = ps.executeQuery();
                return (rs.next()) ? true : false;
        } 
        catch(SQLException e){
            e.printStackTrace(); return false;
        }
    }


    public CreateAccount getUserByEmail(String email) throws SQLException {
        String sqlQuery = "SELECT * FROM users WHERE email = ?";

        try (Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery)) {

            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                CreateAccount user = new CreateAccount();
                user.setUserId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone_no"));
                user.setRole(rs.getString("role"));
                return user;
            }
        }
        return null;
    }

    // -------------------------------------------------------------||
    //  get the userID to handle backend process regarding between  ||
    //  the user and the backend server                             ||
    //                                                              ||
    // -------------------------------------------------------------||
    public CreateAccount getUserById(int id) throws SQLException {
        String sqlQuerry = "SELECT * FROM users WHERE id = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuerry)){
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if(rs.next()){
                CreateAccount user = new CreateAccount();
                user.setUserId(rs.getLong("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone_no"));
                user.setRole(rs.getString("role"));
                return user;
            }
            
        }
        return null;
    }   

     public boolean updateUser(CreateAccount user) throws SQLException {
        String sqlQuery = "UPDATE users SET "
            + "username = ?, "
            + "password = ?, "
            + "email = ?, "
            + "phone_no = ?, "
            + "birthday = ?, "
            + "address = ?, "
            + "gender = ?, "
            + "biography = ?, "
            + "profile_path = ? "
            + "WHERE id = ?";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuery)){
                ps.setString(1, user.getUsername());
                ps.setString(2, user.getPassword());
                ps.setString(3, user.getEmail());
                ps.setString(4, DateUtil.format(user.getDateOfBirth()));
                ps.setString(5, user.getAddress());
                ps.setString(6, user.getGender());
                ps.setString(7, user.getBio());
                ps.setString(8, user.getProfilePicPath());
                return ps.executeUpdate() > 0;
            }

      }

    public boolean setStatusActive(int id) {
        String sqlQuerry = "UPDATE users SET status = 'Active' WHERE id = ? ";

        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuerry)){
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
                
            } catch(SQLException e){
                e.printStackTrace();
                return false;
            }
     }

    public boolean setStatusOffline(int id) {
        String sqlQuerry = "UPDATE users SET status = 'Offline' WHERE id = ? ";
        try(Connection conn = DatabaseConnection.getConnection();
            PreparedStatement ps = conn.prepareStatement(sqlQuerry)){
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
                
            } catch(SQLException e){
                e.printStackTrace();
                return false;
            }
     }

    public boolean deleteUser(int id) throws SQLException {
        String sqlQuery = "DELETE users WHERE id = ?";

            try(Connection conn = DatabaseConnection.getConnection();
                PreparedStatement ps = conn.prepareStatement(sqlQuery)){
                ps.setInt(1, id);
                return ps.executeUpdate() > 0;
        }
    }

}
