package com.gearrent.dao;
import com.gearrent.entity.*; import com.gearrent.util.DBConnection;
import java.sql.*; import java.util.*;
public class EquipmentDAO {
    private static final String SELECT =
        "SELECT e.*, c.name AS cat_name, b.name AS branch_name FROM equipment e " +
        "JOIN category c ON e.category_id=c.category_id " +
        "JOIN branch b ON e.branch_id=b.branch_id ";

    public List<Equipment> findAll() throws SQLException {
        List<Equipment> list=new ArrayList<>();
        try (Connection c=DBConnection.getInstance().getConnection();
             Statement s=c.createStatement();
             ResultSet rs=s.executeQuery(SELECT+"ORDER BY e.equipment_id")) {
            while (rs.next()) list.add(map(rs));
        }
        return list;
    }
    public List<Equipment> findByBranch(int branchId) throws SQLException {
        List<Equipment> list=new ArrayList<>();
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(SELECT+"WHERE e.branch_id=? ORDER BY e.equipment_id")) {
            ps.setInt(1,branchId);
            try (ResultSet rs=ps.executeQuery()){ while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }
    public List<Equipment> search(Integer branchId, Integer categoryId, EquipmentStatus status, String text) throws SQLException {
        StringBuilder sql=new StringBuilder(SELECT+"WHERE 1=1 ");
        List<Object> params=new ArrayList<>();
        if (branchId!=null){ sql.append("AND e.branch_id=? "); params.add(branchId);}
        if (categoryId!=null){ sql.append("AND e.category_id=? "); params.add(categoryId);}
        if (status!=null){ sql.append("AND e.status=? "); params.add(status.name());}
        if (text!=null && !text.isBlank()){
            sql.append("AND (e.brand LIKE ? OR e.model LIKE ?) ");
            params.add("%"+text+"%"); params.add("%"+text+"%");
        }
        sql.append("ORDER BY e.equipment_id");
        List<Equipment> list=new ArrayList<>();
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(sql.toString())) {
            for (int i=0;i<params.size();i++) ps.setObject(i+1,params.get(i));
            try (ResultSet rs=ps.executeQuery()){ while (rs.next()) list.add(map(rs)); }
        }
        return list;
    }
    public Equipment findById(int id) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(SELECT+"WHERE e.equipment_id=?")) {
            ps.setInt(1,id);
            try (ResultSet rs=ps.executeQuery()){ if (rs.next()) return map(rs);}
        }
        return null;
    }
    public void save(Equipment e) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(
               "INSERT INTO equipment(category_id,branch_id,brand,model,purchase_year,daily_base_price,security_deposit,status) VALUES(?,?,?,?,?,?,?,?)")) {
            bind(ps,e); ps.executeUpdate();
        }
    }
    public void update(Equipment e) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(
               "UPDATE equipment SET category_id=?,branch_id=?,brand=?,model=?,purchase_year=?,daily_base_price=?,security_deposit=?,status=? WHERE equipment_id=?")) {
            bind(ps,e); ps.setInt(9,e.getEquipmentId()); ps.executeUpdate();
        }
    }
    public void delete(int id) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement("DELETE FROM equipment WHERE equipment_id=?")) {
            ps.setInt(1,id); ps.executeUpdate();
        }
    }
    public void updateStatus(Connection c, int id, EquipmentStatus s) throws SQLException {
        try (PreparedStatement ps=c.prepareStatement("UPDATE equipment SET status=? WHERE equipment_id=?")) {
            ps.setString(1,s.name()); ps.setInt(2,id); ps.executeUpdate();
        }
    }
    private void bind(PreparedStatement ps, Equipment e) throws SQLException {
        ps.setInt(1,e.getCategoryId()); ps.setInt(2,e.getBranchId());
        ps.setString(3,e.getBrand()); ps.setString(4,e.getModel());
        ps.setInt(5,e.getPurchaseYear());
        ps.setBigDecimal(6,e.getDailyBasePrice()); ps.setBigDecimal(7,e.getSecurityDeposit());
        ps.setString(8,e.getStatus().name());
    }
    private Equipment map(ResultSet rs) throws SQLException {
        Equipment e=new Equipment();
        e.setEquipmentId(rs.getInt("equipment_id"));
        e.setCategoryId(rs.getInt("category_id")); e.setBranchId(rs.getInt("branch_id"));
        e.setBrand(rs.getString("brand")); e.setModel(rs.getString("model"));
        e.setPurchaseYear(rs.getInt("purchase_year"));
        e.setDailyBasePrice(rs.getBigDecimal("daily_base_price"));
        e.setSecurityDeposit(rs.getBigDecimal("security_deposit"));
        e.setStatus(EquipmentStatus.valueOf(rs.getString("status")));
        e.setCategoryName(rs.getString("cat_name"));
        e.setBranchName(rs.getString("branch_name"));
        return e;
    }
}
