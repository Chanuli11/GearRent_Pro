package com.gearrent.dao;
import com.gearrent.entity.*; import com.gearrent.util.DBConnection;
import java.sql.*;
public class UserDAO {
    public User authenticate(String username, String password) throws SQLException {
        String sql="SELECT * FROM app_user WHERE username=? AND password=?";
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setString(1,username); ps.setString(2,password);
            try (ResultSet rs=ps.executeQuery()) {
                if (rs.next()) {
                    User u=new User();
                    u.setUserId(rs.getInt("user_id"));
                    u.setUsername(rs.getString("username"));
                    u.setPassword(rs.getString("password"));
                    u.setFullName(rs.getString("full_name"));
                    u.setRole(UserRole.valueOf(rs.getString("role")));
                    int b=rs.getInt("branch_id"); u.setBranchId(rs.wasNull()?null:b);
                    return u;
                }
            }
        }
        return null;
    }
}
