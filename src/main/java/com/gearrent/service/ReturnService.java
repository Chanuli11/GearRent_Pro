package com.gearrent.service;
import com.gearrent.dao.*; import com.gearrent.entity.*; import com.gearrent.util.DBConnection;
import java.math.BigDecimal; import java.math.RoundingMode; import java.sql.Connection; import java.time.LocalDate; import java.time.temporal.ChronoUnit;

public class ReturnService {
    private final RentalDAO rentalDao = new RentalDAO();
    private final EquipmentDAO equipmentDao = new EquipmentDAO();
    private final CategoryDAO categoryDao = new CategoryDAO();

    public static class Settlement {
        public BigDecimal lateFee, damageCharge, totalCharges, refundToCustomer, extraPayable, finalPayable;
        public long lateDays;
    }

    public Settlement processReturn(int rentalId, LocalDate actualReturn,
                                    BigDecimal damageCharge, String damageNotes,
                                    boolean equipmentDamaged) throws Exception {
        if (damageCharge==null) damageCharge = BigDecimal.ZERO;
        Rental r = rentalDao.findById(rentalId);
        if (r==null) throw new IllegalArgumentException("Rental not found");
        if (r.getRentalStatus()==RentalStatus.RETURNED) throw new IllegalArgumentException("Already returned");

        Equipment eq = equipmentDao.findById(r.getEquipmentId());
        Category cat = categoryDao.findById(eq.getCategoryId());

        long lateDays = 0;
        BigDecimal lateFee = BigDecimal.ZERO;
        if (actualReturn.isAfter(r.getEndDate())) {
            lateDays = ChronoUnit.DAYS.between(r.getEndDate(), actualReturn);
            lateFee = cat.getLateFeePerDay().multiply(BigDecimal.valueOf(lateDays))
                         .setScale(2,RoundingMode.HALF_UP);
        }
        BigDecimal totalCharges = lateFee.add(damageCharge);
        BigDecimal deposit = r.getSecurityDeposit();
        BigDecimal refund = BigDecimal.ZERO, extra = BigDecimal.ZERO;
        if (deposit.compareTo(totalCharges) >= 0) refund = deposit.subtract(totalCharges);
        else extra = totalCharges.subtract(deposit);

        BigDecimal newFinal = r.getFinalPayable().add(totalCharges).subtract(deposit);

        try (Connection c = DBConnection.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try {
                rentalDao.completeReturn(c, rentalId, actualReturn, lateFee, damageCharge, damageNotes, newFinal);
                equipmentDao.updateStatus(c, r.getEquipmentId(),
                    equipmentDamaged ? EquipmentStatus.UNDER_MAINTENANCE : EquipmentStatus.AVAILABLE);
                c.commit();
            } catch (Exception ex){ c.rollback(); throw ex; }
        }

        Settlement s = new Settlement();
        s.lateDays=lateDays; s.lateFee=lateFee; s.damageCharge=damageCharge;
        s.totalCharges=totalCharges; s.refundToCustomer=refund; s.extraPayable=extra; s.finalPayable=newFinal;
        return s;
    }
}
