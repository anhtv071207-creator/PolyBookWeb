package com.example.polybookbe.controller;

import com.example.polybookbe.dto.CreateStaffRequest;
import com.example.polybookbe.dto.UpdateLockRequest;
import com.example.polybookbe.dto.UserDetailResponse;
import com.example.polybookbe.dto.UserResponse;
import com.example.polybookbe.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/api/account-management")
@RequiredArgsConstructor
public class ManagementAccountController {
    private final UserService userService;

    @PostMapping("/admin/staff")
    public UserResponse createStaff(
            @RequestBody CreateStaffRequest request) {

        return userService.createStaff(request);
    }

    @GetMapping("/admin/users")
    public List<UserResponse> getAllUsers() {
        return userService.getAllUsers();
    }

    @PutMapping("/admin/users/{id}/status")
    public void updateUserStatus(
            @PathVariable Integer id,
            @RequestBody UpdateLockRequest request) {

        userService.updateStatus(id, request);
    }

    @GetMapping("/admin/users/{id}/detail")
    public UserDetailResponse getUserDetail(@PathVariable Integer id) {
        return userService.getUserDetail(id);
    }

    @GetMapping("/search")
    public List<UserResponse> searchUsers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String role,
            @RequestParam(required = false) String status
    ) {
        return userService.searchUsers(keyword, role, status);
    }
}
