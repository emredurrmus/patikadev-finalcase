package com.edurmus.librarymanagement.controller;

import com.edurmus.librarymanagement.model.dto.request.BookRequest;
import com.edurmus.librarymanagement.model.dto.request.BookSearchRequest;
import com.edurmus.librarymanagement.model.dto.response.BookResponse;
import com.edurmus.librarymanagement.model.enums.BookGenre;
import com.edurmus.librarymanagement.service.BookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Book Management", description = "Operations related to book management")
public class BookController {

    private final BookService bookService;

    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Create a new book", description = "Allows librarian user to create a new book in the library")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Book successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest bookRequest) {
        log.info("Creating new book: {}", bookRequest.title());
        BookResponse createdBook = bookService.save(bookRequest);
        return ResponseEntity.status(201).body(createdBook);
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Update an existing book by ID", description = "Allows librarian user to update an existing book in the library")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book successfully updated"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest bookRequest) {
        log.info("Updating book with id: {}", id);
        BookResponse updatedBook = bookService.update(id, bookRequest);
        return ResponseEntity.ok(updatedBook);
    }

    @PreAuthorize("hasRole('LIBRARIAN') or hasRole('PATRON')")
    @GetMapping("/search")
    public ResponseEntity<Page<BookResponse>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String isbn,
            @RequestParam(required = false) BookGenre genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        BookSearchRequest request = new BookSearchRequest(title, author, isbn, genre);
        Page<BookResponse> booksResponse = bookService.searchBooks(request, page, size);
        return ResponseEntity.ok(booksResponse);
    }


    @Operation(summary = "Get a book by ID", description = "Retrieves a book by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Book found"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        log.info("Fetching book with id: {}", id);
        BookResponse book = bookService.getById(id);
        return ResponseEntity.ok(book);
    }

    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Delete (soft delete) a book by ID", description = "Allows librarian user to delete a book from the library")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Book successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Book not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        log.info("Deleting book with id: {}", id);
        bookService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get all active books", description = "Retrieves a list of all active books")
    @ApiResponse(responseCode = "200", description = "List of all active books")
    @GetMapping
    public ResponseEntity<List<BookResponse>> getAllBooks() {
        log.info("Fetching all active books");
        return ResponseEntity.ok(bookService.getAll());
    }

    @Operation(summary = "Get all available (and active) books", description = "Retrieves a list of all available books")
    @ApiResponse(responseCode = "200", description = "List of available books")
    @GetMapping("/available")
    public ResponseEntity<List<BookResponse>> getAvailableBooks() {
        log.info("Fetching available books");
        return ResponseEntity.ok(bookService.findByIsAvailable());
    }
}
