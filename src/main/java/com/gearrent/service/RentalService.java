package com.gearrent.service;
import com.gearrent.dao.*; import com.gearrent.entity.*;
import com.gearrent.util.*;
import java.math.BigDecimal; import java.sql.Connection; import java.time.LocalDate;

public class RentalService {
    public static final BigDecimal DEPOSIT_LIMIT = new BigDecimal("500000");
    public static final int MAX_DURATION_DAYS = 30;

    private final RentalDAO rentalDao = new RentalDAO();
    private final EquipmentDAO equipmentDao = new EquipmentDAO();
    private final CategoryDAO categoryDao = new CategoryDAO();
    private final CustomerDAO customerDao = new CustomerDAO();
    private final ReservationDAO reservationDao = new ReservationDAO();
    private final RentalPriceService priceService = new RentalPriceService();

    public Rental createRental(int equipmentId, int customerId, int branchId,
                               LocalDate start, LocalDate end) throws Exception {
        ValidationUtil.validDateRange(start, end, MAX_DURATION_DAYS);

        Equipment eq = equipmentDao.findById(equipmentId);
        if (eq==null) throw new IllegalArgumentException("Equipment not found");
        if (eq.getStatus()==EquipmentStatus.UNDER_MAINTENANCE)
            throw new IllegalArgumentException("Equipment under maintenance");

        Customer cust = customerDao.findById(customerId);
        if (cust==null) throw new IllegalArgumentException("Customer not found");

        Category cat = categoryDao.findById(eq.getCategoryId());
        RentalPriceService.Quote q = priceService.calculate(eq, cat, cust, start, end);

        BigDecimal currentDeposit = customerDao.totalActiveDeposits(customerId);
        if (currentDeposit.add(q.securityDeposit).compareTo(DEPOSIT_LIMIT) > 0)
            throw new IllegalArgumentException("Customer deposit limit exceeded (LKR "+DEPOSIT_LIMIT+")");

        try (Connection c = DBConnection.getInstance().getConnection()) {
            c.setAutoCommit(false);
            try {
                if (rentalDao.hasOverlap(c, equipmentId, start, end, null))
                    throw new IllegalArgumentException("Equipment already rented during this period");

                Rental r = new Rental();
                r.setEquipmentId(equipmentId); r.setCustomerId(customerId); r.setBranchId(branchId);
                r.setStartDate(start); r.setEndDate(end);
                r.setRentalAmount(q.rentalAmount); r.setSecurityDeposit(q.securityDeposit);
                r.setMembershipDisc(q.membershipDisc); r.setLongRentalDisc(q.longRentalDisc);
                r.setFinalPayable(q.finalPayable);
                r.setPaymentStatus(PaymentStatus.PAID);
                r.setRentalStatus(RentalStatus.ACTIVE);
                int id = rentalDao.insert(c, r);
                r.setRentalId(id);
                equipmentDao.updateStatus(c, equipmentId, EquipmentStatus.RENTED);
                c.commit();
                return r;
            } catch (Exception ex) { c.rollback(); throw ex; }
        }
    }

    public Rental convertReservationToRental(int reservationId) throws Exception {
        Reservation res = reservationDao.findById(reservationId);
        if (res==null) throw new IllegalArgumentException("Reservation not found");
        if (res.getStatus()!=ReservationStatus.ACTIVE) throw new IllegalArgumentException("Reservation not active");

        Rental r = createRental(res.getEquipmentId(), res.getCustomerId(), res.getBranchId(),
                                res.getStartDate(), res.getEndDate());
        reservationDao.updateStatus(reservationId, ReservationStatus.CONVERTED);
        return r;
    }

    public RentalPriceService.Quote quote(int equipmentId, int customerId, LocalDate s, LocalDate e) throws Exception {
        ValidationUtil.validDateRange(s,e,MAX_DURATION_DAYS);
        Equipment eq = equipmentDao.findById(equipmentId);
        Customer cust = customerDao.findById(customerId);
        Category cat = categoryDao.findById(eq.getCategoryId());
        return priceService.calculate(eq, cat, cust, s, e);
    }
}
