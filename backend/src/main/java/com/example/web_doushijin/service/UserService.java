package com.example.web_doushijin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.web_doushijin.entity.User;
import com.example.web_doushijin.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
    @Autowired private UserRepository userRepo;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Transactional
    public User register(String username, String password) {
        if(userRepo.existsByUsername(username)) throw new RuntimeException("Tên này có người dùng rồi!");
        User u = new User();
        u.setUsername(username);
        u.setPassword(password); // Mã hóa pass
        u.setBalance(0.0); // Mặc định là sinh viên nghèo
        u.setRole("USER");
        return userRepo.save(u);
    }

    public User login(String username, String password) {
        User u = userRepo.findByUsername(username).orElseThrow(() -> new RuntimeException("User ko tồn tại"));
        if (password.equals(u.getPassword())) return u; // Kiểm tra pass đã băm
        throw new RuntimeException("Sai mật khẩu!");
    }
 // Trong UserController.java
    @Transactional
    public void deposit(Long userId, Double amount) {
        User u = userRepo.findById(userId).orElseThrow();
        u.setBalance(u.getBalance() + amount); // Logic cộng tiền nạp
        userRepo.save(u);
    }
    public User findById(Long id) {
        return userRepo.findById(id).orElse(null);
    }
}