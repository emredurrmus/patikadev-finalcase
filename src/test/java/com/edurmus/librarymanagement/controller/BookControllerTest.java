package com.edurmus.librarymanagement.controller;

import com.edurmus.librarymanagement.model.dto.request.BookRequest;
import com.edurmus.librarymanagement.model.dto.response.BookResponse;
import com.edurmus.librarymanagement.model.enums.BookGenre;
import com.edurmus.librarymanagement.service.BookService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    private static final Long BOOK_ID = 1L;
    private static final String BOOK_TITLE = "Clean Code";
    private static final String AUTHOR = "Robert C. Martin";
    private static final String ISBN = "0132350882";
    private static final LocalDate PUBLISH_DATE = LocalDate.of(2008, 8, 1);
    private static final Double PRICE = 45.0;
    private static final String DESCRIPTION = "A Handbook of Agile Software Craftsmanship";

    private BookRequest createBookRequest(String title, String author, String isbn, LocalDate publishDate, Double price, String genre, String description) {
        return new BookRequest(title, author, isbn, publishDate, price, genre, description);
    }

    private BookResponse createBookResponse(Long id, String title, String author, String isbn, LocalDate publishDate, Double price, BookGenre genre, String description, boolean isAvailable) {
        return new BookResponse(id, title, author, isbn, publishDate, price, genre, description, isAvailable);
    }

    @Test
    void shouldCreateBook() {
        BookRequest request = createBookRequest(BOOK_TITLE, AUTHOR, ISBN, PUBLISH_DATE, PRICE, "EDUCATION", DESCRIPTION);
        BookResponse response = createBookResponse(BOOK_ID, BOOK_TITLE, AUTHOR, ISBN, PUBLISH_DATE, PRICE, BookGenre.EDUCATION, DESCRIPTION, true);

        when(bookService.save(request)).thenReturn(response);

        ResponseEntity<BookResponse> result = bookController.createBook(request);

        assertEquals(HttpStatus.CREATED, result.getStatusCode());
        assertEquals(BOOK_TITLE, result.getBody().title());
        assertEquals(BookGenre.EDUCATION, result.getBody().genre());
    }

    @Test
    void shouldUpdateBook() {
        BookRequest request = createBookRequest("Clean Architecture", "Robert C. Martin", "9780134494166", LocalDate.of(2017, 9, 20), 55.0, "EDUCATION", "A guide to software structure and design");
        BookResponse response = createBookResponse(BOOK_ID, "Clean Architecture", "Robert C. Martin", "9780134494166", LocalDate.of(2017, 9, 20), 55.0, BookGenre.EDUCATION, "A guide to software structure and design", true);

        when(bookService.update(BOOK_ID, request)).thenReturn(response);

        ResponseEntity<BookResponse> result = bookController.updateBook(BOOK_ID, request);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Clean Architecture", result.getBody().title());
    }

    @Test
    void shouldGetBookById() {
        BookResponse response = createBookResponse(BOOK_ID, "Refactoring", "Martin Fowler", "0201485672", LocalDate.of(1999, 7, 8), 60.0, BookGenre.EDUCATION, "Improving the design of existing code", true);

        when(bookService.getById(BOOK_ID)).thenReturn(response);

        ResponseEntity<BookResponse> result = bookController.getBookById(BOOK_ID);

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertEquals("Refactoring", result.getBody().title());
        assertEquals(BOOK_ID, result.getBody().id());
    }

    @Test
    void shouldDeleteBook() {
        doNothing().when(bookService).deleteById(BOOK_ID);

        ResponseEntity<Void> result = bookController.deleteBook(BOOK_ID);

        assertEquals(HttpStatus.NO_CONTENT, result.getStatusCode());
        verify(bookService).deleteById(BOOK_ID);
    }

    @Test
    void shouldGetAllBooks() {
        BookResponse book = createBookResponse(BOOK_ID, "Effective Java", "Joshua Bloch", "9780134685991", LocalDate.of(2018, 1, 6), 50.0, BookGenre.EDUCATION, "Best practices for Java programming", true);

        List<BookResponse> books = Collections.singletonList(book);

        when(bookService.getAll()).thenReturn(books);

        ResponseEntity<List<BookResponse>> result = bookController.getAllBooks();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertFalse(result.getBody().isEmpty());
    }

    @Test
    void shouldGetAvailableBooks() {
        BookResponse book = createBookResponse(BOOK_ID + 1, "Design Patterns", "GoF", "9780201633610", LocalDate.of(1994, 10, 31), 65.0, BookGenre.EDUCATION, "Elements of Reusable Object-Oriented Software", true);

        List<BookResponse> books = Collections.singletonList(book);

        when(bookService.findByIsAvailable()).thenReturn(books);

        ResponseEntity<List<BookResponse>> result = bookController.getAvailableBooks();

        assertEquals(HttpStatus.OK, result.getStatusCode());
        assertFalse(result.getBody().isEmpty());
    }

    @Test
    void shouldSearchBooksAndReturnEmptyPage() {
        String title = "NonExistentTitle";
        String author = null;
        String isbn = null;
        BookGenre genre = null;
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);
        Page<BookResponse> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);

        when(bookService.searchBooks(any(), eq(page), eq(size))).thenReturn(emptyPage);

        ResponseEntity<Page<BookResponse>> response = bookController.searchBooks(title, author, isbn, genre, page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().getContent().isEmpty());

        verify(bookService).searchBooks(any(), eq(page), eq(size));
    }

    @Test
    void shouldSearchBooksAndReturnNonEmptyPage() {
        String title = "Clean Code";
        String author = "Robert C. Martin";
        String isbn = "0132350882";
        BookGenre genre = BookGenre.EDUCATION;
        int page = 0;
        int size = 10;
        Pageable pageable = PageRequest.of(page, size);

        BookResponse book = createBookResponse(BOOK_ID, title, author, isbn, PUBLISH_DATE, PRICE, genre, DESCRIPTION, true);
        Page<BookResponse> bookPage = new PageImpl<>(List.of(book), pageable, 1);

        when(bookService.searchBooks(any(), eq(page), eq(size))).thenReturn(bookPage);

        ResponseEntity<Page<BookResponse>> response = bookController.searchBooks(title, author, isbn, genre, page, size);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertFalse(response.getBody().getContent().isEmpty());
        assertEquals(1, response.getBody().getTotalElements());
        assertEquals("Clean Code", response.getBody().getContent().get(0).title());

        verify(bookService).searchBooks(any(), eq(page), eq(size));
    }
}
