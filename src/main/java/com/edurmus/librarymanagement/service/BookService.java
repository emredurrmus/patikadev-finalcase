package com.edurmus.librarymanagement.service;

import com.edurmus.librarymanagement.model.dto.response.BookResponse;

import java.util.List;

public interface BookService {

    BookResponse save(BookResponse bookResponse);
    BookResponse update(Long id, BookResponse bookResponse);
    Void deleteById(Long id);
    BookResponse getById(Long id);
    List<BookResponse> getAll();
    List<BookResponse> findByIsAvailable();
}
