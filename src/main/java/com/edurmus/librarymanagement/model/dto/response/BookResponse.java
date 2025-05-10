package com.edurmus.librarymanagement.model.dto.response;

import com.edurmus.librarymanagement.model.enums.BookGenre;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDate;

@Schema(name = "Book", description = "Response representing a book")
public record BookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        LocalDate publishedDate,
        double price,
        BookGenre genre,
        String description,
        boolean available
) {}
