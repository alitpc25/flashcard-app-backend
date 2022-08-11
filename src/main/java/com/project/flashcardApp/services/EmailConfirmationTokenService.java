package com.project.flashcardApp.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.project.flashcardApp.entities.EmailConfirmationToken;
import com.project.flashcardApp.exceptions.ConfirmationTokenNotFoundException;
import com.project.flashcardApp.repository.EmailConfirmationTokenRepository;


@Service
public class EmailConfirmationTokenService {
	
	private final EmailConfirmationTokenRepository emailConfirmationTokenRepository;
	
	public EmailConfirmationTokenService(EmailConfirmationTokenRepository emailConfirmationTokenRepository) {
		this.emailConfirmationTokenRepository = emailConfirmationTokenRepository;
	}
	
	public EmailConfirmationToken saveConfirmationToken(EmailConfirmationToken token) {
		return emailConfirmationTokenRepository.save(token);
	}
	
	public EmailConfirmationToken getConfirmationToken(String token) {
		return emailConfirmationTokenRepository.findByToken(token).orElseThrow(() -> new ConfirmationTokenNotFoundException("Token is wrong."));
	}

	public void setTokenConfirmedAt(String token) {
		getConfirmationToken(token).setConfirmedAt(LocalDateTime.now());
	}
	
	public boolean isTokenExpired(String token) {
		return getConfirmationToken(token).getExpiresAt().isBefore(LocalDateTime.now());
	}
	
	public void deleteConfirmationToken(String token) {
		emailConfirmationTokenRepository.deleteByToken(token);
	}
	
}
