package com.edurmus.librarymanagement.service.impl;

import com.edurmus.librarymanagement.exception.book.BookNotFoundException;
import com.edurmus.librarymanagement.exception.book.BookSaveException;
import com.edurmus.librarymanagement.model.dto.response.BookResponse;
import com.edurmus.librarymanagement.model.entity.Book;
import com.edurmus.librarymanagement.model.mapper.BookMapper;
import com.edurmus.librarymanagement.repository.BookRepository;
import com.edurmus.librarymanagement.service.ReactiveBookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReactiveBookServiceImpl implements ReactiveBookService {

    private final BookRepository bookRepository;

    @Override
    public Mono<BookResponse> save(BookResponse bookDTO) {
        return Mono.fromCallable(() -> {
                    Book book = BookMapper.INSTANCE.toEntity(bookDTO);
                    book.setAvailable(true);
                    return bookRepository.save(book);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(BookMapper.INSTANCE::toDto)
                .doOnSuccess(bookDto -> log.info("Book saved: {}", bookDto.title()))
                .onErrorMap(e -> new BookSaveException("Failed to save the book: " + e.getMessage(), e));
    }

    @Override
    public Mono<BookResponse> update(Long id, BookResponse bookDTO) {
        return Mono.fromCallable(() -> {
                    Book book = bookRepository.findById(id)
                            .orElseThrow(() -> new BookNotFoundException("Book not found for the update with id: " + id));
                    BookMapper.INSTANCE.updateEntity(book, bookDTO);
                    return bookRepository.save(book);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .map(BookMapper.INSTANCE::toDto)
                .doOnSuccess(bookDto -> log.info("Book updated: {}", bookDto.title()))
                .onErrorMap(e -> new BookSaveException("Failed to update the book: " + e.getMessage(), e));
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return Mono.fromCallable(() -> bookRepository.findById(id)
                        .orElseThrow(() -> new BookNotFoundException("Book not found for the delete with id: " + id)))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(book -> {
                    book.setActive(false);
                    return Mono.fromCallable(() -> bookRepository.save(book))
                            .subscribeOn(Schedulers.boundedElastic())
                            .doOnTerminate(() -> log.info("Book with ID {} set as inactive", book.getId()))
                            .then();
                })
                .onErrorMap(e -> new BookSaveException("Failed to delete the book: " + e.getMessage(), e));
    }



    @Override
    public Mono<BookResponse> getById(Long id) {
        return Mono.fromCallable(() -> bookRepository.findById(id)
                        .orElseThrow(() -> new BookNotFoundException("Book not found by id")))
                .subscribeOn(Schedulers.boundedElastic())
                .map(BookMapper.INSTANCE::toDto)
                .doOnSuccess(bookDto -> log.info("Book fetched successfully: {}", bookDto.title()))
                .doOnError(error -> log.error("Error fetching book with id {}: {}", id, error.getMessage()))
                .onErrorMap(e -> new BookNotFoundException("Book not found by id: " + id));
    }


    @Override
    public Flux<BookResponse> getAll() {
        return Flux.fromIterable(bookRepository.findAllByActiveIsTrue())
                .map(BookMapper.INSTANCE::toDto)
                .onErrorMap(error -> {
                    log.error("Error fetching all books: {}", error.getMessage());
                    return new BookNotFoundException("Error fetching all books: " + error.getMessage());
                }).delayElements(Duration.ofSeconds(1));
    }

    @Override
    public Flux<BookResponse> findByIsAvailable() {
        return Flux.fromIterable(bookRepository.findAllByIsAvailableIsTrueAndActiveIsTrue())
                .map(BookMapper.INSTANCE::toDto)
                .onErrorMap(error -> new BookNotFoundException("Error fetching available books: " + error.getMessage()))
                .delayElements(Duration.ofSeconds(1));
    }
}
