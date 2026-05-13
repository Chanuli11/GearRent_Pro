package com.gearrent.dao;
import com.gearrent.entity.Category; import com.gearrent.util.DBConnection;
import java.math.BigDecimal; import java.sql.*; import java.util.*;
public class CategoryDAO {
    public List<Category> findAll() throws SQLException {
        List<Category> list=new ArrayList<>();
        try (Connection c=DBConnection.getInstance().getConnection();
             Statement s=c.createStatement();
             ResultSet rs=s.executeQuery("SELECT * FROM category ORDER BY name")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }
    public Category findById(int id) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement("SELECT * FROM category WHERE category_id=?")) {
            ps.setInt(1,id);
            try (ResultSet rs=ps.executeQuery()){ if (rs.next()) return map(rs);}
        }
        return null;
    }
    public void save(Category c0) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(
               "INSERT INTO category(name,description,base_price_factor,weekend_multiplier,late_fee_per_day,active) VALUES(?,?,?,?,?,?)")) {
            bind(ps,c0); ps.executeUpdate();
        }
    }
    public void update(Category c0) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(
               "UPDATE category SET name=?,description=?,base_price_factor=?,weekend_multiplier=?,late_fee_per_day=?,active=? WHERE category_id=?")) {
            bind(ps,c0); ps.setInt(7,c0.getCategoryId()); ps.executeUpdate();
        }
    }
    public void delete(int id) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement("DELETE FROM category WHERE category_id=?")) {
            ps.setInt(1,id); ps.executeUpdate();
        }
    }
    private void bind(PreparedStatement ps, Category c) throws SQLException {
        ps.setString(1,c.getName()); ps.setString(2,c.getDescription());
        ps.setBigDecimal(3,c.getBasePriceFactor()); ps.setBigDecimal(4,c.getWeekendMultiplier());
        ps.setBigDecimal(5,c.getLateFeePerDay()); ps.setBoolean(6,c.isActive());
    }
    private Category map(ResultSet rs) throws SQLException {
        Category c=new Category();
        c.setCategoryId(rs.getInt("category_id")); c.setName(rs.getString("name"));
        c.setDescription(rs.getString("description"));
        c.setBasePriceFactor(rs.getBigDecimal("base_price_factor"));
        c.setWeekendMultiplier(rs.getBigDecimal("weekend_multiplier"));
        c.setLateFeePerDay(rs.getBigDecimal("late_fee_per_day"));
        c.setActive(rs.getBoolean("active"));
        return c;
    }
}
