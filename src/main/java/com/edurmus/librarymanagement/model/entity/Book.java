package com.edurmus.librarymanagement.model.entity;


import com.edurmus.librarymanagement.model.enums.BookGenre;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Book extends BaseEntity {

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String author;

    @Column(nullable = false, unique = true)
    private String isbn;

    private LocalDate publishedDate;

    private BigDecimal price;

    private String description;

    @Enumerated(EnumType.STRING)
    private BookGenre genre;

    private boolean isAvailable = true;

}
