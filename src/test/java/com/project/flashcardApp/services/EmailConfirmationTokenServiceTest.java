package com.project.flashcardApp.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.project.flashcardApp.entities.EmailConfirmationToken;
import com.project.flashcardApp.entities.User;
import com.project.flashcardApp.exceptions.ConfirmationTokenNotFoundException;
import com.project.flashcardApp.repository.EmailConfirmationTokenRepository;

class EmailConfirmationTokenServiceTest {
	
	private EmailConfirmationTokenRepository emailConfirmationTokenRepository;
	
	private EmailConfirmationTokenService emailConfirmationTokenService;

	@BeforeEach
	void setUp() throws Exception {
		emailConfirmationTokenRepository = Mockito.mock(EmailConfirmationTokenRepository.class);
		emailConfirmationTokenService = new EmailConfirmationTokenService(emailConfirmationTokenRepository);
	}

	@Test
	void testSaveConfirmationToken_whenCalledWithValidRequest_thenShouldReturnEmailConfirmationToken() {
		//Data for testing
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime currentTimePlus15Minutes = LocalDateTime.now().plusMinutes(15);
		
		EmailConfirmationToken confirmationToken = new EmailConfirmationToken(1L, user, "123456", currentTime, currentTimePlus15Minutes);
		
		//Test Case
		Mockito.when(emailConfirmationTokenRepository.save(confirmationToken)).thenReturn(confirmationToken);
		
		
		//Verify
		EmailConfirmationToken result = emailConfirmationTokenService.saveConfirmationToken(confirmationToken);
		assertEquals(result, confirmationToken);
	}
	
	@Test
	void testGetConfirmationToken_whenCalledWithValidRequest_thenShouldReturnEmailConfirmationToken() {
		//Data for testing
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime currentTimePlus15Minutes = LocalDateTime.now().plusMinutes(15);
		
		EmailConfirmationToken confirmationToken = new EmailConfirmationToken(1L, user, "123456", currentTime, currentTimePlus15Minutes);
		
		//Test Case
		Mockito.when(emailConfirmationTokenRepository.findByToken("123456")).thenReturn(Optional.of(confirmationToken));
		
		
		//Verify
		EmailConfirmationToken result = emailConfirmationTokenService.getConfirmationToken("123456");
		assertEquals(result, confirmationToken);
	}
	
	@Test
	void testGetConfirmationToken_whenCalledWithNonexistToken_thenShouldThrowConfirmationTokenNotFoundException() {
		//Data for testing
		
		
		//Test Case
		Mockito.when(emailConfirmationTokenRepository.findByToken("123456")).thenReturn(Optional.empty());
		
		
		//Verify
		assertThrows(ConfirmationTokenNotFoundException.class, () -> {
			emailConfirmationTokenService.getConfirmationToken("123456");
		});
		Mockito.verify(emailConfirmationTokenRepository).findByToken("123456");
	}

}
