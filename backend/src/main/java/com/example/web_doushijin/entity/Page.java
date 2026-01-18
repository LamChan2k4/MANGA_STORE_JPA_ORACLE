package com.example.web_doushijin.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "PAGES")
public class Page {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="CHAPTER_ID")
	@com.fasterxml.jackson.annotation.JsonIgnore
	private Chapter chapter;
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Chapter getChapter() {
		return chapter;
	}

	public void setChapter(Chapter chapter) {
		this.chapter = chapter;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public Integer getPage_order() {
		return pageOrder;
	}

	public void setPage_order(Integer pageOrder) {
		this.pageOrder = pageOrder;
	}
	public Page() {
		
	}

	public Page(Long id, Chapter chapter, String imageUrl, Integer pageOrder) {
		super();
		this.id = id;
		this.chapter = chapter;
		this.imageUrl = imageUrl;
		this.pageOrder = pageOrder;
	}

	@Column(name = "IMAGE_URL")
	private String imageUrl;
	
	@Column(name = "PAGE_ORDER")
	private Integer pageOrder;
}
