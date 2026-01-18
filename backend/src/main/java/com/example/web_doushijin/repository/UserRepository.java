package com.example.web_doushijin.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.web_doushijin.entity.User;

public interface UserRepository extends JpaRepository<User, Long>{
	// Tự sinh SQL: SELECT * FROM USERS WHERE USERNAME = ?
    Optional<User> findByUsername(String username);
    
    // Tự sinh SQL: SELECT COUNT(*) > 0 FROM USERS WHERE USERNAME = ?
    boolean existsByUsername(String username);
}
