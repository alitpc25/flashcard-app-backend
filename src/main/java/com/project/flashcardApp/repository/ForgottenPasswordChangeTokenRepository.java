package com.project.flashcardApp.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.flashcardApp.entities.ForgottenPasswordChangeToken;

public interface ForgottenPasswordChangeTokenRepository extends JpaRepository<ForgottenPasswordChangeToken, Long> {

	Optional<ForgottenPasswordChangeToken> findByToken(String token);

	void deleteByToken(String token);
}
