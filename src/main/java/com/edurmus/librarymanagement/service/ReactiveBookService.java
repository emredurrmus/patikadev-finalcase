package com.edurmus.librarymanagement.service;

import com.edurmus.librarymanagement.model.dto.request.BookRequest;
import com.edurmus.librarymanagement.model.dto.response.BookResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveBookService {
    Mono<BookResponse> save(BookRequest bookRequest);
    Mono<BookResponse> update(Long id, BookRequest bookRequest);
    Mono<Void> deleteById(Long id);
    Mono<BookResponse> getById(Long id);
    Flux<BookResponse> getAll();
    Flux<BookResponse> findByIsAvailable();

}
