package com.edurmus.librarymanagement.model.mapper;

import com.edurmus.librarymanagement.model.dto.request.UserRequest;
import com.edurmus.librarymanagement.model.dto.response.UserResponse;
import com.edurmus.librarymanagement.model.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequest userRequest);

    UserResponse toDto(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void updateEntity(@MappingTarget User user, UserRequest userRequest);
}
