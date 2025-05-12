package com.edurmus.librarymanagement.model.dto.response;


import com.edurmus.librarymanagement.model.enums.BorrowingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BorrowingDTO {

    private Long id;
    private String username;
    private String bookTitle;
    private BorrowingStatus status;
    private LocalDateTime borrowingDate;
    private LocalDateTime dueDate;
    private LocalDateTime returnDate;
    private BigDecimal fine;
}
