package com.edurmus.librarymanagement.controller;

import com.edurmus.librarymanagement.model.dto.response.BorrowingDTO;
import com.edurmus.librarymanagement.model.dto.response.BorrowingSuccessResponse;
import com.edurmus.librarymanagement.model.dto.response.ReturnBookResponse;
import com.edurmus.librarymanagement.model.enums.BorrowingStatus;
import com.edurmus.librarymanagement.service.BorrowingService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowingControllerTest {

    private static final String USERNAME = "emre_durmus";
    private static final String BOOK_TITLE = "Clean Code";
    private static final Long BORROWING_ID = 1L;

    @Mock
    private BorrowingService borrowingService;

    @InjectMocks
    private BorrowingController borrowingController;

    @Test
    void shouldBorrowBook() {
        BorrowingSuccessResponse responseMock = new BorrowingSuccessResponse(
                USERNAME,
                BOOK_TITLE,
                LocalDate.now().plusDays(14),
                BorrowingStatus.BORROWED
        );

        when(borrowingService.borrowBook(BORROWING_ID)).thenReturn(responseMock);

        ResponseEntity<BorrowingSuccessResponse> response = borrowingController.borrowBook(BORROWING_ID);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(USERNAME, response.getBody().username());
        assertEquals(BOOK_TITLE, response.getBody().bookTitle());
    }

    @Test
    void shouldReturnBook() {
        BorrowingDTO borrowingDTO = createBorrowingDTO(BORROWING_ID, BorrowingStatus.RETURNED, LocalDate.now());
        ReturnBookResponse responseMock = new ReturnBookResponse(borrowingDTO, false, BigDecimal.ZERO);

        when(borrowingService.returnBook(BORROWING_ID)).thenReturn(responseMock);

        ResponseEntity<ReturnBookResponse> response = borrowingController.returnBook(BORROWING_ID);

        assertOkWithBody(response);
        assertEquals(BOOK_TITLE, response.getBody().borrowingDTO().getBookTitle());
        assertFalse(response.getBody().isOverdue());
    }

    @Test
    void shouldGetUserBorrowingHistory() {
        BorrowingDTO borrowingDTO = createBorrowingDTO(BORROWING_ID, BorrowingStatus.BORROWED, null);

        when(borrowingService.getUserBorrowingHistory()).thenReturn(List.of(borrowingDTO));

        ResponseEntity<List<BorrowingDTO>> response = borrowingController.getUserBorrowingHistory();

        assertOkWithBody(response);
        assertEquals(USERNAME, response.getBody().get(0).getUsername());
    }

    @Test
    void shouldGetAllBorrowingHistory() {
        BorrowingDTO borrowingDTO = createBorrowingDTO(BORROWING_ID, BorrowingStatus.BORROWED, null);

        when(borrowingService.getAllBorrowingHistory()).thenReturn(List.of(borrowingDTO));

        ResponseEntity<List<BorrowingDTO>> response = borrowingController.getAllBorrowingHistory();

        assertOkWithBody(response);
        assertEquals(BOOK_TITLE, response.getBody().get(0).getBookTitle());
    }

    @Test
    void shouldGenerateOverdueReport() {
        String overdueReport = "Overdue report generated successfully.";

        when(borrowingService.generateOverdueReport()).thenReturn(overdueReport);

        ResponseEntity<String> response = borrowingController.generateOverdueReport();

        assertOkWithBody(response);
        assertEquals(overdueReport, response.getBody());
    }

    // Helper methods
    private BorrowingDTO createBorrowingDTO(Long id, BorrowingStatus status, LocalDate returnDate) {
        return new BorrowingDTO(
                id,
                USERNAME,
                BOOK_TITLE,
                status,
                LocalDate.now().minusDays(5),
                LocalDate.now().plusDays(9),
                returnDate,
                BigDecimal.ZERO
        );
    }

    private void assertOkWithBody(ResponseEntity<?> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
