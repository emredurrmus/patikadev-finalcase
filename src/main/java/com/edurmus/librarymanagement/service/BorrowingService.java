package com.edurmus.librarymanagement.service;

import com.edurmus.librarymanagement.model.dto.response.BorrowingDTO;
import com.edurmus.librarymanagement.model.dto.response.BorrowingSuccessResponse;
import com.edurmus.librarymanagement.model.dto.response.ReturnBookResponse;

import java.util.List;

public interface BorrowingService {

    BorrowingSuccessResponse borrowBook(Long bookId);

    ReturnBookResponse returnBook(Long borrowingId);

    List<BorrowingDTO> getUserBorrowingHistory();

    List<BorrowingDTO> getAllBorrowingHistory();

    String generateOverdueReport();


}
