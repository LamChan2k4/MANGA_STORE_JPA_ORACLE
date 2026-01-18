package com.example.web_doushijin.service;

import com.example.web_doushijin.entity.Book;
import com.example.web_doushijin.entity.Chapter;
import com.example.web_doushijin.entity.Page;
import com.example.web_doushijin.repository.BookRepository;
import com.example.web_doushijin.repository.ChapterRepository;
import com.example.web_doushijin.repository.PageRepository;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.destination.PDPageDestination;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class MangaUploadService {

    @Autowired private BookRepository bookRepo;
    @Autowired private ChapterRepository chapterRepo;
    @Autowired private PageRepository pageRepo;

    // Đường dẫn lưu ảnh trên Server (Tí nữa lên Ubuntu ta đổi đường dẫn sau)
    private final String UPLOAD_DIR = "manga_storage/";
    
    @Transactional
    public void processMangaPdf(MultipartFile file, Long bookId) throws IOException {
        Book book = bookRepo.findById(bookId).orElseThrow(() -> new RuntimeException("Truyện không tồn tại!"));

        // 1. Nạp file PDF bằng cơ chế đóng tự động (try-with-resources)
        try (PDDocument document = PDDocument.load(file.getInputStream())) {
            
            // 2. Lấy Mục lục (Bookmarks)
            PDDocumentOutline outline = document.getDocumentCatalog().getDocumentOutline();

            if (outline != null) {
                System.out.println(">>> 📁 PHÁT HIỆN MỤC LỤC: Bắt đầu chế độ Xẻ Đa Chương...");
                
                PDOutlineItem current = outline.getFirstChild();
                int index = 1;
                
                while (current != null) {
                    // Xác định trang bắt đầu
                    int startPage = getPageIndex(current, document);
                    
                    // Xác định trang kết thúc (trước khi chương tiếp theo bắt đầu)
                    int endPage = (current.getNextSibling() != null) 
                                   ? getPageIndex(current.getNextSibling(), document) - 1 
                                   : document.getNumberOfPages() - 1;

                    // Nếu bookmark bị lỗi dẫn đến start > end, bỏ qua chương này
                    if (startPage >= 0 && startPage <= endPage) {
                        System.out.println(">>> [Đang mổ] " + current.getTitle() + " (Trang " + (startPage + 1) + " đến " + (endPage + 1) + ")");
                        
                        // Bước A: Tạo Chapter mới vào Oracle
                        Chapter chap = createChapter(book, current.getTitle(), index++);
                        
                        // Bước B: Xẻ từng trang PDF trong khoảng [start, end] ra thành ảnh .jpg
                        extractImages(document, chap, startPage, endPage);
                    }
                    
                    current = current.getNextSibling();
                }
            } else {
                System.out.println(">>> 📄 KHÔNG CÓ MỤC LỤC: Chế độ Oneshot (Xẻ toàn bộ file)...");
                // Tự gán index = 1 và title là tên file cho truyện ngắn
                Chapter oneshotChap = createChapter(book, "Oneshot: " + file.getOriginalFilename(), 1);
                extractImages(document, oneshotChap, 0, document.getNumberOfPages() - 1);
            }
        }
    }
    private void extractImages(PDDocument doc, Chapter chap, int startPage, int endPage) throws IOException {
        PDFRenderer renderer = new PDFRenderer(doc);
        
        // Tạo đường dẫn: folder/{bookId}/{chapterId}/
        String folderPath = UPLOAD_DIR + chap.getBook().getId() + "/" + chap.getId() + "/";
        Files.createDirectories(Paths.get(folderPath));

        // Duyệt từ trang bắt đầu đến trang kết thúc của chương đó trong PDF
        for (int i = startPage; i <= endPage; i++) {
            BufferedImage img = renderer.renderImageWithDPI(i, 150);
            
            // Số thứ tự trang tính từ 1 (Dân DS gọi đây là normalization)
            int order = i - startPage + 1; 
            
            String fileName = "p_" + order + ".jpg";
            File outputFile = new File(folderPath + fileName);
            ImageIO.write(img, "jpg", outputFile);

            // Lưu vào bảng PAGES trong Oracle
            Page p = new Page();
            p.setChapter(chap);
            
            // CHỖ BẠN CẦN ĐÂY: Dùng đúng biến bạn khai báo trong Entity
            p.setPage_order(order); 
            
            p.setImageUrl("/images/" + chap.getBook().getId() + "/" + chap.getId() + "/" + fileName);
            pageRepo.save(p);
            
        }
    }
 // SỬA LẠI CHỮ KÝ HÀM: Nhận thêm 'document' làm tham số thứ 2
    private int getPageIndex(PDOutlineItem item, PDDocument document) throws IOException {
        
        // 1. Lấy điểm đến (destination) của Bookmark
        PDDestination dest = item.getDestination();
        
        // 2. Ép kiểu sang PDPageDestination (đích đến là trang cụ thể)
        if (dest instanceof PDPageDestination) {
            PDPage destinationPage = ((PDPageDestination) dest).getPage();
            
            if (destinationPage != null) {
                // QUAN TRỌNG: Phải tìm index trên đúng tài liệu đang mở
                return document.getPages().indexOf(destinationPage); 
            }
        }
        
        // Nếu bookmark ko trỏ đến trang (link web hoặc folder), trả về 0 để an toàn
        return 0; 
    }
    private Chapter createChapter(Book book, String title, int index) {
        Chapter chapter = new Chapter();
        chapter.setBook(book);
        chapter.setTitle(title);
        chapter.setChapter_index(index);
        chapter.setPrice(10000.0); // Mặc định giá 10k
        return chapterRepo.save(chapter); // Lưu xong Oracle sẽ nổ ra ID
    }
    
}