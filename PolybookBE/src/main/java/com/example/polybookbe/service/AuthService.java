package com.example.polybookbe.service;

import com.example.polybookbe.dto.LoginRequest;
import com.example.polybookbe.dto.LoginResponse;
import com.example.polybookbe.entity.User;
import com.example.polybookbe.exception.ApiException;
import com.example.polybookbe.repository.UserRepository;
import com.example.polybookbe.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new ApiException("Email không tồn tại", "EMAIL_NOT_FOUND")
                );

        if (!user.getTrangThai()) {
            throw new ApiException("Tài khoản đã bị khóa", "ACCOUNT_LOCKED");
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new ApiException("Mật khẩu không chính xác", "WRONG_PASSWORD");
        }

        String token = jwtUtil.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole(),
                user.getHoTen()
        );
    }

}

