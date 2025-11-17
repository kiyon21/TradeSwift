package com.tradeswift.backend.service;

import com.tradeswift.backend.exception.DuplicateResourceException;
import com.tradeswift.backend.model.dto.request.RegisterRequest;
import com.tradeswift.backend.model.dto.response.UserResponse;
import com.tradeswift.backend.model.entity.User;
import com.tradeswift.backend.model.enums.UserStatus;
import com.tradeswift.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserResponse registerUser(RegisterRequest registerRequest) {
        // Check if email or phone number exists
        if(userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new DuplicateResourceException("User", "phone",  registerRequest.getPhone());
        }
        if(registerRequest.getEmail() != null && userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new  DuplicateResourceException("User", "email",  registerRequest.getEmail());
        }
        User user = User.builder()
                .phone(registerRequest.getPhone())
                .email(registerRequest.getEmail())
                .passwordHash(registerRequest.getPassword())
                .userType(registerRequest.getUserType())
                .isVerified(false)
                .status(UserStatus.PENDING)
                .build();

        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    private UserResponse convertToResponse(User user) {

        return UserResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .userType(user.getUserType())
                .userStatus(user.getStatus())
                .isVerified(user.getIsVerified())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
