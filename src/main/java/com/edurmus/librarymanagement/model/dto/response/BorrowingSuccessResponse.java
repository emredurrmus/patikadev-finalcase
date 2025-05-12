package com.edurmus.librarymanagement.model.dto.response;

import com.edurmus.librarymanagement.model.enums.BorrowingStatus;

import java.time.LocalDateTime;

public record BorrowingSuccessResponse(String username,
                                       String bookTitle,
                                       LocalDateTime dueDate,
                                       BorrowingStatus status)
{}
