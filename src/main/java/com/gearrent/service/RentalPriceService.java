package com.gearrent.service;
import com.gearrent.dao.MembershipConfigDAO;
import com.gearrent.entity.*; import com.gearrent.util.DateUtil;
import java.math.BigDecimal; import java.math.RoundingMode; import java.time.LocalDate;

public class RentalPriceService {
    private static final BigDecimal LONG_RENTAL_DISC_PCT = new BigDecimal("10"); // 10% if >=7 days
    private static final int LONG_RENTAL_THRESHOLD = 7;
    private final MembershipConfigDAO membershipDao = new MembershipConfigDAO();

    public static class Quote {
        public BigDecimal rentalAmount, membershipDisc, longRentalDisc, finalPayable, securityDeposit;
        public long days;
    }

    public Quote calculate(Equipment eq, Category cat, Customer cust, LocalDate start, LocalDate end) throws Exception {
        long days = DateUtil.daysBetweenInclusive(start, end);
        long weekend = DateUtil.weekendDays(start, end);
        long weekday = days - weekend;
        BigDecimal base = eq.getDailyBasePrice().multiply(cat.getBasePriceFactor());
        BigDecimal weekendPrice = base.multiply(cat.getWeekendMultiplier());
        BigDecimal rental = base.multiply(BigDecimal.valueOf(weekday))
                              .add(weekendPrice.multiply(BigDecimal.valueOf(weekend)))
                              .setScale(2, RoundingMode.HALF_UP);

        BigDecimal longDisc = BigDecimal.ZERO;
        if (days >= LONG_RENTAL_THRESHOLD)
            longDisc = rental.multiply(LONG_RENTAL_DISC_PCT).divide(new BigDecimal("100"),2,RoundingMode.HALF_UP);

        BigDecimal memPct = membershipDao.getDiscountPct(cust.getMembership());
        BigDecimal afterLong = rental.subtract(longDisc);
        BigDecimal memDisc = afterLong.multiply(memPct).divide(new BigDecimal("100"),2,RoundingMode.HALF_UP);

        Quote q = new Quote();
        q.days = days;
        q.rentalAmount = rental;
        q.longRentalDisc = longDisc;
        q.membershipDisc = memDisc;
        q.finalPayable = afterLong.subtract(memDisc).setScale(2,RoundingMode.HALF_UP);
        q.securityDeposit = eq.getSecurityDeposit();
        return q;
    }
}
