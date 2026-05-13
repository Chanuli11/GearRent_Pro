package com.gearrent.service;
import com.gearrent.util.DBConnection;
import java.math.BigDecimal; import java.sql.*; import java.time.LocalDate; import java.util.*;

public class ReportService {
    public static class BranchRevenueRow {
        public String branch; public int rentals; public BigDecimal income, lateFees, damageCharges;
    }
    public static class UtilizationRow {
        public String equipment; public long daysRented; public long daysInRange; public double pct;
    }

    public List<BranchRevenueRow> branchRevenue(LocalDate from, LocalDate to) throws SQLException {
        String sql = "SELECT b.name, COUNT(r.rental_id) AS cnt, " +
                     "COALESCE(SUM(r.final_payable),0) AS income, " +
                     "COALESCE(SUM(r.late_fee),0) AS late, " +
                     "COALESCE(SUM(r.damage_charge),0) AS damage " +
                     "FROM branch b LEFT JOIN rental r ON r.branch_id=b.branch_id " +
                     "AND r.start_date BETWEEN ? AND ? " +
                     "GROUP BY b.branch_id, b.name ORDER BY b.name";
        List<BranchRevenueRow> list = new ArrayList<>();
        try (Connection c = DBConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(from)); ps.setDate(2, Date.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BranchRevenueRow row = new BranchRevenueRow();
                    row.branch = rs.getString(1); row.rentals = rs.getInt(2);
                    row.income = rs.getBigDecimal(3); row.lateFees = rs.getBigDecimal(4);
                    row.damageCharges = rs.getBigDecimal(5);
                    list.add(row);
                }
            }
        }
        return list;
    }

    public List<UtilizationRow> equipmentUtilization(int branchId, LocalDate from, LocalDate to) throws SQLException {
        long rangeDays = java.time.temporal.ChronoUnit.DAYS.between(from, to) + 1;
        String sql = "SELECT e.equipment_id, CONCAT(e.brand,' ',e.model) AS name, " +
                     "COALESCE(SUM(DATEDIFF(LEAST(r.end_date,?), GREATEST(r.start_date,?)) + 1),0) AS days_rented " +
                     "FROM equipment e LEFT JOIN rental r ON r.equipment_id=e.equipment_id " +
                     "AND r.start_date <= ? AND r.end_date >= ? " +
                     "WHERE e.branch_id=? GROUP BY e.equipment_id, name ORDER BY name";
        List<UtilizationRow> list = new ArrayList<>();
        try (Connection c = DBConnection.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(to)); ps.setDate(2, Date.valueOf(from));
            ps.setDate(3, Date.valueOf(to)); ps.setDate(4, Date.valueOf(from));
            ps.setInt(5, branchId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    UtilizationRow row = new UtilizationRow();
                    row.equipment = rs.getString("name");
                    row.daysRented = Math.max(0, rs.getLong("days_rented"));
                    row.daysInRange = rangeDays;
                    row.pct = rangeDays==0 ? 0 : (row.daysRented * 100.0 / rangeDays);
                    list.add(row);
                }
            }
        }
        return list;
    }
}
