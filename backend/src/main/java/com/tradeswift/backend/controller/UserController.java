package com.tradeswift.backend.controller;

import com.tradeswift.backend.model.dto.request.ChangePasswordRequest;
import com.tradeswift.backend.model.dto.request.RegisterRequest;
import com.tradeswift.backend.model.dto.response.ApiResponse;
import com.tradeswift.backend.model.dto.response.UserResponse;
import com.tradeswift.backend.service.user.UserAuthService;
import com.tradeswift.backend.service.user.UserQueryService;
import com.tradeswift.backend.service.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserAuthService userAuthService;
    private final UserQueryService userQueryService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(@PathVariable String userId) {
        UUID uuid = UUID.fromString(userId);
        UserResponse userResponse = userQueryService.getUserById(uuid);
        ApiResponse<UserResponse> response = ApiResponse.success(
                "User Retrieved Successfully", userResponse
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        // Extract phone from authenticated user
        String phone = userDetails.getUsername();

        // Get user by phone
        UserResponse user = userQueryService.getUserByPhone(phone);

        ApiResponse<UserResponse> response = ApiResponse.success(
                "Current user retrieved successfully",
                user
        );

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<UserResponse> userResponse = userQueryService.getAllUsers();

        ApiResponse<List<UserResponse>> response = ApiResponse.success(
                "Users Fetched", userResponse
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<ApiResponse<UserResponse>> changePassword(@PathVariable String userId, @Valid @RequestBody ChangePasswordRequest changePasswordRequest) {
        UUID uuid = UUID.fromString(userId);
        UserResponse userResponse = userAuthService.changePassword(uuid, changePasswordRequest);
        ApiResponse<UserResponse> response = ApiResponse.success(
                "User Password Successfully", userResponse
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(
            @Valid @RequestBody RegisterRequest request) {
        UserResponse userResponse = userAuthService.registerUser(request);

        ApiResponse<UserResponse> response = ApiResponse.success(
                "User Registered Successfully", userResponse
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

}
