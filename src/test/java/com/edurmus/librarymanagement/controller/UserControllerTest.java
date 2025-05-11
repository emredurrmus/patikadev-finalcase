package com.edurmus.librarymanagement.controller;

import com.edurmus.librarymanagement.model.dto.request.UserRequest;
import com.edurmus.librarymanagement.model.dto.request.UserRoleRequest;
import com.edurmus.librarymanagement.model.dto.response.UserDetailsResponse;
import com.edurmus.librarymanagement.model.dto.response.UserRoleResponse;
import com.edurmus.librarymanagement.service.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final Long USER_ID = 1L;
    private static final Long NON_EXISTENT_ID = 99L;
    private static final String USERNAME = "emre_durmus";
    private static final String FIRST_NAME = "Emre";
    private static final String LAST_NAME = "Durmus";
    private static final String EMAIL = "emre@example.com";
    private static final String PHONE = "05463453543";
    private static final String ROLE = "ROLE_LIBRARIAN";

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @Test
    void shouldGetAllUsers() {
        UserDetailsResponse userDetails = createUserDetailsResponse(USER_ID, USERNAME, EMAIL, ROLE);
        when(userService.getAllUsers()).thenReturn(Collections.singletonList(userDetails));

        ResponseEntity<List<UserDetailsResponse>> response = userController.getAllUsers();

        assertOkWithBody(response);
        assertEquals(USERNAME, response.getBody().get(0).username());
    }

    @Test
    void shouldGetUserById() {
        UserDetailsResponse userDetails = createUserDetailsResponse(USER_ID, USERNAME, EMAIL, ROLE);
        when(userService.getById(USER_ID)).thenReturn(userDetails);

        ResponseEntity<UserDetailsResponse> response = userController.getUserById(USER_ID);

        assertOkWithBody(response);
        assertEquals(USER_ID, response.getBody().id());
    }

    @Test
    void shouldReturnNotFoundWhenUserDoesNotExist() {
        when(userService.getById(NON_EXISTENT_ID)).thenReturn(null);

        ResponseEntity<UserDetailsResponse> response = userController.getUserById(NON_EXISTENT_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldUpdateUser() {
        UserRequest request = createUserRequest();
        UserDetailsResponse updatedUser = createUserDetailsResponse(USER_ID, "UpdatedUser", "updated@example.com", ROLE);

        when(userService.updateUser(USER_ID, request)).thenReturn(updatedUser);

        ResponseEntity<UserDetailsResponse> response = userController.updateUser(USER_ID, request);

        assertOkWithBody(response);
        assertEquals("UpdatedUser", response.getBody().username());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingNonExistentUser() {
        UserRequest request = createUserRequest();
        when(userService.updateUser(NON_EXISTENT_ID, request)).thenReturn(null);

        ResponseEntity<UserDetailsResponse> response = userController.updateUser(NON_EXISTENT_ID, request);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldUpdateUserRole() {
        UserRoleRequest roleRequest = new UserRoleRequest(ROLE);
        UserRoleResponse roleResponse = new UserRoleResponse(USERNAME, ROLE);

        when(userService.updateUserRole(USER_ID, roleRequest)).thenReturn(roleResponse);

        ResponseEntity<UserRoleResponse> response = userController.updateUserRole(roleRequest, USER_ID);

        assertOkWithBody(response);
        assertEquals(ROLE, response.getBody().role());
    }

    @Test
    void shouldReturnNotFoundWhenUpdatingRoleOfNonExistentUser() {
        UserRoleRequest roleRequest = new UserRoleRequest(ROLE);
        when(userService.updateUserRole(NON_EXISTENT_ID, roleRequest)).thenReturn(null);

        ResponseEntity<UserRoleResponse> response = userController.updateUserRole(roleRequest, NON_EXISTENT_ID);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void shouldDeleteUser() {
        doNothing().when(userService).deleteUser(USER_ID);

        ResponseEntity<Void> response = userController.deleteUser(USER_ID);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService).deleteUser(USER_ID);
    }

    // Helper methods
    private UserDetailsResponse createUserDetailsResponse(Long id, String username, String email, String role) {
        return new UserDetailsResponse(id, username, LAST_NAME, email, role, true);
    }

    private UserRequest createUserRequest() {
        return new UserRequest(FIRST_NAME, LAST_NAME, USERNAME, EMAIL, PHONE, "sifre123");
    }

    private void assertOkWithBody(ResponseEntity<?> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }
}
