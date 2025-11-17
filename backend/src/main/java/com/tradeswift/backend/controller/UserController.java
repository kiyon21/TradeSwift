package com.tradeswift.backend.controller;

import com.tradeswift.backend.model.dto.request.RegisterRequest;
import com.tradeswift.backend.model.dto.response.ApiResponse;
import com.tradeswift.backend.model.dto.response.UserResponse;
import com.tradeswift.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<UserResponse>> registerUser(
            @Valid @RequestBody RegisterRequest request) {
        UserResponse userResponse = userService.registerUser(request);

        ApiResponse<UserResponse> response = ApiResponse.success(
                "User Registered Successfully", userResponse
        );

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
