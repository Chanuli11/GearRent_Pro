package com.gearrent.dao;
import com.gearrent.entity.Branch; import com.gearrent.util.DBConnection;
import java.sql.*; import java.util.*;
public class BranchDAO {
    public List<Branch> findAll() throws SQLException {
        List<Branch> list=new ArrayList<>();
        try (Connection c=DBConnection.getInstance().getConnection();
             Statement s=c.createStatement();
             ResultSet rs=s.executeQuery("SELECT * FROM branch ORDER BY name")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }
    public Branch findById(int id) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement("SELECT * FROM branch WHERE branch_id=?")) {
            ps.setInt(1,id);
            try (ResultSet rs=ps.executeQuery()){ if (rs.next()) return map(rs); }
        }
        return null;
    }
    public void save(Branch b) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(
               "INSERT INTO branch(branch_code,name,address,contact) VALUES(?,?,?,?)")) {
            ps.setString(1,b.getBranchCode()); ps.setString(2,b.getName());
            ps.setString(3,b.getAddress()); ps.setString(4,b.getContact());
            ps.executeUpdate();
        }
    }
    public void update(Branch b) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(
               "UPDATE branch SET branch_code=?,name=?,address=?,contact=? WHERE branch_id=?")) {
            ps.setString(1,b.getBranchCode()); ps.setString(2,b.getName());
            ps.setString(3,b.getAddress()); ps.setString(4,b.getContact());
            ps.setInt(5,b.getBranchId()); ps.executeUpdate();
        }
    }
    public void delete(int id) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement("DELETE FROM branch WHERE branch_id=?")) {
            ps.setInt(1,id); ps.executeUpdate();
        }
    }
    private Branch map(ResultSet rs) throws SQLException {
        return new Branch(rs.getInt("branch_id"),rs.getString("branch_code"),
            rs.getString("name"),rs.getString("address"),rs.getString("contact"));
    }
}
