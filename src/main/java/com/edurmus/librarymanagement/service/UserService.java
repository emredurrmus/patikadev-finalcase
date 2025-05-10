package com.edurmus.librarymanagement.service;

import com.edurmus.librarymanagement.model.dto.request.UserRequest;
import com.edurmus.librarymanagement.model.dto.request.UserRoleRequest;
import com.edurmus.librarymanagement.model.dto.response.UserDetailsResponse;
import com.edurmus.librarymanagement.model.dto.response.UserResponse;
import com.edurmus.librarymanagement.model.dto.response.UserRoleResponse;

import java.util.List;

public interface UserService {
    UserResponse register(UserRequest request);
    List<UserDetailsResponse> getAllUsers();
    UserDetailsResponse getById(Long id);
    UserDetailsResponse updateUser(Long id, UserRequest request);
    UserRoleResponse updateUserRole(Long id, UserRoleRequest request);
    void deleteUser(Long id);
}

