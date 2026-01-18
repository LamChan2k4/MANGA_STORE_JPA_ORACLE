package com.example.web_doushijin.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.web_doushijin.entity.Book;
import com.example.web_doushijin.entity.Page;
import com.example.web_doushijin.entity.Chapter;
import com.example.web_doushijin.repository.BookRepository;
import com.example.web_doushijin.repository.PageRepository;
import com.example.web_doushijin.repository.PurchaseRepository;
import com.example.web_doushijin.service.BookService;
import com.example.web_doushijin.service.PageService;


@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*") 
public class BookController {
	@Autowired
	private BookService bookService;
	@Autowired 
	private PageService pageService;
	@Autowired 
	private PurchaseRepository purchaseRepo;
	@GetMapping 
    public List<Book> listAllManga() {
        return bookService.tatCaTruyen();
    }
	// Trong Controller
	@PostMapping("/upload-manga")
	public ResponseEntity<String> upload(@RequestParam("file") MultipartFile file, @RequestParam Long bookId) {
	    try {
	        pageService.splitAndSave(file, bookId);
	        return ResponseEntity.ok("Thành công rực rỡ! Em gái vào kiểm tra đi.");
	    } catch (Exception e) {
	        return ResponseEntity.status(500).body("Máy xẻ ảnh bị hỏng rồi: " + e.getMessage());
	    }
	}
	@GetMapping("/{id}")
	public Book getBookDetail(@PathVariable Long id, @RequestParam(required = false) Long userId) {
	    Book book = bookService.timTheoId(id).get();
	    
	    // Nếu web gửi kèm ID người dùng, ta đi soi từng chương
	    if (userId != null) {
	        for (Chapter chap : book.getChapters()) {
	            boolean isBought = purchaseRepo.existsByUserIdAndChapterId(userId, chap.getId());
	            chap.setBought(isBought);
	        }
	    }
	    return book;
	 }
	@PostMapping
	public Book addNewBook(@RequestBody Book newBook) {
		return bookService.luuTruyen(newBook);
	}
	
	@DeleteMapping("/{id}")
	public void deleteBook(@PathVariable Long id) {
		bookService.xoaTruyen(id);
	}
	@PutMapping("/{id}")
	public Book updateBook(@PathVariable Long id, @RequestBody Book bookDetails) {
	    // Tìm quyển sách cũ trong kho xem có không
	    return bookService.timTheoId(id).map(book -> {
	        // Nếu có, thay đồ mới cho nó
	        book.setTitle(bookDetails.getTitle());
	        book.setPrice(bookDetails.getPrice());
	        book.setAuthor(bookDetails.getAuthor());
	        book.setImageUrl(bookDetails.getImageUrl());
	        book.setStockQuantity(bookDetails.getStockQuantity());
	        // Lưu lại vào kho
	        return bookService.luuTruyen(book);
	    }).orElseThrow(() -> new RuntimeException("Không tìm thấy truyện ID: " + id));
	}
}
