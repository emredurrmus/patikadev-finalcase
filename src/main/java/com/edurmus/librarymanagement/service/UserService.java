package com.edurmus.librarymanagement.service;

import com.edurmus.librarymanagement.model.dto.request.UserRequest;
import com.edurmus.librarymanagement.model.dto.request.UserRoleRequest;
import com.edurmus.librarymanagement.model.dto.response.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse register(UserRequest request);
    List<UserResponse> getAllUsers();
    UserResponse getById(Long id);
    UserResponse updateUser(Long id, UserRequest request);
    UserResponse updateUserRole(Long id, UserRoleRequest request);
    void deleteUser(Long id);
}

