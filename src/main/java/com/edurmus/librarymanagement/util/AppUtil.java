package com.edurmus.librarymanagement.util;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class AppUtil {

    private static final String FINE_PER_DAY = "2.00";

    public static BigDecimal calculateOverdueFine(LocalDate dueDate) {
        long daysOverdue = ChronoUnit.DAYS.between(dueDate, LocalDate.now());
        BigDecimal dailyFine = new BigDecimal(FINE_PER_DAY);
        return dailyFine.multiply(new BigDecimal(daysOverdue));
    }


}
