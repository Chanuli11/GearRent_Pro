package com.gearrent.util;
import java.time.DayOfWeek; import java.time.LocalDate; import java.time.temporal.ChronoUnit;
public class DateUtil {
    public static long daysBetweenInclusive(LocalDate s, LocalDate e){
        return ChronoUnit.DAYS.between(s, e) + 1;
    }
    public static long weekendDays(LocalDate s, LocalDate e){
        long count=0; LocalDate d=s;
        while(!d.isAfter(e)){
            DayOfWeek dw=d.getDayOfWeek();
            if (dw==DayOfWeek.SATURDAY||dw==DayOfWeek.SUNDAY) count++;
            d=d.plusDays(1);
        }
        return count;
    }
}
