package com.project.flashcardApp.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.project.flashcardApp.entities.RefreshToken;
import com.project.flashcardApp.entities.User;
import com.project.flashcardApp.repository.RefreshTokenRepository;

class RefreshTokenServiceTest {
	
	private RefreshTokenRepository refreshTokenRepository;
	
	private RefreshTokenService refreshTokenService;
	
	private Long refreshTokenExpiresIn = 604800L;

	@BeforeEach
	void setUp() throws Exception {
		refreshTokenRepository = Mockito.mock(RefreshTokenRepository.class);
		
		refreshTokenService = new RefreshTokenService(refreshTokenRepository);
	}

	@Test
	void testCreateRefreshToken_whenCalledWithValidRequest_thenShouldReturnValidToken() {
		//Data for testing
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		Instant currentTime = Instant.now();
		String randomId = UUID.randomUUID().toString();
		RefreshToken refreshToken = new RefreshToken(user, randomId, Date.from(currentTime.plusSeconds(refreshTokenExpiresIn)));
		RefreshToken savedRefreshToken = new RefreshToken(1L, user, randomId, Date.from(currentTime.plusSeconds(refreshTokenExpiresIn)));
		
		
		//Test case
		Mockito.when(refreshTokenRepository.save(refreshToken)).thenReturn(savedRefreshToken);
		
		//Verify
		String result = refreshTokenService.createRefreshToken(user);
		assertEquals(user, savedRefreshToken.getUser());
		assertNotNull(result);
		
		Mockito.verify(refreshTokenRepository).save(refreshToken);
	}
	
	@Test
	void testIsRefreshTokenExpired_whenCalledWithExpiredRefreshToken_thenShouldReturnTrue() {
		//Data for testing
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		Instant currentTime = Instant.now();
		String randomId = UUID.randomUUID().toString();
		RefreshToken refreshToken = new RefreshToken(user, randomId, Date.from(currentTime.minusSeconds(refreshTokenExpiresIn)));
		
		//Test case
		
		//Verify
		boolean result = refreshTokenService.isRefreshTokenExpired(refreshToken);
		assertEquals(true, result);
	}
	
	@Test
	void testIsRefreshTokenExpired_whenCalledWithNonexpiredRefreshToken_thenShouldReturnFalse() {
		//Data for testing
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		Instant currentTime = Instant.now();
		String randomId = UUID.randomUUID().toString();
		RefreshToken refreshToken = new RefreshToken(user, randomId, Date.from(currentTime.plusSeconds(refreshTokenExpiresIn)));
		
		//Test case
		
		//Verify
		boolean result = refreshTokenService.isRefreshTokenExpired(refreshToken);
		assertEquals(false, result);
	}
	
	@Test
	void testGetByUserId_whenCalledWithValidRequest_thenShouldReturnValidRefreshToken() {
		//Data for testing
		Long userId = 1L;
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		Instant currentTime = Instant.now();
		String randomId = UUID.randomUUID().toString();
		RefreshToken refreshToken = new RefreshToken(user, randomId, Date.from(currentTime.plusSeconds(refreshTokenExpiresIn)));
		
		//Test case
		Mockito.when(refreshTokenRepository.findByUserId(userId)).thenReturn(refreshToken);
		
		//Verify
		RefreshToken result = refreshTokenService.getByUserId(userId);
		assertEquals(result, refreshToken);
		
		Mockito.verify(refreshTokenRepository).findByUserId(userId);
	}

}
