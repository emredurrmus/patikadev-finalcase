package com.edurmus.librarymanagement.model.dto.request;

import com.edurmus.librarymanagement.model.annotation.SearchableField;
import com.edurmus.librarymanagement.model.enums.BookGenre;
import com.edurmus.librarymanagement.model.enums.ComparisonOperation;

public record BookSearchRequest(
        @SearchableField(operation = ComparisonOperation.LIKE)
        String title,

        @SearchableField(operation = ComparisonOperation.LIKE)
        String author,

        @SearchableField(operation = ComparisonOperation.EQUAL)
        String isbn,

        @SearchableField(operation = ComparisonOperation.EQUAL)
        BookGenre genre) {
}
