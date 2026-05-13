package com.gearrent.dao;
import com.gearrent.entity.*; import com.gearrent.util.DBConnection;
import java.sql.*; import java.time.LocalDate; import java.util.*;
public class ReservationDAO {
    private static final String SELECT =
        "SELECT r.*, c.name AS cust_name, CONCAT(e.brand,' ',e.model) AS eq_name " +
        "FROM reservation r JOIN customer c ON r.customer_id=c.customer_id " +
        "JOIN equipment e ON r.equipment_id=e.equipment_id ";

    public List<Reservation> findAll(Integer branchId) throws SQLException {
        StringBuilder sql=new StringBuilder(SELECT+"WHERE 1=1 ");
        if (branchId!=null) sql.append("AND r.branch_id=? ");
        sql.append("ORDER BY r.reservation_id DESC");
        List<Reservation> list=new ArrayList<>();
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(sql.toString())) {
            if (branchId!=null) ps.setInt(1,branchId);
            try (ResultSet rs=ps.executeQuery()){ while (rs.next()) list.add(map(rs));}
        }
        return list;
    }
    public Reservation findById(int id) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(SELECT+"WHERE r.reservation_id=?")) {
            ps.setInt(1,id);
            try (ResultSet rs=ps.executeQuery()){ if (rs.next()) return map(rs);}
        }
        return null;
    }
    public boolean hasOverlap(Connection c, int equipmentId, LocalDate s, LocalDate e) throws SQLException {
        String sql="SELECT 1 FROM reservation WHERE equipment_id=? AND status='ACTIVE' " +
                   "AND NOT (end_date < ? OR start_date > ?)";
        try (PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setInt(1,equipmentId); ps.setDate(2,Date.valueOf(s)); ps.setDate(3,Date.valueOf(e));
            try (ResultSet rs=ps.executeQuery()){ return rs.next();}
        }
    }
    public int insert(Connection c, Reservation r) throws SQLException {
        String sql="INSERT INTO reservation(equipment_id,customer_id,branch_id,start_date,end_date,status) VALUES(?,?,?,?,?,'ACTIVE')";
        try (PreparedStatement ps=c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,r.getEquipmentId()); ps.setInt(2,r.getCustomerId()); ps.setInt(3,r.getBranchId());
            ps.setDate(4,Date.valueOf(r.getStartDate())); ps.setDate(5,Date.valueOf(r.getEndDate()));
            ps.executeUpdate();
            try (ResultSet k=ps.getGeneratedKeys()){ k.next(); return k.getInt(1);}
        }
    }
    public void updateStatus(Connection c, int id, ReservationStatus s) throws SQLException {
        try (PreparedStatement ps=c.prepareStatement("UPDATE reservation SET status=? WHERE reservation_id=?")) {
            ps.setString(1,s.name()); ps.setInt(2,id); ps.executeUpdate();
        }
    }
    public void updateStatus(int id, ReservationStatus s) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection()) { updateStatus(c,id,s); }
    }
    private Reservation map(ResultSet rs) throws SQLException {
        Reservation r=new Reservation();
        r.setReservationId(rs.getInt("reservation_id"));
        r.setEquipmentId(rs.getInt("equipment_id"));
        r.setCustomerId(rs.getInt("customer_id"));
        r.setBranchId(rs.getInt("branch_id"));
        r.setStartDate(rs.getDate("start_date").toLocalDate());
        r.setEndDate(rs.getDate("end_date").toLocalDate());
        r.setStatus(ReservationStatus.valueOf(rs.getString("status")));
        r.setCustomerName(rs.getString("cust_name"));
        r.setEquipmentName(rs.getString("eq_name"));
        return r;
    }
}
