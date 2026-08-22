package com.demo.user.service;

import com.demo.user.dto.Dtos.AuthResponse;
import com.demo.user.dto.Dtos.LoginRequest;
import com.demo.user.dto.Dtos.UserResponse;
import com.demo.user.entity.User;
import com.demo.user.exception.UserExceptions;
import com.demo.user.repository.UserRepository;
import com.demo.user.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new UserExceptions.UserNotFoundException("Invalid email or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new UserExceptions.UserNotFoundException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole().name());
        
        UserResponse userResponse = new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole().name());
        return new AuthResponse(token, userResponse);
    }
}
