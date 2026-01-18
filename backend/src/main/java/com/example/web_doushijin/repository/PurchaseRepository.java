package com.example.web_doushijin.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.web_doushijin.entity.Chapter;
import com.example.web_doushijin.entity.Purchase;

public interface PurchaseRepository extends JpaRepository<Purchase, Long>{
	boolean existsByUserIdAndChapterId(Long userId, Long chapterId);
}
