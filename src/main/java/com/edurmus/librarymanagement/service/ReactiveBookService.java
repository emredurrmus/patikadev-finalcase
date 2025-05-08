package com.edurmus.librarymanagement.service;

import com.edurmus.librarymanagement.model.dto.response.BookResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveBookService {
    Mono<BookResponse> save(BookResponse bookDTO);
    Mono<BookResponse> update(Long id, BookResponse bookDTO);
    Mono<Void> deleteById(Long id);
    Mono<BookResponse> getById(Long id);
    Flux<BookResponse> getAll();
    Flux<BookResponse> findByIsAvailable();

}
