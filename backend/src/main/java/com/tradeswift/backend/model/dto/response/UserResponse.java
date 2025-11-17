package com.tradeswift.backend.model.dto.response;

import com.tradeswift.backend.model.enums.UserStatus;
import com.tradeswift.backend.model.enums.UserType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class UserResponse {
    private UUID userId;
    private String phone;
    private String email;
    private UserType userType;
    private UserStatus userStatus;
    private Boolean isVerified;
    private LocalDateTime createdAt;
}
