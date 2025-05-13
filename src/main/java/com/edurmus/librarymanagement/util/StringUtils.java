package com.edurmus.librarymanagement.util;

import com.edurmus.librarymanagement.model.entity.User;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class StringUtils {

    public static String formatUserOverdueLine(User user, int count, BigDecimal totalFine) {
        return " - %s (%s): %d book(s) overdue | Total fine: %.2f\n".formatted(
                user.getFirstName() + " " + user.getLastName(),
                user.getEmail(),
                count,
                totalFine
        );
    }

    public static String buildReportHeader(int totalOverdue, int totalUsersWithOverdues) {
        return """
                OVERDUE BOOK REPORT
                ----------------------
                Total Overdue Books: %d
                Total Users with Overdues: %d

                """.formatted(totalOverdue, totalUsersWithOverdues);
    }

    public static String buildReportFooter(LocalDateTime timestamp) {
        return "\nGenerated at: %s".formatted(
                timestamp.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

}
