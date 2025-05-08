package com.edurmus.librarymanagement.model.mapper;


import com.edurmus.librarymanagement.model.dto.response.BookResponse;
import com.edurmus.librarymanagement.model.entity.Book;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookMapper INSTANCE = Mappers.getMapper(BookMapper.class);

    Book toEntity(BookResponse book);

    BookResponse toDto(Book book);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
                nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateEntity(@MappingTarget Book book, BookResponse bookResponse);

}
