package com.example.polybookbe.service.impl;

import com.example.polybookbe.dto.*;
import com.example.polybookbe.entity.Address;
import com.example.polybookbe.entity.User;
import com.example.polybookbe.exception.ApiException;
import com.example.polybookbe.repository.AddressRepository;
import com.example.polybookbe.repository.UserRepository;
import com.example.polybookbe.service.UserService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    public User register(RegisterRequest request) {

        if (!StringUtils.hasText(request.getHoTen())) {
            throw new ApiException("Họ tên không được để trống", "FULLNAME_REQUIRED");
        }

        if (!StringUtils.hasText(request.getEmail())) {
            throw new ApiException("Email không được để trống", "EMAIL_REQUIRED");
        }

        if (!request.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ApiException("Email không hợp lệ", "EMAIL_INVALID");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("Email đã tồn tại", "EMAIL_EXISTS");
        }

        if (!StringUtils.hasText(request.getPassword())) {
            throw new ApiException("Mật khẩu không được để trống", "PASSWORD_REQUIRED");
        }

        if (request.getPassword().length() < 6) {
            throw new ApiException("Mật khẩu phải có ít nhất 6 ký tự", "PASSWORD_TOO_SHORT");
        }

        User user = new User();
        user.setHoTen(request.getHoTen());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        user.setRole("USER");
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

        Integer userId = Integer.valueOf(authentication.getName());
        return userRepository.findById(userId).orElse(null);
    }


    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public UserInfo getUserInfo(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository
                .findByUserId(userId)
                .orElse(null);

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

    @Override
    public UserResponse createStaff(CreateStaffRequest request) {

        if (!StringUtils.hasText(request.getHoTen())) {
            throw new ApiException("Họ tên không được để trống", "FULLNAME_REQUIRED");
        }

        if (!StringUtils.hasText(request.getEmail())) {
            throw new ApiException("Email không được để trống", "EMAIL_REQUIRED");
        }

        if (!request.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new ApiException("Email không hợp lệ", "EMAIL_INVALID");
        }

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new ApiException("Email đã tồn tại", "EMAIL_EXISTS");
        }

        if (!StringUtils.hasText(request.getPassword())) {
            throw new ApiException("Mật khẩu không được để trống", "PASSWORD_REQUIRED");
        }

        if (request.getPassword().length() < 6) {
            throw new ApiException("Mật khẩu phải có ít nhất 6 ký tự", "PASSWORD_TOO_SHORT");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setHoTen(request.getHoTen());
        user.setPhone(request.getPhone());

        user.setRole("STAFF");
        user.setTrangThai(true);
        user.setNgayTao(new Date());

        return toResponse(userRepository.save(user));
    }

    @Override
    public void updateStatus(Integer userId, UpdateLockRequest request) {

        User targetUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getTrangThai() == null) {
            throw new RuntimeException("Trạng thái không hợp lệ");
        }

        if ("ADMIN".equals(targetUser.getRole())) {
            throw new RuntimeException("Không thể khóa ADMIN");
        }

        targetUser.setTrangThai(request.getTrangThai());
        userRepository.save(targetUser);
    }


    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    private UserResponse toResponse(User user) {
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setEmail(user.getEmail());
        res.setHoTen(user.getHoTen());
        res.setPhone(user.getPhone());
        res.setRole(user.getRole());
        res.setTrangThai(user.getTrangThai());
        res.setNgayTao(user.getNgayTao());
        return res;
    }

    @Override
    public UserDetailResponse getUserDetail(Integer userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Address address = addressRepository
                .findByUserId(userId)
                .orElse(null);

        return UserDetailResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .hoTen(user.getHoTen())
                .phone(user.getPhone())
                .role(user.getRole())
                .trangThai(user.getTrangThai())
                .ngayTao(user.getNgayTao())

                .quocGia(address != null ? address.getQuocGia() : "Việt Nam")
                .tinhThanh(address != null ? address.getTinhThanh() : "")
                .quanHuyen(address != null ? address.getQuanHuyen() : "")
                .phuongXa(address != null ? address.getPhuongXa() : "")
                .diaChi(address != null ? address.getDiaChi() : "")
                .build();
    }

    @Override
    @Transactional
    public User updateProfile(UpdateProfileRequest request) {

        User user = getCurrentUser();
        if (user == null) {
            throw new RuntimeException("Chưa đăng nhập");
        }

        user.setHoTen(request.getHoTen());
        user.setPhone(request.getPhone());

        userRepository.save(user);

        Address address = addressRepository
                .findByUserId(user.getId())
                .orElseGet(() -> {
                    Address newAddr = new Address();
                    newAddr.setUser(user);
                    return newAddr;
                });

        address.setQuocGia(request.getQuocGia());
        address.setTinhThanh(request.getTinhThanh());
        address.setQuanHuyen(request.getQuanHuyen());
        address.setPhuongXa(request.getPhuongXa());
        address.setDiaChi(request.getDiaChi());

        addressRepository.save(address);

        return user;
    }

    @Override
    public List<UserResponse> searchUsers(String keyword, String role, String status) {

        List<User> users = userRepository.findAll();

        return users.stream()
                .filter(user -> {
                    if (!StringUtils.hasText(keyword)) return true;

                    String kw = keyword.toLowerCase();
                    return (user.getHoTen() != null && user.getHoTen().toLowerCase().contains(kw))
                            || (user.getEmail() != null && user.getEmail().toLowerCase().contains(kw))
                            || (user.getPhone() != null && user.getPhone().contains(kw));
                })

                .filter(user -> {
                    if (!StringUtils.hasText(role)) return true;
                    return role.equalsIgnoreCase(user.getRole());
                })

                .filter(user -> {
                    if (!StringUtils.hasText(status)) return true;

                    if ("active".equalsIgnoreCase(status)) {
                        return Boolean.TRUE.equals(user.getTrangThai());
                    }
                    if ("locked".equalsIgnoreCase(status)) {
                        return Boolean.FALSE.equals(user.getTrangThai());
                    }
                    return true;
                })

                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void changePassword(ChangePasswordRequest request) {

        User user = getCurrentUser();
        if (user == null) {
            throw new ApiException("Chưa đăng nhập", "UNAUTHORIZED");
        }

        // validate input
        if (!StringUtils.hasText(request.getOldPassword())) {
            throw new ApiException("Mật khẩu cũ không được để trống", "OLD_PASSWORD_REQUIRED");
        }

        if (!StringUtils.hasText(request.getNewPassword())) {
            throw new ApiException("Mật khẩu mới không được để trống", "NEW_PASSWORD_REQUIRED");
        }

        if (request.getNewPassword().length() < 6) {
            throw new ApiException("Mật khẩu mới phải có ít nhất 6 ký tự", "PASSWORD_TOO_SHORT");
        }

        // check mật khẩu cũ
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new ApiException("Mật khẩu cũ không đúng", "OLD_PASSWORD_INCORRECT");
        }

        // không cho trùng mật khẩu cũ
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new ApiException("Mật khẩu mới không được trùng mật khẩu cũ", "PASSWORD_DUPLICATE");
        }

        // update
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }
}
