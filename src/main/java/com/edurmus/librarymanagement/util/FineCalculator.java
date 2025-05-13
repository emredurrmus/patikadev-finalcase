package com.edurmus.librarymanagement.util;

import com.edurmus.librarymanagement.model.entity.Borrowing;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

public class FineCalculator {

    private static final String FINE_PER_DAY = "2.00";

    public static BigDecimal calculateOverdueFine(LocalDateTime dueDateTime) {
        long daysOverdue = ChronoUnit.DAYS.between(dueDateTime, LocalDateTime.now());
        BigDecimal dailyFine = new BigDecimal(FINE_PER_DAY);
        return dailyFine.multiply(new BigDecimal(daysOverdue));
    }


    public static BigDecimal calculateTotalFine(List<Borrowing> borrowings) {
        return borrowings.stream()
                .map(Borrowing::getFine)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

}
