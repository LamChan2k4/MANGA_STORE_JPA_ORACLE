package com.example.web_doushijin.controller;

import com.example.web_doushijin.service.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase") // Cửa sổ giao dịch /api/purchase
@CrossOrigin("*")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService; // Gọi ông chuyên gia tính tiền

    // Khi khách mang "Thùng hàng" đến theo phương thức POST
    @PostMapping
    public String buyChapter(@RequestParam Long userId, @RequestParam Long chapterId) {
        // Gọi service xử lý trừ tiền trong DB
        return purchaseService.buyChapter(userId, chapterId);
    }
}