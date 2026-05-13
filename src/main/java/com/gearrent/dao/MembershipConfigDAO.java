package com.gearrent.dao;
import com.gearrent.entity.MembershipType; import com.gearrent.util.DBConnection;
import java.math.BigDecimal; import java.sql.*; import java.util.*;
public class MembershipConfigDAO {
    public Map<MembershipType,BigDecimal> findAll() throws SQLException {
        Map<MembershipType,BigDecimal> map=new EnumMap<>(MembershipType.class);
        try (Connection c=DBConnection.getInstance().getConnection();
             Statement s=c.createStatement();
             ResultSet rs=s.executeQuery("SELECT * FROM membership_config")) {
            while (rs.next()) map.put(MembershipType.valueOf(rs.getString("membership")), rs.getBigDecimal("discount_pct"));
        }
        return map;
    }
    public BigDecimal getDiscountPct(MembershipType m) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement("SELECT discount_pct FROM membership_config WHERE membership=?")) {
            ps.setString(1,m.name());
            try (ResultSet rs=ps.executeQuery()){ if (rs.next()) return rs.getBigDecimal(1);}
        }
        return BigDecimal.ZERO;
    }
    public void update(MembershipType m, BigDecimal pct) throws SQLException {
        try (Connection c=DBConnection.getInstance().getConnection();
             PreparedStatement ps=c.prepareStatement("UPDATE membership_config SET discount_pct=? WHERE membership=?")) {
            ps.setBigDecimal(1,pct); ps.setString(2,m.name()); ps.executeUpdate();
        }
    }
}
