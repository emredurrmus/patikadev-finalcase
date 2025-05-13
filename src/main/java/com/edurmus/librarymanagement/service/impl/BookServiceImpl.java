package com.edurmus.librarymanagement.service.impl;

import com.edurmus.librarymanagement.exception.book.BookNotFoundException;
import com.edurmus.librarymanagement.exception.book.BookSaveException;
import com.edurmus.librarymanagement.model.dto.request.BookRequest;
import com.edurmus.librarymanagement.model.dto.request.BookSearchRequest;
import com.edurmus.librarymanagement.model.dto.response.BookResponse;
import com.edurmus.librarymanagement.model.entity.Book;
import com.edurmus.librarymanagement.model.mapper.BookMapper;
import com.edurmus.librarymanagement.repository.BookRepository;
import com.edurmus.librarymanagement.service.BookService;
import com.edurmus.librarymanagement.service.specification.GenericSpecification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    @Override
    @Transactional
    public BookResponse save(BookRequest bookRequest) {
        try {
            Book book = BookMapper.INSTANCE.toEntity(bookRequest);
            book.setAvailable(true);
            Book savedBook = bookRepository.save(book);
            log.info("Book saved: {}", savedBook.getTitle());
            return BookMapper.INSTANCE.toDto(savedBook);
        } catch (Exception e) {
            throw new BookSaveException("Failed to save the book: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public BookResponse update(Long id, BookRequest bookRequest) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new BookNotFoundException("Book not found for the update with id: " + id));
            BookMapper.INSTANCE.updateEntity(book, bookRequest);
            Book updatedBook = bookRepository.save(book);
            log.info("Book updated: {}", updatedBook.getTitle());
            return BookMapper.INSTANCE.toDto(updatedBook);
        } catch (Exception e) {
            throw new BookSaveException("Failed to update the book: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new BookNotFoundException("Book not found for the delete with id: " + id));
            book.setActive(false);
            bookRepository.save(book);
            log.info("Book with ID {} set as inactive", book.getId());
        } catch (Exception e) {
            throw new BookSaveException("Failed to delete the book: " + e.getMessage(), e);
        }
    }

    @Override
    public BookResponse getById(Long id) {
        try {
            Book book = bookRepository.findById(id)
                    .orElseThrow(() -> new BookNotFoundException("Book not found by id: " + id));
            BookResponse bookResponse = BookMapper.INSTANCE.toDto(book);
            log.info("Book fetched successfully: {}", bookResponse.title());
            return bookResponse;
        } catch (Exception e) {
            log.error("Error fetching book with id {}: {}", id, e.getMessage());
            throw new BookNotFoundException("Book not found by id: " + id);
        }
    }

    @Override
    public List<BookResponse> getAll() {
        try {
            return bookRepository.findAllByActiveIsTrue()
                    .stream()
                    .map(BookMapper.INSTANCE::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error fetching all books: {}", e.getMessage());
            throw new BookNotFoundException("Error fetching all books: " + e.getMessage());
        }
    }

    @Override
    public List<BookResponse> findByIsAvailable() {
        try {
            return bookRepository.findAllByIsAvailableIsTrueAndActiveIsTrue()
                    .stream()
                    .map(BookMapper.INSTANCE::toDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new BookNotFoundException("Error fetching available books: " + e.getMessage());
        }
    }

    @Override
    public Page<BookResponse> searchBooks(BookSearchRequest bookSearchRequest, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Specification<Book> specification = new GenericSpecification<BookSearchRequest, Book>()
                .build(bookSearchRequest);

        return bookRepository.findAll(specification, pageable)
                .map(BookMapper.INSTANCE::toDto);
    }
}
