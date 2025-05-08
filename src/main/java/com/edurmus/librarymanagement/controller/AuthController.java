package com.edurmus.librarymanagement.controller;

import com.edurmus.librarymanagement.model.dto.request.AuthRequest;
import com.edurmus.librarymanagement.model.dto.request.UserRequest;
import com.edurmus.librarymanagement.model.dto.response.AuthResponse;
import com.edurmus.librarymanagement.model.dto.response.UserResponse;
import com.edurmus.librarymanagement.service.UserService;
import com.edurmus.librarymanagement.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Slf4j
@Tag(name = "Authentication", description = "API endpoints for authentication")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserDetailsService userDetailsService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "Login user", description = "Allows user to login")
    @ApiResponse(responseCode = "200", description = "User successfully logged in")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );
        UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
        String token = jwtUtil.generateToken(userDetails);
        if (token == null) {
            log.error("Token generation failed for user {}", request.username());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new AuthResponse("Token generation failed"));
        }
        return ResponseEntity.ok(new AuthResponse(token));
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Allows librarian user to register a new user")
    @ApiResponse(responseCode = "201", description = "User successfully registered")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest userRequest) {
        log.info("Registering user: {}", userRequest.email());
        UserResponse savedUser = userService.register(userRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUser);
    }


}
