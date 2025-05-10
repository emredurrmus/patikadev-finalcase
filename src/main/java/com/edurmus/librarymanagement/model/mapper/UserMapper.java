package com.edurmus.librarymanagement.model.mapper;

import com.edurmus.librarymanagement.model.dto.request.UserRequest;
import com.edurmus.librarymanagement.model.dto.response.UserDetailsResponse;
import com.edurmus.librarymanagement.model.dto.response.UserResponse;
import com.edurmus.librarymanagement.model.dto.response.UserRoleResponse;
import com.edurmus.librarymanagement.model.entity.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = RoleMapper.class)
public interface UserMapper {

    UserResponse toDto(User user);

    @Mapping(target = "role", source = "roles", qualifiedByName = "mapRolesToString")
    UserRoleResponse toRoleDto(User user);

    @Mapping(target = "role", source = "roles", qualifiedByName = "mapRolesToString")
    UserDetailsResponse toDetailsDto(User user);

}
