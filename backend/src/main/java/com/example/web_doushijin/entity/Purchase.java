package com.example.web_doushijin.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime; 
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "PURCHASES")
public class Purchase {
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
	
	@ManyToOne
    @JoinColumn(name = "USER_ID")
    private User user;

	@ManyToOne
    @JoinColumn(name = "BOOK_ID") // Nối đến bộ truyện
    private Book book;
	
    @ManyToOne
    @JoinColumn(name = "CHAPTER_ID")
    private Chapter chapter;
	
	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public Purchase(Long id, User user, Book book, Chapter chapter, LocalDateTime purchaseAt) {
		super();
		this.id = id;
		this.user = user;
		this.book = book;
		this.chapter = chapter;
		this.purchaseAt = purchaseAt;
	}

	@Column(name="PURCHASE_AT", nullable=false,updatable=false)
	@CreationTimestamp
	private LocalDateTime purchaseAt;
	
	public Purchase() {}

	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Chapter getChapter() {
		return chapter;
	}

	public void setChapter(Chapter chapter) {
		this.chapter = chapter;
	}

	public LocalDateTime getPurchaseAt() {
		return purchaseAt;
	}

	public void setPurchaseAt(LocalDateTime purchaseAt) {
		this.purchaseAt = purchaseAt;
	}
	
}
