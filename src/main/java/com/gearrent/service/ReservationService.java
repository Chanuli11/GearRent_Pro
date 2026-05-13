package com.gearrent.service;
import com.gearrent.dao.*; import com.gearrent.entity.*;
import com.gearrent.util.*;
import java.math.BigDecimal; import java.sql.Connection; import java.time.LocalDate;

public class ReservationService {
    private final ReservationDAO dao = new ReservationDAO();
    private final RentalDAO rentalDao = new RentalDAO();
    private final EquipmentDAO equipmentDao = new EquipmentDAO();
    private final CustomerDAO customerDao = new CustomerDAO();

    public Reservation create(int equipmentId, int customerId, int branchId,
                              LocalDate start, LocalDate end) throws Exception {
        ValidationUtil.validDateRange(start,end,RentalService.MAX_DURATION_DAYS);
        Equipment eq = equipmentDao.findById(equipmentId);
        if (eq==null) throw new IllegalArgumentException("Equipment not found");

        BigDecimal currentDeposit = customerDao.totalActiveDeposits(customerId);
        if (currentDeposit.add(eq.getSecurityDeposit()).compareTo(RentalService.DEPOSIT_LIMIT) > 0)
            throw new IllegalArgumentException("Customer deposit limit would be exceeded");

        try (Connection c = DBConnection.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try {
                if (dao.hasOverlap(c, equipmentId, start, end))
                    throw new IllegalArgumentException("Reservation overlap exists");
                if (rentalDao.hasOverlap(c, equipmentId, start, end, null))
                    throw new IllegalArgumentException("Active rental overlaps this period");

                Reservation r = new Reservation();
                r.setEquipmentId(equipmentId); r.setCustomerId(customerId); r.setBranchId(branchId);
                r.setStartDate(start); r.setEndDate(end); r.setStatus(ReservationStatus.ACTIVE);
                int id = dao.insert(c, r); r.setReservationId(id);
                c.commit(); return r;
            } catch (Exception ex){ c.rollback(); throw ex; }
        }
    }

    public void cancel(int reservationId) throws Exception {
        dao.updateStatus(reservationId, ReservationStatus.CANCELLED);
    }
}
