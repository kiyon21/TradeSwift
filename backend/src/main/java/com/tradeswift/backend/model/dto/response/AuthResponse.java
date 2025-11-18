package com.tradeswift.backend.model.dto.response;

import com.tradeswift.backend.model.enums.UserStatus;
import com.tradeswift.backend.model.enums.UserType;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AuthResponse {

    private String accessToken;

    @Builder.Default
    private String tokenType = "Bearer";

    private Long expiresIn;

    // User information
    private UUID userId;

    private String phone;

    private String email;

    private UserType userType;

    private UserStatus status;

    private Boolean isVerified;
}