package com.edurmus.librarymanagement.model.mapper;

import com.edurmus.librarymanagement.model.dto.response.BorrowingDTO;
import com.edurmus.librarymanagement.model.entity.Borrowing;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface BorrowingMapper {

    BorrowingMapper INSTANCE = Mappers.getMapper(BorrowingMapper.class);

    Borrowing toEntity(BorrowingDTO borrowingDTO);

    BorrowingDTO toDto(Borrowing borrowing);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateEntity(@MappingTarget Borrowing borrowing, BorrowingDTO borrowingDTO);

}
