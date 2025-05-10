package com.edurmus.librarymanagement.model.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UserDetailsResponse(@JsonProperty("id") Long id,
                                  @JsonProperty("username") String username,
                                  @JsonProperty("email") String email,
                                  @JsonProperty("phone_number") String phoneNumber,
                                  @JsonProperty("role") String role,
                                  @JsonProperty("enabled") boolean enabled) {
}
