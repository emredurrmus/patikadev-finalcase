package com.edurmus.librarymanagement.model.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

@Schema(name = "Book", description = "Book request object")
public record BookRequest(
        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Author is required")
        String author,

        @NotBlank(message = "ISBN is required")
        @Pattern(regexp = "^(\\d{10}|\\d{13})$", message = "ISBN must be 10 or 13 digits")
        String isbn,

        @NotNull(message = "Published date is required")
        LocalDate publishedDate,

        @Positive(message = "Price must be positive")
        double price,

        @NotNull(message = "Genre is required")
        String genre,

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        String description

) { }
