package com.project.flashcardApp.services;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.project.flashcardApp.entities.ForgottenPasswordChangeToken;
import com.project.flashcardApp.exceptions.ConfirmationTokenNotFoundException;
import com.project.flashcardApp.repository.ForgottenPasswordChangeTokenRepository;

@Service
public class ForgottenPasswordChangeTokenService {
	private final ForgottenPasswordChangeTokenRepository forgottenPasswordChangeTokenRepository;

	public ForgottenPasswordChangeTokenService(
			ForgottenPasswordChangeTokenRepository forgottenPasswordChangeTokenRepository) {
		this.forgottenPasswordChangeTokenRepository = forgottenPasswordChangeTokenRepository;
	}
	
	public ForgottenPasswordChangeToken saveConfirmationToken(ForgottenPasswordChangeToken token) {
		return forgottenPasswordChangeTokenRepository.save(token);
	}
	
	public ForgottenPasswordChangeToken getConfirmationToken(String token) {
		return forgottenPasswordChangeTokenRepository.findByToken(token).orElseThrow(() -> new ConfirmationTokenNotFoundException("Token is wrong."));
	}

	public void setTokenConfirmedAt(String token) {
		getConfirmationToken(token).setConfirmedAt(LocalDateTime.now());
	}
	
	public boolean isTokenExpired(String token) {
		return getConfirmationToken(token).getExpiresAt().isBefore(LocalDateTime.now());
	}
	
	public void deleteConfirmationToken(String token) {
		forgottenPasswordChangeTokenRepository.deleteByToken(token);
	}
}
