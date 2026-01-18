package com.example.web_doushijin.service;

import org.springframework.beans.factory.annotation.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import com.example.web_doushijin.entity.*;
import com.example.web_doushijin.repository.*;

@Service
public class PurchaseService {

	@Autowired private PurchaseRepository purchaseRepo;
    @Autowired private UserRepository userRepo;
    @Autowired private ChapterRepository chapterRepo;
    
    @Transactional
    public String buyChapter(Long userId, Long chapterId) {
    	if (purchaseRepo.existsByUserIdAndChapterId(userId, chapterId)) {
            return "Bạn đã sở hữu chương này, hãy vào đọc thôi!";
        }
    	User user = userRepo.findById(userId).get();
        Chapter chapter = chapterRepo.findById(chapterId).get();

        // 3. Logic ví tiền
        if (user.getBalance() < chapter.getPrice()) {
            throw new RuntimeException("Tiền đâu mà mua? Nạp thêm đi!");
        }

        // 4. TRỪ TIỀN
        user.setBalance(user.getBalance() - chapter.getPrice());
        userRepo.save(user);

        // 5. CẤP VÉ XEM (Lưu vào bảng Purchase)
        Purchase p = new Purchase(); // Dùng hàm khởi tạo rỗng
        p.setUser(user);
        p.setBook(chapter.getBook()); // Lấy luôn Book từ Chapter ra cho tiện
        p.setChapter(chapter);
        
        purchaseRepo.save(p);

        return "Giao dịch thành công!";
    }
    
}
