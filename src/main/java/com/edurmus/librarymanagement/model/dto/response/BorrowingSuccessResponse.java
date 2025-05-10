package com.edurmus.librarymanagement.model.dto.response;

import com.edurmus.librarymanagement.model.enums.BorrowingStatus;

import java.time.LocalDate;

public record BorrowingSuccessResponse(String username,
                                       String bookTitle,
                                       LocalDate dueDate,
                                       BorrowingStatus status)
{}
