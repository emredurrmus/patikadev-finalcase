package com.edurmus.librarymanagement.controller;

import com.edurmus.librarymanagement.model.dto.request.BookRequest;
import com.edurmus.librarymanagement.model.dto.response.BookResponse;
import com.edurmus.librarymanagement.service.ReactiveBookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/reactive/books")
@Tag(name = "Reactive Book Management (Optional)", description = "Operations related to book management")
public class ReactiveBookController {

    private final ReactiveBookService bookService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<BookResponse> createBook(@Valid @RequestBody BookRequest bookRequest) {
        log.info("Request received to add a new book: {}", bookRequest.title());
         return bookService.save(bookRequest);
    }


    @PutMapping("/{id}")
    public Mono<ResponseEntity<BookResponse>> updateBook(@PathVariable Long id, @Valid @RequestBody BookRequest bookRequest) {
        log.info("Request received to update book with id: {}", id);
        return bookService.update(id, bookRequest)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<BookResponse>> getBookById(@PathVariable Long id) {
        return bookService.getById(id)
                .map(ResponseEntity::ok)
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteBook(@PathVariable Long id) {
        return bookService.deleteById(id);
    }

    @GetMapping(produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BookResponse> getAllBooks() {
        log.info("Fetching all active books");
        return bookService.getAll();
    }

    @GetMapping(path = "/available", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<BookResponse> getAvailableBooks() {
        return bookService.findByIsAvailable();
    }
}
