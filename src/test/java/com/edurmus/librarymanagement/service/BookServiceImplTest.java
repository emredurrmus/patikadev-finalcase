package com.edurmus.librarymanagement.service;

import com.edurmus.librarymanagement.exception.book.BookNotFoundException;
import com.edurmus.librarymanagement.exception.book.BookSaveException;
import com.edurmus.librarymanagement.model.dto.request.BookRequest;
import com.edurmus.librarymanagement.model.dto.request.BookSearchRequest;
import com.edurmus.librarymanagement.model.dto.response.BookResponse;
import com.edurmus.librarymanagement.model.entity.Book;
import com.edurmus.librarymanagement.model.enums.BookGenre;
import com.edurmus.librarymanagement.repository.BookRepository;
import com.edurmus.librarymanagement.service.impl.BookServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class BookServiceImplTest {


    @Mock
    private BookRepository bookRepository;

    @InjectMocks
    private BookServiceImpl bookService;

    private Book book;
    private BookRequest bookRequest;

    @BeforeEach
    void setup() {
        book = new Book();
        book.setId(1L);
        book.setTitle("Test Book");
        book.setAvailable(true);
        book.setActive(true);

        bookRequest = new BookRequest("Test Book", "Test Book",
                "123ABC123",  LocalDate.of(2000, 1, 1), 10.0, "CLASSICS",
                "Test Description");
    }

    @Test
    void save_shouldReturnSavedBookResponse() {
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponse response = bookService.save(bookRequest);

        assertNotNull(response);
        assertEquals("Test Book", response.title());
        verify(bookRepository).save(any(Book.class));
        log.info("save_shouldReturnSavedBookResponse test passed.");
    }

    @Test
    void save_shouldThrowBookSaveException_whenErrorOccurs() {
        when(bookRepository.save(any(Book.class))).thenThrow(RuntimeException.class);

        assertThrows(BookSaveException.class, () -> bookService.save(bookRequest));
        log.info("save_shouldThrowBookSaveException_whenErrorOccurs test passed.");
    }

    @Test
    void update_shouldReturnUpdatedBookResponse() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));
        when(bookRepository.save(any(Book.class))).thenReturn(book);

        BookResponse response = bookService.update(1L, bookRequest);

        assertNotNull(response);
        assertEquals("Test Book", response.title());
        verify(bookRepository).save(any(Book.class));
        log.info("update_shouldReturnUpdatedBookResponse test passed.");
    }

    @Test
    void shouldThrowBookSaveException_whenErrorOccurs() {
        when(bookRepository.findById(anyLong())).thenReturn(Optional.empty());

        BookRequest mockBookResponse = new BookRequest(
                "Dummy Author",
                "Dummy Title",
                "DummyISBN",
                LocalDate.now(),
                10.0,
                "CLASSICS",
                "Dummy Description"
        );

        BookSaveException exception = assertThrows(BookSaveException.class, () -> {
            bookService.update(1L, mockBookResponse);
        });

        assertTrue(exception.getCause() instanceof BookNotFoundException);
        assertEquals("Book not found for the update with id: 1", exception.getCause().getMessage());

        log.info("shouldThrowBookSaveException_whenErrorOccurs test passed.");
    }

    @Test
    void shouldSetBookInactiveToDelete() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        bookService.deleteById(1L);

        assertFalse(book.isActive());
        verify(bookRepository).save(book);
        log.info("shouldSetBookInactiveToDelete test passed.");
    }

    @Test
    void shouldDeleteById_ThrowBookSaveException_whenErrorOccurs() {
        when(bookRepository.findById(1L)).thenThrow(new RuntimeException());

        assertThrows(BookSaveException.class, () -> bookService.deleteById(1L));
        log.info("shouldDeleteById_ThrowBookSaveException_whenErrorOccurs test passed.");
    }

    @Test
    void shouldGetById_shouldReturnBookResponse() {
        when(bookRepository.findById(1L)).thenReturn(Optional.of(book));

        BookResponse result = bookService.getById(1L);

        assertNotNull(result);
        assertEquals("Test Book", result.title());
        log.info("shouldGetById_shouldReturnBookResponse test passed.");
    }

    @Test
    void shouldGetById_ThrowBookNotFoundException_whenNotFound() {
        when(bookRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(BookNotFoundException.class, () -> bookService.getById(1L));
        log.info("shouldGetById_ThrowBookNotFoundException_whenNotFound test passed.");
    }

    @Test
    void shouldReturnListOfBookResponses() {
        when(bookRepository.findAllByActiveIsTrue()).thenReturn(Arrays.asList(book));

        List<BookResponse> list = bookService.getAll();

        assertEquals(1, list.size());
        assertEquals("Test Book", list.get(0).title());
        log.info("shouldReturnListOfBookResponses test passed.");
    }

    @Test
    void shouldThrowBookNotFoundException_whenErrorOccurs() {
        when(bookRepository.findAllByActiveIsTrue()).thenThrow(RuntimeException.class);

        assertThrows(BookNotFoundException.class, () -> bookService.getAll());
        log.info("shouldThrowBookNotFoundException_whenErrorOccurs test passed.");
    }

    @Test
    void shouldFindByIsAvailable_shouldReturnAvailableBooks() {
        when(bookRepository.findAllByIsAvailableIsTrueAndActiveIsTrue()).thenReturn(List.of(book));

        List<BookResponse> list = bookService.findByIsAvailable();

        assertEquals(1, list.size());
        assertTrue(list.get(0).available());
        log.info("shouldFindByIsAvailable_shouldReturnAvailableBooks test passed.");
    }

    @Test
    void shouldFindByAvailable_ThrowBookNotFoundException_whenErrorOccurs() {
        when(bookRepository.findAllByIsAvailableIsTrueAndActiveIsTrue()).thenThrow(RuntimeException.class);

        assertThrows(BookNotFoundException.class, () -> bookService.findByIsAvailable());
        log.info("shouldFindByAvailable_ThrowBookNotFoundException_whenErrorOccurs test passed.");
    }

    @Test
    void searchBooks_ShouldReturnBooks_WhenValidSearchRequest() {
        BookSearchRequest searchRequest = new BookSearchRequest("Java", "Author", "123456", BookGenre.FICTION);
        Book book = Book.builder()
                .title("Java")
                .author("Author")
                .isbn("123456")
                .genre(BookGenre.FICTION)
                .build();

        List<Book> books = List.of(book);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(books, pageable, books.size());

        when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<BookResponse> result = bookService.searchBooks(searchRequest, 0, 10);

        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("Java", result.getContent().get(0).title());
        verify(bookRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void searchBooks_ShouldReturnEmpty_WhenNoBooksFound() {
        BookSearchRequest searchRequest = new BookSearchRequest("NonExistentTitle", null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = new PageImpl<>(List.of(), pageable, 0);

        when(bookRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<BookResponse> result = bookService.searchBooks(searchRequest, 0, 10);

        assertNotNull(result);
        assertTrue(result.getContent().isEmpty());
        verify(bookRepository).findAll(any(Specification.class), eq(pageable));
    }
}

