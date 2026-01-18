package com.example.web_doushijin.service;

import com.example.web_doushijin.entity.Book;
import com.example.web_doushijin.entity.Chapter;
import com.example.web_doushijin.entity.Page;
import com.example.web_doushijin.repository.BookRepository;
import com.example.web_doushijin.repository.ChapterRepository;
import com.example.web_doushijin.repository.PageRepository;
import com.example.web_doushijin.repository.PurchaseRepository;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.imageio.ImageIO;

@Service
public class PageService {
    @Autowired private PageRepository pageRepo;
    @Autowired private ChapterRepository chapterRepo;
    @Autowired private BookRepository bookRepo;
    // ĐÂY LÀ HÀM BẠN ĐANG THIẾU
    public List<Page> getPagesByChapter(Long chapterId) {
        // Gọi xuống repo để lấy dữ liệu thật từ Oracle
        return pageRepo.findByChapterIdOrderByPageOrderAsc(chapterId);
    }
    // Đường dẫn tuyệt đối nơi lưu ảnh trên ổ cứng
    private final String UPLOAD_DIR = "manga_storage/";

    @Transactional // <--- Quan trọng: Để đảm bảo hỏng nửa chừng thì DB ko bị rác
    public void splitAndSave(MultipartFile pdfFile, Long bookId) throws IOException {
        
        // 1. Tìm bộ truyện mẹ
        Book book = bookRepo.findById(bookId).orElseThrow();

        // 2. Tạo nhanh 1 Chapter mới mang tên file PDF (Cho em gái dễ quản lý)
        Chapter newChap = new Chapter();
        newChap.setBook(book);
        newChap.setTitle("Bộ nạp: " + pdfFile.getOriginalFilename());
        newChap.setPrice(15000.0); // Tạm đặt giá bán
        newChap = chapterRepo.save(newChap);

        // 3. Chuẩn bị "ổ đẻ" (Tạo thư mục lưu ảnh)
        File folder = new File(UPLOAD_DIR + newChap.getId());
        if (!folder.exists()) folder.mkdirs();

        // 4. MỔ PDF (MA THUẬT PDFBOX TẠI ĐÂY)
        try (PDDocument document = PDDocument.load(pdfFile.getInputStream())) {
            PDFRenderer renderer = new PDFRenderer(document);
            
            for (int i = 0; i < document.getNumberOfPages(); i++) {
                // Biến trang i thành ảnh
                BufferedImage img = renderer.renderImageWithDPI(i, 150); // 150 là đủ đẹp
                
                String fileName = "p" + (i + 1) + ".jpg";
                File outputfile = new File(folder, fileName);
                
                // GHI ẢNH VÀO Ổ CỨNG
                ImageIO.write(img, "jpg", outputfile);

                // 5. GHI DÒNG DỮ LIỆU VÀO ORACLE
                Page page = new Page();
                page.setChapter(newChap);
                page.setPage_order(i + 1);
                // URL này tí nữa Web sẽ gọi
                page.setImageUrl("/images/" + newChap.getId() + "/" + fileName);
                pageRepo.save(page);
            }
        }
    }
}