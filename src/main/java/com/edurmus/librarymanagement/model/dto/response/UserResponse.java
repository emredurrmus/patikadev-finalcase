package com.edurmus.librarymanagement.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserResponse(@JsonProperty("username") String username,
                           @JsonProperty("email") String email) {
}

