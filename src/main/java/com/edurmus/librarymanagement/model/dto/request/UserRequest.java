package com.edurmus.librarymanagement.model.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserRequest(

        @JsonProperty("firstname")
        @NotBlank(message = "First name cannot be blank")
        @Size(min = 2, max = 30, message = "First name must be between 2 and 30 characters")
        String firstName,

        @JsonProperty("lastname")
        @NotBlank(message = "Last name cannot be blank")
        @Size(min = 2, max = 30, message = "Last name must be between 2 and 30 characters")
        String lastName,

        @JsonProperty("username")
        @NotBlank(message = "Username cannot be blank")
        @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
        String username,

        @JsonProperty("email")
        @NotBlank(message = "Email cannot be blank")
        @Email(message = "Email should be valid")
        String email,

        @JsonProperty("phone_number")
        @NotBlank(message = "Phone number cannot be blank")
        @Pattern(
                regexp = "^\\+?[0-9]{10,15}$",
                message = "Phone number must be valid and contain 10 to 15 digits"
        )
        String phoneNumber,

        @JsonProperty("password")
        @NotBlank(message = "Password cannot be blank")
        @Size(min = 6, max = 30, message = "Password must be between 6 and 30 characters")
        String password

) {}
