package com.gearrent.dao;
import com.gearrent.entity.*; import com.gearrent.util.DBConnection;
import java.math.BigDecimal; import java.sql.*; import java.util.*;
public class CustomerDAO {
    public List<Customer> findAll() throws SQLException {
        List<Customer> list=new ArrayList<>();
        try (Connection c=DBConnection.getInstance().getConnection();
             Statement s=c.createStatement();
             ResultSet rs=s.executeQuery("SELECT * FROM customer ORDER BY name")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }
    public Customer findById(int id) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement("SELECT * FROM customer WHERE customer_id=?")) {
            ps.setInt(1,id);
            try (ResultSet rs=ps.executeQuery()){ if (rs.next()) return map(rs);}
        }
        return null;
    }
    public void save(Customer c0) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(
               "INSERT INTO customer(name,nic,contact,email,address,membership) VALUES(?,?,?,?,?,?)")) {
            bind(ps,c0); ps.executeUpdate();
        }
    }
    public void update(Customer c0) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(
               "UPDATE customer SET name=?,nic=?,contact=?,email=?,address=?,membership=? WHERE customer_id=?")) {
            bind(ps,c0); ps.setInt(7,c0.getCustomerId()); ps.executeUpdate();
        }
    }
    public void delete(int id) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement("DELETE FROM customer WHERE customer_id=?")) {
            ps.setInt(1,id); ps.executeUpdate();
        }
    }
    public BigDecimal totalActiveDeposits(int customerId) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(
               "SELECT COALESCE(SUM(security_deposit),0) FROM rental WHERE customer_id=? AND rental_status IN ('ACTIVE','OVERDUE')")) {
            ps.setInt(1,customerId);
            try (ResultSet rs=ps.executeQuery()){ rs.next(); return rs.getBigDecimal(1); }
        }
    }
    private void bind(PreparedStatement ps, Customer c) throws SQLException {
        ps.setString(1,c.getName()); ps.setString(2,c.getNic());
        ps.setString(3,c.getContact()); ps.setString(4,c.getEmail());
        ps.setString(5,c.getAddress()); ps.setString(6,c.getMembership().name());
    }
    private Customer map(ResultSet rs) throws SQLException {
        Customer c=new Customer();
        c.setCustomerId(rs.getInt("customer_id")); c.setName(rs.getString("name"));
        c.setNic(rs.getString("nic")); c.setContact(rs.getString("contact"));
        c.setEmail(rs.getString("email")); c.setAddress(rs.getString("address"));
        c.setMembership(MembershipType.valueOf(rs.getString("membership")));
        return c;
    }
}
