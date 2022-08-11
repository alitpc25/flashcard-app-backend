package com.project.flashcardApp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.project.flashcardApp.entities.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
	
	@Query(value="SELECT * FROM refresh_token ORDER BY expiry_date DESC LIMIT 1", nativeQuery = true)
	RefreshToken findByUserId(Long userId);

}
