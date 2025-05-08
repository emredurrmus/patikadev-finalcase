package com.edurmus.librarymanagement.model.entity;

import com.edurmus.librarymanagement.model.enums.BorrowingStatus;
import com.edurmus.librarymanagement.util.AppUtil;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Borrowing extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @Enumerated(EnumType.STRING)
    private BorrowingStatus status;

    private LocalDate borrowingDate;

    private LocalDate dueDate;

    private LocalDate returnDate;

    private String description;

    private BigDecimal fine;

    public boolean isOverdue() {
        return returnDate != null && dueDate != null && returnDate.isAfter(dueDate);
    }

    public BigDecimal calculateOverdueFine() {
        if (isOverdue()) {
            return AppUtil.calculateOverdueFine(dueDate);
        }
        return BigDecimal.ZERO;
    }
}
