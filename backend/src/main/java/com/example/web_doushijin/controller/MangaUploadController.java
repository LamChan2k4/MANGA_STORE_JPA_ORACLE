package com.example.web_doushijin.controller;

import com.example.web_doushijin.service.MangaUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.web_doushijin.service.MangaUploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*") // Cho phép Frontend gọi API
public class MangaUploadController {

    @Autowired
    private MangaUploadService uploadService;

    /**
     * Cửa sổ nhận file PDF và xẻ ảnh
     * Địa chỉ: POST http://localhost:8081/api/admin/upload-oneshot
     */
    @PostMapping("/upload-oneshot")
    public ResponseEntity<String> uploadPdf(
            @RequestParam("file") MultipartFile file,
            @RequestParam("bookId") Long bookId) {
        
        // 1. Kiểm tra file rỗng để tránh làm nặng server
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("Lỗi: Bạn chưa chọn file hoặc file rỗng!");
        }

        try {
            // 2. Gửi file cho 'Đầu bếp' xử lý
            // Chú ý: Tên hàm PHẢI khớp với trong MangaUploadService.java
            uploadService.processMangaPdf(file, bookId);
            
            return ResponseEntity.ok("Thành công! Đã nạp và xẻ ảnh cho file: " + file.getOriginalFilename());
            
        } catch (Exception e) {
            // Log lỗi ra màn hình đen để mình debug
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Lỗi xử lý hệ thống: " + e.getMessage());
        }
    }
    
}