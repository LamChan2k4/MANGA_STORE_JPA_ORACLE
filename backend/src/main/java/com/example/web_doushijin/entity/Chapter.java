package com.example.web_doushijin.entity;

import jakarta.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "CHAPTERS") 
public class Chapter {
	@Transient // Nhãn này bảo Hibernate: Đừng tìm cột này trong Oracle, đây là biến tạm thôi
	private boolean isBought = false;
	public boolean isBought() {
		return isBought;
	}

	public void setBought(boolean isBought) {
		this.isBought = isBought;
	}

	public Chapter(boolean isBought, Long id, Double price, Book book, String title, Integer chapter_index) {
		super();
		this.isBought = isBought;
		this.id = id;
		this.price = price;
		this.book = book;
		this.title = title;
		this.chapter_index = chapter_index;
	}

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	@Column(name="PRICE")
	private Double price;
	

	@ManyToOne
    @JoinColumn(name = "BOOK_ID")
	@com.fasterxml.jackson.annotation.JsonIgnore // <--- DÒNG CHỮ VÀNG CỨU RỖI CẢ DỰ ÁN Ở ĐÂY
    private Book book;
	
	@Column(name = "TITLE")
	private String title;
	
	@Column(name = "CHAPTER_INDEX")
	private Integer chapter_index;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Book getBook() {
		return book;
	}

	public void setBook(Book book) {
		this.book = book;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getChapter_index() {
		return chapter_index;
	}

	public void setChapter_index(Integer chapter_index) {
		this.chapter_index = chapter_index;
	}
	
	public Chapter() {
			
	}


	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

}
