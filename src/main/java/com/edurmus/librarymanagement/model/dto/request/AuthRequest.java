package com.edurmus.librarymanagement.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AuthRequest(
        @JsonProperty("username")
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        String username,

        @JsonProperty("password")
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 30, message = "Password must be between 6 and 30 characters")
        String password

) {}
