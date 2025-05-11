package com.edurmus.librarymanagement.controller;

import com.edurmus.librarymanagement.model.dto.request.UserRequest;
import com.edurmus.librarymanagement.model.dto.request.UserRoleRequest;
import com.edurmus.librarymanagement.model.dto.response.UserDetailsResponse;
import com.edurmus.librarymanagement.model.dto.response.UserRoleResponse;
import com.edurmus.librarymanagement.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "Operations related to user management")
public class UserController {

    private final UserService userService;

    @GetMapping
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Get all users (Librarian only)", description = "Retrieves a list of all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of users fetched successfully"),
            @ApiResponse(responseCode = "404", description = "No users found")
    })
    public ResponseEntity<List<UserDetailsResponse>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Get user by ID (Librarian only)", description = "Retrieves a user by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found with the given ID successfully"),
            @ApiResponse(responseCode = "404", description = "User not found with the given ID")
    })
    public ResponseEntity<UserDetailsResponse> getUserById(@PathVariable Long id) {
        UserDetailsResponse user = userService.getById(id);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Update user by ID (Librarian only)", description = "Allows librarian user to update a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully updated"),
            @ApiResponse(responseCode = "404", description = "Error occurred while updating user")
    })
    public ResponseEntity<UserDetailsResponse> updateUser(@PathVariable Long id, @Valid @RequestBody UserRequest userRequest) {
        UserDetailsResponse updatedUser = userService.updateUser(id, userRequest);
        if (updatedUser == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/role/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Update user role by ID (Librarian only)", description = "Allows librarian user to update the role of another user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User role successfully updated"),
            @ApiResponse(responseCode = "404", description = "Error occurred while updating user role")
    })
    public ResponseEntity<UserRoleResponse> updateUserRole(@Valid @RequestBody UserRoleRequest userRoleRequest, @PathVariable Long id) {
        UserRoleResponse updatedRole = userService.updateUserRole(id, userRoleRequest);
        if (updatedRole == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedRole);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('LIBRARIAN')")
    @Operation(summary = "Delete user by ID (Librarian only)", description = "Allows librarian user to delete a user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User successfully deleted"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
