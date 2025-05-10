package com.edurmus.librarymanagement.service;

import com.edurmus.librarymanagement.model.dto.request.BookRequest;
import com.edurmus.librarymanagement.model.dto.response.BookResponse;

import java.util.List;

public interface BookService {

    BookResponse save(BookRequest bookRequest);
    BookResponse update(Long id, BookRequest bookRequest);
    void deleteById(Long id);
    BookResponse getById(Long id);
    List<BookResponse> getAll();
    List<BookResponse> findByIsAvailable();
}
