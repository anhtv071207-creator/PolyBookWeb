package com.example.polybookbe.controller;

import com.example.polybookbe.dto.UpdateProfileRequest;
import com.example.polybookbe.dto.UserDetailResponse;
import com.example.polybookbe.dto.UserInfo;
import com.example.polybookbe.entity.User;
import com.example.polybookbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}/info")
    public ResponseEntity<UserInfo> getUserInfo(@PathVariable Integer userId) {
        return ResponseEntity.ok(userService.getUserInfo(userId));
    }
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestBody UpdateProfileRequest request
    ) {
        User user = userService.updateProfile(request);
        UserDetailResponse response =
                userService.getUserDetail(user.getId());
        return ResponseEntity.ok(response);
    }
}


