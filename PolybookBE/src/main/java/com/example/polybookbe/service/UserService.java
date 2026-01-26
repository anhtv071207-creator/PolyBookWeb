package com.example.polybookbe.service;

import com.example.polybookbe.entity.User;

public interface UserService {
    User save(User user);

    User register(User user);

    User getCurrentUser();

    User findByEmail(String email);
}
