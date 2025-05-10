package com.edurmus.librarymanagement.model.mapper;


import com.edurmus.librarymanagement.model.entity.Role;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class RoleMapper {

    @Named("mapRolesToString")
    public String mapRolesToString(Set<Role> roles) {
        return roles.stream()
                .map(role -> role.getUserRole().name())
                .collect(Collectors.joining(", "));
    }
}
