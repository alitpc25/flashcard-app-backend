package com.project.flashcardApp.services;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.project.flashcardApp.entities.RefreshToken;
import com.project.flashcardApp.entities.User;
import com.project.flashcardApp.repository.RefreshTokenRepository;

@Service
public class RefreshTokenService {
	private final RefreshTokenRepository refreshTokenRepository;
	
	// Seconds
	private Long refreshTokenExpiresIn = 604800L;

	public RefreshTokenService(RefreshTokenRepository refreshTokenRepository) {
		this.refreshTokenRepository = refreshTokenRepository;
	}
	
	// TODO: Test ederken expiryDate zamanı ve token, randomUUID'si old. için nasıl test edilebilir, ben sildim.
	// TODO: Bunun aynısı kullanım Message ve Essay entitylerinde de var zaman için LocalDateTime.now() sildim.
	// TODO: Ayrıca application.properties içindeki value okunamıyor, neden?
	public String createRefreshToken(User user) {
		RefreshToken refreshToken = new RefreshToken(user, UUID.randomUUID().toString(), 
				Date.from(Instant.now().plusSeconds(refreshTokenExpiresIn)));
		refreshTokenRepository.save(refreshToken);
		return refreshToken.getToken();
	}
	
	public boolean isRefreshTokenExpired(RefreshToken refreshToken) {
		return refreshToken.getExpiryDate().before(new Date());
	}

	public RefreshToken getByUserId(Long userId) {
		return refreshTokenRepository.findByUserId(userId);
	}
	
}
