package com.edurmus.librarymanagement.controller;

import com.edurmus.librarymanagement.model.dto.request.AuthRequest;
import com.edurmus.librarymanagement.model.dto.request.UserRequest;
import com.edurmus.librarymanagement.model.dto.response.AuthResponse;
import com.edurmus.librarymanagement.model.dto.response.UserResponse;
import com.edurmus.librarymanagement.service.UserService;
import com.edurmus.librarymanagement.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AuthController authController;

    private static final String USERNAME = "emre_durmus";
    private static final String PASSWORD = "password123";
    private static final String TOKEN = "mocked_jwt_token";
    private static final String EMAIL = "emre@example.com";

    private AuthRequest authRequest;
    private UserRequest userRequest;

    @BeforeEach
    void setUp() {
        authRequest = new AuthRequest(USERNAME, PASSWORD);
        userRequest = new UserRequest("Emre", "Durmus", USERNAME, EMAIL, "05463453543", PASSWORD);
    }

    @Test
    void shouldLoginSuccessfully() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(TOKEN);

        ResponseEntity<?> response = authController.login(authRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody() instanceof AuthResponse);
        assertEquals(TOKEN, ((AuthResponse) response.getBody()).token());
    }

    @Test
    void shouldReturnInternalServerErrorWhenTokenGenerationFails() {
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetailsService.loadUserByUsername(USERNAME)).thenReturn(userDetails);
        when(jwtUtil.generateToken(userDetails)).thenReturn(null);

        ResponseEntity<?> response = authController.login(authRequest);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof AuthResponse);
        assertEquals("Token generation failed", ((AuthResponse) response.getBody()).token());
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        UserResponse userResponse = new UserResponse(USERNAME, EMAIL);
        when(userService.register(userRequest)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = authController.registerUser(userRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(userResponse.email(), response.getBody().email());
        assertEquals(userResponse.username(), response.getBody().username());
    }

    @Test
    void shouldReturnBadRequestWhenUserRegistrationFails() {
        when(userService.register(userRequest)).thenThrow(new RuntimeException("User registration failed"));

        try {
            authController.registerUser(userRequest);
            fail("Exception should have been thrown");
        } catch (Exception e) {
            assertEquals("User registration failed", e.getMessage());
        }
    }
}
