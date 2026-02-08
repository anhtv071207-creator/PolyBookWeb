package com.example.polybookbe.service;

import com.example.polybookbe.dto.*;
import com.example.polybookbe.entity.User;

import java.util.List;

public interface UserService {
    User save(User user);

    User register(RegisterRequest request);

    User getCurrentUser();

    User findByEmail(String email);

    UserInfo getUserInfo(Integer userId);


    UserResponse createStaff(CreateStaffRequest request);

//    UserResponse updateProfile(Integer userId, UpdateUserRequest request);

    void updateStatus(Integer userId, UpdateLockRequest request);

    List<UserResponse> getAllUsers();

    UserDetailResponse getUserDetail(Integer userId);

    User updateProfile(UpdateProfileRequest request);

    List<UserResponse> searchUsers(String keyword, String role, String status);

}
