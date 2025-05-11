package com.edurmus.librarymanagement.controller;

import com.edurmus.librarymanagement.model.dto.response.BorrowingDTO;
import com.edurmus.librarymanagement.model.dto.response.BorrowingSuccessResponse;
import com.edurmus.librarymanagement.model.dto.response.ReturnBookResponse;
import com.edurmus.librarymanagement.service.BorrowingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/borrowings")
@Tag(name = "Borrowing Management", description = "Endpoints for borrowing and returning books")
public class BorrowingController {

    private final BorrowingService borrowingService;

    public BorrowingController(BorrowingService borrowingService) {
        this.borrowingService = borrowingService;
    }

    @PreAuthorize("hasRole('PATRON')")
    @PostMapping("/borrow/{bookId}")
    @Operation(summary = "Borrow a book", description = "Allows a user to borrow a book from the library")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book borrowed successfully"),
            @ApiResponse(responseCode = "404", description = "Error occurred while borrowing book")
    })
    public ResponseEntity<BorrowingSuccessResponse> borrowBook(@PathVariable Long bookId) {
        BorrowingSuccessResponse borrowing = borrowingService.borrowBook(bookId);
        return ResponseEntity.status(HttpStatus.CREATED).body(borrowing);
    }

    @PreAuthorize("hasRole('PATRON')")
    @PostMapping("/return/{borrowingId}")
    @Operation(summary = "Return a borrowed book", description = "Allows a user to return a borrowed book")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book returned successfully"),
            @ApiResponse(responseCode = "404", description = "Error occurred while returning book")
    })
    public ResponseEntity<ReturnBookResponse> returnBook(@PathVariable Long borrowingId) {
        ReturnBookResponse returnBookResponse = borrowingService.returnBook(borrowingId);
        return ResponseEntity.ok(returnBookResponse);
    }

    @PreAuthorize("hasRole('PATRON') or hasRole('LIBRARIAN')")
    @GetMapping("/history")
    @Operation(summary = "Get user's borrowing history", description = "Retrieves the borrowing history of the logged-in user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User's borrowing history fetched successfully"),
            @ApiResponse(responseCode = "404", description = "No borrowing history found")
    })
    public ResponseEntity<List<BorrowingDTO>> getUserBorrowingHistory() {
        List<BorrowingDTO> borrowings = borrowingService.getUserBorrowingHistory();
        return ResponseEntity.ok(borrowings);
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @GetMapping("/all-history")
    @Operation(summary = "Get all borrowing history", description = "Retrieves the borrowing history for all users (admin access only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "All users' borrowing history fetched successfully"),
            @ApiResponse(responseCode = "404", description = "No borrowing history found")
    })
    public ResponseEntity<List<BorrowingDTO>> getAllBorrowingHistory() {
        List<BorrowingDTO> borrowings = borrowingService.getAllBorrowingHistory();
        return ResponseEntity.ok(borrowings);
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @GetMapping("/overdue/report")
    @Operation(summary = "Generate overdue report", description = "Generates a report of overdue books (librarian access only)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Overdue report generated successfully"),
            @ApiResponse(responseCode = "404", description = "No overdue books found in the library")
    })
    public ResponseEntity<String> generateOverdueReport() {
        return ResponseEntity.ok(borrowingService.generateOverdueReport());
    }

}

