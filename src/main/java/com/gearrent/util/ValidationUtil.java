package com.gearrent.util;
import java.time.LocalDate;
public class ValidationUtil {
    public static void notBlank(String v, String field){
        if (v==null || v.trim().isEmpty()) throw new IllegalArgumentException(field+" is required");
    }
    public static void positive(double v, String field){
        if (v <= 0) throw new IllegalArgumentException(field+" must be positive");
    }
    public static void validDateRange(LocalDate s, LocalDate e, int maxDays){
        if (s==null || e==null) throw new IllegalArgumentException("Start and end date are required");
        if (e.isBefore(s)) throw new IllegalArgumentException("End date cannot be before start date");
        long days = DateUtil.daysBetweenInclusive(s, e);
        if (days > maxDays) throw new IllegalArgumentException("Duration exceeds max ("+maxDays+" days)");
    }
}
