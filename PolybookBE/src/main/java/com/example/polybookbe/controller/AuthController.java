package com.example.polybookbe.controller;

import com.example.polybookbe.entity.User;
import com.example.polybookbe.security.JwtUtil;
import com.example.polybookbe.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
public class AuthController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(
            UserService userService,
            PasswordEncoder passwordEncoder,
            JwtUtil jwtUtil
    ) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public User register(@RequestBody User user) {
        return userService.register(user);
    }

    @PostMapping("/login")
    public Map<String, String> login(@RequestBody Map<String, String> req) {
        User user = userService.findByEmail(req.get("email"));

        if (user == null || !passwordEncoder.matches(req.get("password"), user.getPassword())) {
            throw new RuntimeException("Sai email hoặc mật khẩu");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());
        return Map.of("token", token);
    }
}
