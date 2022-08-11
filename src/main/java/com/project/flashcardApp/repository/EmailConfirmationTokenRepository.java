package com.project.flashcardApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.flashcardApp.entities.EmailConfirmationToken;

public interface EmailConfirmationTokenRepository extends JpaRepository<EmailConfirmationToken, Long> {

	Optional<EmailConfirmationToken> findByToken(String token);

	void deleteByToken(String token);
}
