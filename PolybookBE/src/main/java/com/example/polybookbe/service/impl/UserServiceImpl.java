package com.example.polybookbe.service.impl;

import com.example.polybookbe.dto.UserInfo;
import com.example.polybookbe.entity.Address;
import com.example.polybookbe.entity.User;
import com.example.polybookbe.repository.AddressRepository;
import com.example.polybookbe.repository.UserRepository;
import com.example.polybookbe.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AddressRepository addressRepository;

    public UserServiceImpl(UserRepository userRepository,
                           PasswordEncoder passwordEncoder,
                           AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.addressRepository = addressRepository;
    }

    @Override
    public User register(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("ROLE_USER");
        user.setTrangThai(true);
        user.setNgayTao(new Date());

        return userRepository.save(user);
    }

    @Override
    public User save(User user) {
        if (user.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return null;
        }

        String email = authentication.getName();
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public UserInfo getUserInfo(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository.findFirstByUserId(userId);

        return UserInfo.builder()
                .userId(user.getId())
                .hoTenNguoiNhan(user.getHoTen())
                .email(user.getEmail())
                .phone(user.getPhone())
                .quocGia(address != null ? address.getQuocGia() : "Việt Nam")
                .tinhThanh(address != null ? address.getTinhThanh() : "")
                .quanHuyen(address != null ? address.getQuanHuyen() : "")
                .phuongXa(address != null ? address.getPhuongXa() : "")
                .diaChi(address != null ? address.getDiaChi() : "")
                .build();
    }
}
