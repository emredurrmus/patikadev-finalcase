package com.edurmus.librarymanagement.model.entity;

import com.edurmus.librarymanagement.model.enums.BorrowingStatus;
import com.edurmus.librarymanagement.util.FineCalculator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

    private LocalDateTime borrowingDate;

    private LocalDateTime dueDate;

    private LocalDateTime returnDate;

    private BigDecimal fine;

    public boolean isOverdue() {
        return returnDate != null && returnDate.isAfter(dueDate);
    }

    public BigDecimal calculateOverdueFine() {
        if (isOverdue()) {
            return FineCalculator.calculateOverdueFine(dueDate);
        }
        return BigDecimal.ZERO;
    }
}
