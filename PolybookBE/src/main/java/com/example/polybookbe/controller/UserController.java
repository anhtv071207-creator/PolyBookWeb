package com.example.polybookbe.controller;

import com.example.polybookbe.dto.UserInfo;
import com.example.polybookbe.entity.Address;
import com.example.polybookbe.entity.User;
import com.example.polybookbe.repository.AddressRepository;
import com.example.polybookbe.repository.UserRepository;
import com.example.polybookbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}/info")
    public ResponseEntity<UserInfo> getUserInfo(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }
}

