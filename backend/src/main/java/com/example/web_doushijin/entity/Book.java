package com.example.web_doushijin.entity;

//THAY ĐỔI TẠI ĐÂY: Dùng jakarta thay cho javax
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;

@Entity
@Table(name = "BOOKS") 
public class Book {
	@OneToMany(mappedBy = "book", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<Chapter> chapters;
	@Id 
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(name = "IMAGE_URL")
	private String imageUrl;

	@Column(name = "TITLE")
	private String title;

	@Column(name = "AUTHOR")
	private String author;
	
	@Column(name = "PRICE")
	private Double price;

	@Column(name = "STOCK_QUANTITY") // Thêm vào để đồng bộ
	private Integer stockQuantity;

	@Column(name = "BOOK_CONDITION") // Thêm vào để chuyên nghiệp
	private String bookCondition;
	
	@ManyToMany
    @JoinTable(
        name = "BOOK_GENRES", // Tên bảng trung gian
        // BẮT BUỘC phải thêm dấu { } ở đây
        joinColumns = { @JoinColumn(name = "BOOK_ID") }, 
        inverseJoinColumns = { @JoinColumn(name = "GENRE_ID") }
    )
	private List<Genre> genres;
	
	public Book(Long id,String imageUrl,String title,String author,Double price,Integer stockQuantity,String bookCondition) {
		this.id=id;
		this.imageUrl=imageUrl;
		this.author=author;
		this.bookCondition=bookCondition;
		this.price=price;
		this.title=title;
		this.stockQuantity=stockQuantity;
	}
	
	public Book() {}

	public List<Chapter> getChapters() {
        return chapters;
    }

    // Tiện tay thêm luôn hàm Set để dùng nếu cần
    public void setChapters(List<Chapter> chapters) {
        this.chapters = chapters;
    }

	
	public Long getId() {
		return id;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Integer getStockQuantity() {
		return stockQuantity;
	}

	public void setStockQuantity(Integer stockQuantity) {
		this.stockQuantity = stockQuantity;
	}

	public String getBookCondition() {
		return bookCondition;
	}

	public void setBookCondition(String bookCondition) {
		this.bookCondition = bookCondition;
	}
	
	
}
