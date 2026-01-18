package com.example.web_doushijin.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.web_doushijin.entity.Book;
import com.example.web_doushijin.repository.BookRepository;


@Service
public class BookService {
	
	@Autowired
	private BookRepository bookRepo;
	
	public List<Book> getAllBooks(){
		return bookRepo.findAll();
	}
	
	public List<Book> filterByGenres(List<Long> genreIds){
		if (genreIds == null || genreIds.isEmpty()) {
            return bookRepo.findAll();
        }
        
        return bookRepo.findByMultipleGenres(genreIds, (long) genreIds.size());
	}
	public List<Book> tatCaTruyen() {
        return bookRepo.findAll();
    }

    public Book luuTruyen(Book b) {
        return bookRepo.save(b);
    }
    

    public void xoaTruyen(Long id) {
        bookRepo.deleteById(id);
    }
    
    @org.springframework.transaction.annotation.Transactional 
    public Optional<Book> timTheoId(Long id) {
        return bookRepo.findById(id);
    }
}
