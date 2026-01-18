package com.example.web_doushijin.controller;

import com.example.web_doushijin.entity.User;
import com.example.web_doushijin.repository.UserRepository;
import com.example.web_doushijin.service.UserService;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin("*")
public class UserController {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private UserService userService;
    // API để lấy thông tin ví tiền của người dùng
    // URL: http://localhost:8081/api/users/1
    @GetMapping("/{id}")
    public User getUserInfo(@PathVariable Long id) {
        return userRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Người dùng " + id + " chưa nạp vào kho Oracle!"));
    }
 // Trong UserController.java

    @PostMapping("/login")
    public User handleLogin(@RequestBody Map<String, String> credentials) {
        // Lúc này userService sẽ không còn null nữa
        return userService.login(credentials.get("username"), credentials.get("password"));
    }
    @PostMapping("/{id}/deposit")
    public ResponseEntity<String> deposit(@PathVariable Long id, @RequestParam Double amount) {
        userService.deposit(id, amount); // Gọi hàm deposit của UserService mà bạn đã viết tối qua
        return ResponseEntity.ok("Vàng đã về kho!");
    }
    
    @PostMapping("/register") // <--- CHẮC CHẮN PHẢI LÀ POST
    public User handleRegister(@RequestBody User user) {
        // Gọi sang service xử lý lưu Oracle
        return userService.register(user.getUsername(), user.getPassword());
    }

}