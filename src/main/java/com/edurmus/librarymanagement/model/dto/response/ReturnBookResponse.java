package com.edurmus.librarymanagement.model.dto.response;

import java.math.BigDecimal;

public record ReturnBookResponse(
        BorrowingDTO borrowingDTO,
        boolean isOverdue,
        BigDecimal fine
) {}
