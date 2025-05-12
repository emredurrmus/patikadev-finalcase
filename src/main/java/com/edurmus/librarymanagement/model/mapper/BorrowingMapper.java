package com.edurmus.librarymanagement.model.mapper;

import com.edurmus.librarymanagement.model.dto.response.BorrowingDTO;
import com.edurmus.librarymanagement.model.dto.response.BorrowingSuccessResponse;
import com.edurmus.librarymanagement.model.entity.Borrowing;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface BorrowingMapper {

    BorrowingMapper INSTANCE = Mappers.getMapper(BorrowingMapper.class);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "bookTitle", source = "book.title")
    BorrowingSuccessResponse toSuccessResponse(Borrowing borrowing);

    @Mapping(target = "username", source = "user.username")
    @Mapping(target = "bookTitle", source = "book.title")
    BorrowingDTO toDto(Borrowing borrowing);


}
