package com.gearrent.dao;
import com.gearrent.entity.*; import com.gearrent.util.DBConnection;
import java.math.BigDecimal; import java.sql.*; import java.time.LocalDate; import java.util.*;
public class RentalDAO {
    private static final String SELECT =
        "SELECT r.*, c.name AS cust_name, CONCAT(e.brand,' ',e.model) AS eq_name, b.name AS branch_name " +
        "FROM rental r JOIN customer c ON r.customer_id=c.customer_id " +
        "JOIN equipment e ON r.equipment_id=e.equipment_id " +
        "JOIN branch b ON r.branch_id=b.branch_id ";

    public List<Rental> findAll(Integer branchId, RentalStatus status) throws SQLException {
        StringBuilder sql=new StringBuilder(SELECT+"WHERE 1=1 ");
        List<Object> params=new ArrayList<>();
        if (branchId!=null){ sql.append("AND r.branch_id=? "); params.add(branchId);}
        if (status!=null){ sql.append("AND r.rental_status=? "); params.add(status.name());}
        sql.append("ORDER BY r.rental_id DESC");
        List<Rental> list=new ArrayList<>();
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(sql.toString())) {
            for (int i=0;i<params.size();i++) ps.setObject(i+1,params.get(i));
            try (ResultSet rs=ps.executeQuery()){ while (rs.next()) list.add(map(rs));}
        }
        return list;
    }
    public List<Rental> findOverdue(Integer branchId) throws SQLException {
        StringBuilder sql=new StringBuilder(SELECT+
          "WHERE r.rental_status IN ('ACTIVE','OVERDUE') AND r.end_date < CURDATE() ");
        if (branchId!=null) sql.append("AND r.branch_id=? ");
        sql.append("ORDER BY r.end_date");
        List<Rental> list=new ArrayList<>();
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(sql.toString())) {
            if (branchId!=null) ps.setInt(1,branchId);
            try (ResultSet rs=ps.executeQuery()){ while (rs.next()) list.add(map(rs));}
        }
        return list;
    }
    public Rental findById(int id) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement(SELECT+"WHERE r.rental_id=?")) {
            ps.setInt(1,id);
            try (ResultSet rs=ps.executeQuery()){ if (rs.next()) return map(rs);}
        }
        return null;
    }
    public boolean hasOverlap(Connection c, int equipmentId, LocalDate s, LocalDate e, Integer excludeRentalId) throws SQLException {
        String sql="SELECT 1 FROM rental WHERE equipment_id=? AND rental_status IN ('ACTIVE','OVERDUE') " +
                   "AND NOT (end_date < ? OR start_date > ?) " + (excludeRentalId!=null ? "AND rental_id<>? " : "");
        try (PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setInt(1,equipmentId); ps.setDate(2,Date.valueOf(s)); ps.setDate(3,Date.valueOf(e));
            if (excludeRentalId!=null) ps.setInt(4,excludeRentalId);
            try (ResultSet rs=ps.executeQuery()){ return rs.next();}
        }
    }
    public int insert(Connection c, Rental r) throws SQLException {
        String sql="INSERT INTO rental(equipment_id,customer_id,branch_id,start_date,end_date,rental_amount,security_deposit,membership_disc,long_rental_disc,final_payable,payment_status,rental_status) VALUES(?,?,?,?,?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps=c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1,r.getEquipmentId()); ps.setInt(2,r.getCustomerId()); ps.setInt(3,r.getBranchId());
            ps.setDate(4,Date.valueOf(r.getStartDate())); ps.setDate(5,Date.valueOf(r.getEndDate()));
            ps.setBigDecimal(6,r.getRentalAmount()); ps.setBigDecimal(7,r.getSecurityDeposit());
            ps.setBigDecimal(8,r.getMembershipDisc()); ps.setBigDecimal(9,r.getLongRentalDisc());
            ps.setBigDecimal(10,r.getFinalPayable());
            ps.setString(11,r.getPaymentStatus().name()); ps.setString(12,r.getRentalStatus().name());
            ps.executeUpdate();
            try (ResultSet k=ps.getGeneratedKeys()){ k.next(); return k.getInt(1);}
        }
    }
    public void completeReturn(Connection c, int rentalId, LocalDate actual, BigDecimal lateFee, BigDecimal damage, String notes, BigDecimal finalPayable) throws SQLException {
        String sql="UPDATE rental SET actual_return_date=?, late_fee=?, damage_charge=?, damage_notes=?, final_payable=?, rental_status='RETURNED' WHERE rental_id=?";
        try (PreparedStatement ps=c.prepareStatement(sql)) {
            ps.setDate(1,Date.valueOf(actual)); ps.setBigDecimal(2,lateFee);
            ps.setBigDecimal(3,damage); ps.setString(4,notes);
            ps.setBigDecimal(5,finalPayable); ps.setInt(6,rentalId);
            ps.executeUpdate();
        }
    }
    public void markOverdueRentals() throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             Statement s=c.createStatement()) {
            s.executeUpdate("UPDATE rental SET rental_status='OVERDUE' WHERE rental_status='ACTIVE' AND end_date < CURDATE()");
        }
    }
    private Rental map(ResultSet rs) throws SQLException {
        Rental r=new Rental();
        r.setRentalId(rs.getInt("rental_id")); r.setEquipmentId(rs.getInt("equipment_id"));
        r.setCustomerId(rs.getInt("customer_id")); r.setBranchId(rs.getInt("branch_id"));
        r.setStartDate(rs.getDate("start_date").toLocalDate()); r.setEndDate(rs.getDate("end_date").toLocalDate());
        Date a=rs.getDate("actual_return_date"); if (a!=null) r.setActualReturnDate(a.toLocalDate());
        r.setRentalAmount(rs.getBigDecimal("rental_amount"));
        r.setSecurityDeposit(rs.getBigDecimal("security_deposit"));
        r.setMembershipDisc(rs.getBigDecimal("membership_disc"));
        r.setLongRentalDisc(rs.getBigDecimal("long_rental_disc"));
        r.setLateFee(rs.getBigDecimal("late_fee")); r.setDamageCharge(rs.getBigDecimal("damage_charge"));
        r.setDamageNotes(rs.getString("damage_notes")); r.setFinalPayable(rs.getBigDecimal("final_payable"));
        r.setPaymentStatus(PaymentStatus.valueOf(rs.getString("payment_status")));
        r.setRentalStatus(RentalStatus.valueOf(rs.getString("rental_status")));
        r.setCustomerName(rs.getString("cust_name")); r.setEquipmentName(rs.getString("eq_name"));
        r.setBranchName(rs.getString("branch_name"));
        return r;
    }
}
