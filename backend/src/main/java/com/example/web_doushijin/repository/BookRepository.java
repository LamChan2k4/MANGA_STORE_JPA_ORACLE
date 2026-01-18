package com.example.web_doushijin.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.example.web_doushijin.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long>{
	@Query("SELECT b FROM Book b JOIN b.genres g WHERE g.id IN :genreIds "+"GROUP BY b.id HAVING COUNT(DISTINCT g.id) = :genreCount")
	    List<Book> findByMultipleGenres(@Param("genreIds") List<Long> genreIds, @Param("genreCount") Long genreCount);
	
}
