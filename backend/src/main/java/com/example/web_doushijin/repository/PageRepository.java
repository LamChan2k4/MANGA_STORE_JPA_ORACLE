package com.example.web_doushijin.repository;

import com.example.web_doushijin.entity.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface PageRepository extends JpaRepository<Page, Long> {
    // Spring Data JPA tự hiểu: Tìm theo ChapterId và OrderBy PageOrder tăng dần (Ascending)
	List<Page> findByChapterIdOrderByPageOrderAsc(Long chapterId);
}