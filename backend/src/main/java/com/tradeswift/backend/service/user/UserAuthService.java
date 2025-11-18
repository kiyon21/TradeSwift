package  com.tradeswift.backend.service.user;

import com.tradeswift.backend.config.JwtConfig;
import com.tradeswift.backend.exception.BadRequestException;
import com.tradeswift.backend.exception.DuplicateResourceException;
import com.tradeswift.backend.exception.ResourceNotFoundException;
import com.tradeswift.backend.model.dto.request.ChangePasswordRequest;
import com.tradeswift.backend.model.dto.request.LoginRequest;
import com.tradeswift.backend.model.dto.request.RegisterRequest;
import com.tradeswift.backend.model.dto.response.AuthResponse;
import com.tradeswift.backend.model.dto.response.UserResponse;
import com.tradeswift.backend.model.entity.User;
import com.tradeswift.backend.model.enums.UserStatus;
import com.tradeswift.backend.repository.UserRepository;
import com.tradeswift.backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.tradeswift.backend.service.user.UserService.convertToResponse;

@Service
@RequiredArgsConstructor
public class UserAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtConfig jwtConfig;

    /**
     * Registers user in db if not already registered.
     * @param registerRequest new user to register.
     * @return UserResponse
     */
    public UserResponse registerUser(RegisterRequest registerRequest) {
        // Check if email or phone number exists
        if(userRepository.existsByPhone(registerRequest.getPhone())) {
            throw new DuplicateResourceException("User", "phone",  registerRequest.getPhone());
        }
        if(registerRequest.getEmail() != null && userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new  DuplicateResourceException("User", "email",  registerRequest.getEmail());
        }

        String hashedPassword = passwordEncoder.encode(registerRequest.getPassword());

        User user = User.builder()
                .phone(registerRequest.getPhone())
                .email(registerRequest.getEmail())
                .passwordHash(hashedPassword)
                .userType(registerRequest.getUserType())
                .isVerified(false)
                .status(UserStatus.PENDING)
                .build();

        User savedUser = userRepository.save(user);
        return convertToResponse(savedUser);
    }

    /**
     * Login user if valid.
     * @param loginRequest
     * @return AuthResponse with Bearer Token
     */
    public AuthResponse login(LoginRequest loginRequest) {
        // 1. Find user by phone
        User user = userRepository.findByPhone(loginRequest.getPhone())
                .orElseThrow(() -> new ResourceNotFoundException("User", "phone", loginRequest.getPhone()));

        // 2. Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Invalid credentials");
        }

        return AuthResponse.builder()
                .accessToken(jwtTokenProvider.generateToken(user))
                .expiresIn(jwtConfig.getExpirationMs()/1000)
                .userId(user.getUserId())
                .tokenType("Bearer")
                .phone(user.getPhone())
                .email(user.getEmail())
                .userType(user.getUserType())
                .status(user.getStatus())
                .isVerified(user.getIsVerified())
                .build();
    }

    /**
     * Changes password for specific uuid
     * @param uuid
     * @param changePasswordRequest
     * @return
     */
    public UserResponse changePassword(UUID uuid, ChangePasswordRequest changePasswordRequest) {
        User user = userRepository.findById(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("user", "id", uuid.toString()));

        if(!verifyPassword(changePasswordRequest.getCurrentPassword(), user.getPasswordHash())) {
            throw new BadRequestException("Current password is incorrect");
        }

        String newPasswordHash = passwordEncoder.encode(changePasswordRequest.getNewPassword());
        user.setPasswordHash(newPasswordHash);
        userRepository.save(user);
        return convertToResponse(user);
    }

    /**
     * Verifies if a raw password matches the stored hash
     *
     * @param rawPassword The plain text password from login
     * @param hashedPassword The BCrypt hash from database
     * @return true if passwords match, false otherwise
     */
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }
}