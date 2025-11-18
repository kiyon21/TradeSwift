package com.tradeswift.backend.controller;

import com.tradeswift.backend.model.dto.request.LoginRequest;
import com.tradeswift.backend.model.dto.response.ApiResponse;
import com.tradeswift.backend.model.dto.response.AuthResponse;
import com.tradeswift.backend.service.user.UserAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final UserAuthService userAuthService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginUser(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = userAuthService.login(loginRequest);

        ApiResponse<AuthResponse> response = ApiResponse.success(
                "Login Successful.", authResponse
        );
        return ResponseEntity.ok(response);
    }

}
