package com.project.flashcardApp.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.flashcardApp.dtos.UserDto;
import com.project.flashcardApp.entities.EmailConfirmationToken;
import com.project.flashcardApp.entities.RefreshToken;
import com.project.flashcardApp.entities.User;
import com.project.flashcardApp.exceptions.ConfirmationTokenExpiredException;
import com.project.flashcardApp.exceptions.EmailAlreadyConfirmedException;
import com.project.flashcardApp.exceptions.EmailAlreadyInUseException;
import com.project.flashcardApp.exceptions.EmailInvalidException;
import com.project.flashcardApp.exceptions.InvalidPasswordException;
import com.project.flashcardApp.exceptions.RefreshTokenInvalidException;
import com.project.flashcardApp.exceptions.RefreshTokenValidException;
import com.project.flashcardApp.exceptions.UsernameAlreadyInUseException;
import com.project.flashcardApp.requests.RefreshTokenRequest;
import com.project.flashcardApp.requests.UserLoginRequest;
import com.project.flashcardApp.requests.UserRegisterConfirmRequest;
import com.project.flashcardApp.requests.UserRegisterRequest;
import com.project.flashcardApp.responses.AuthResponse;
import com.project.flashcardApp.security.JwtTokenProvider;
import com.project.flashcardApp.utilities.registration.EmailSender;
import com.project.flashcardApp.utilities.registration.EmailValidator;

class AuthServiceTest {
	
	private AuthService authService;
	
	private AuthenticationManager authenticationManager;
	private JwtTokenProvider jwtTokenProvider;
	private PasswordEncoder passwordEncoder;
	private UserService userService;
	private RefreshTokenService refreshTokenService;
	private EmailValidator emailValidator;
	private EmailConfirmationTokenService emailConfirmationTokenService;
	private ForgottenPasswordChangeTokenService forgottenPasswordChangeTokenService;
	private EmailSender emailSender;

	@BeforeEach
	void setUp() throws Exception {
		authenticationManager = Mockito.mock(AuthenticationManager.class);
		jwtTokenProvider = Mockito.mock(JwtTokenProvider.class);
		passwordEncoder = Mockito.mock(PasswordEncoder.class);
		userService = Mockito.mock(UserService.class);
		refreshTokenService = Mockito.mock(RefreshTokenService.class);
		emailValidator = Mockito.mock(EmailValidator.class);
		emailConfirmationTokenService = Mockito.mock(EmailConfirmationTokenService.class);
		forgottenPasswordChangeTokenService = Mockito.mock(ForgottenPasswordChangeTokenService.class);
		emailSender = Mockito.mock(EmailSender.class);
		
		authService = new AuthService(authenticationManager, jwtTokenProvider, passwordEncoder, userService, 
				refreshTokenService, emailValidator, emailConfirmationTokenService, forgottenPasswordChangeTokenService, emailSender);	
	}

	@Test
	void testLogin_whenCalledWithValidRequest_thenShouldReturnValidAuthResponse() {
		//Data for testing
		UserLoginRequest loginRequest = new UserLoginRequest("test-username","test-password");
		User user = new User(1L, "test-username", "test-email", true, "test-password", 0);
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				loginRequest.getUsername(), loginRequest.getPassword());
		Authentication auth = authenticationManager.authenticate(authToken);
		SecurityContextHolder.getContext().setAuthentication(auth);
		String jwtToken = jwtTokenProvider.generateJwtToken(auth);
		
		AuthResponse authResponse = new AuthResponse();
		authResponse.setMessage("Successfully logged in.");
		authResponse.setJwtAccessToken("Bearer " + jwtToken);
		authResponse.setUserId(user.getId());
		authResponse.setJwtRefreshToken(refreshTokenService.createRefreshToken(user));
		
		//Test case
		Mockito.when(userService.getOneUserByUsername("test-username")).thenReturn(user);
		Mockito.when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);
		
		//Verify
		AuthResponse result = authService.login(loginRequest);
		
		assertEquals(result, authResponse);
		Mockito.verify(userService).getOneUserByUsername("test-username");
		Mockito.verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPassword());
	}
	
	@Test
	void testLogin_whenCalledWithWrongPassword_thenShouldThrowInvalidPasswordException() {
		//Data for testing
		UserLoginRequest loginRequest = new UserLoginRequest("test-username","test-wrong-password");
		User user = new User("test-username", "test-email", true, "test-password", 0);
		
		//Test case
		Mockito.when(userService.getOneUserByUsername("test-username")).thenReturn(user);
		Mockito.when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);
		
		//Verify
		assertThrows(InvalidPasswordException.class, () -> {
			authService.login(loginRequest);
		});

		Mockito.verify(userService).getOneUserByUsername("test-username");
		Mockito.verify(passwordEncoder).matches(loginRequest.getPassword(), user.getPassword());
	}
	
	@Test
	void testRegister_whenCalledWithValidRequest_thenShouldReturnValidAuthResponse() {
		//Data for testing
		UserRegisterRequest registerRequest = new UserRegisterRequest("test-email@email.com","test-username","test-password");
		User user = new User("test-username", "test-email@email.com", false, passwordEncoder.encode("test-password"), 0);
		UserDto userDto = new UserDto(1L,"test-username", "test-email@email.com", false, passwordEncoder.encode("test-password"), 0);
		String token = "123456";
		
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime currentTimePlus15Minutes = LocalDateTime.now().plusMinutes(15);
		
		EmailConfirmationToken confirmationToken = new EmailConfirmationToken(user, "123456", currentTime, currentTimePlus15Minutes);
		AuthResponse authResponse = new AuthResponse();
		authResponse.setMessage("Email confirmation code is sent.");
		
		//Test case
		Mockito.when(userService.userExistsByEmail(registerRequest.getEmail())).thenReturn(false);
		Mockito.when(userService.userExistsByUsername(registerRequest.getUsername())).thenReturn(false);
		Mockito.when(emailValidator.test(registerRequest.getEmail())).thenReturn(true);
		Mockito.when(userService.saveOneUser(user)).thenReturn(userDto);
		Mockito.when(emailConfirmationTokenService.saveConfirmationToken(confirmationToken)).thenReturn(confirmationToken);

		//Verify
		AuthResponse result = authService.register(registerRequest);
		
		assertEquals(result, authResponse);
		assertEquals(confirmationToken.getToken(), token);
		Mockito.verify(userService).userExistsByEmail(registerRequest.getEmail());
		Mockito.verify(userService).userExistsByUsername(registerRequest.getUsername());
		Mockito.verify(emailValidator).test(registerRequest.getEmail());
		
		ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);
		ArgumentCaptor<EmailConfirmationToken> tokenArgumentCaptor = ArgumentCaptor.forClass(EmailConfirmationToken.class);
		
		Mockito.verify(userService).saveOneUser(userArgumentCaptor.capture());
		Mockito.verify(emailConfirmationTokenService).saveConfirmationToken(tokenArgumentCaptor.capture());
	}
	
	@Test
	void testRegister_whenCalledWithExistingEmail_thenShouldThrowEmailAlreadyInUseException() {
		//Data for testing
		UserRegisterRequest registerRequest = new UserRegisterRequest("test-email@email.com","test-username","test-password");
		
		//Test case
		Mockito.when(userService.userExistsByEmail(registerRequest.getEmail())).thenReturn(true);

		//Verify
		assertThrows(EmailAlreadyInUseException.class, () -> {
			authService.register(registerRequest);
		});
		
		Mockito.verify(userService).userExistsByEmail(registerRequest.getEmail());
	}
	
	@Test
	void testRegister_whenCalledWithExistingUsername_thenShouldThrowUsernameAlreadyInUseException() {
		//Data for testing
		UserRegisterRequest registerRequest = new UserRegisterRequest("test-email@email.com","test-username","test-password");
		
		//Test case
		Mockito.when(userService.userExistsByEmail(registerRequest.getEmail())).thenReturn(false);
		Mockito.when(userService.userExistsByUsername(registerRequest.getUsername())).thenReturn(true);
		
		//Verify
		assertThrows(UsernameAlreadyInUseException.class, () -> {
			authService.register(registerRequest);
		});
		
		Mockito.verify(userService).userExistsByEmail(registerRequest.getEmail());
		Mockito.verify(userService).userExistsByUsername(registerRequest.getUsername());
	}
	
	@Test
	void testRegister_whenCalledWithInvalidEmail_thenShouldThrowEmailInvalidException() {
		//Data for testing
		UserRegisterRequest registerRequest = new UserRegisterRequest("test-email@email.com","test-username","test-password");
		
		//Test case
		Mockito.when(userService.userExistsByEmail(registerRequest.getEmail())).thenReturn(false);
		Mockito.when(userService.userExistsByUsername(registerRequest.getUsername())).thenReturn(false);
		Mockito.when(emailValidator.test(registerRequest.getEmail())).thenReturn(false);

		//Verify
		assertThrows(EmailInvalidException.class, () -> {
			authService.register(registerRequest);
		});
		
		Mockito.verify(userService).userExistsByEmail(registerRequest.getEmail());
		Mockito.verify(userService).userExistsByUsername(registerRequest.getUsername());
		Mockito.verify(emailValidator).test(registerRequest.getEmail());
	}
	
	@Test
	void testConfirmEmailToken_whenCalledWithValidRequest_thenShouldReturnValidAuthResponse() {
		//Data for testing
		UserRegisterConfirmRequest userRegisterConfirmRequest = new UserRegisterConfirmRequest("123456", "test-password");
		String token = "123456";
		User user = new User(1L, "test-username", "test-email@email.com", false, passwordEncoder.encode("test-password"), 0);
	
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime currentTimePlus15Minutes = LocalDateTime.now().plusMinutes(15);
		
		EmailConfirmationToken confirmationToken = new EmailConfirmationToken(1L, user, token, currentTime, currentTimePlus15Minutes);
		
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
				user.getUsername(), userRegisterConfirmRequest.getPassword());
		Authentication auth = authenticationManager.authenticate(authToken);
		SecurityContextHolder.getContext().setAuthentication(auth);
		String jwtToken = jwtTokenProvider.generateJwtToken(auth);
			
		AuthResponse authResponse = new AuthResponse();
		authResponse.setMessage("Successfully registered.");
		authResponse.setJwtAccessToken("Bearer " + jwtToken);
		authResponse.setUserId(user.getId());
		authResponse.setJwtRefreshToken(refreshTokenService.createRefreshToken(user));
		
		//Test case
		Mockito.when(emailConfirmationTokenService.getConfirmationToken(token)).thenReturn(confirmationToken);

		//Verify
		AuthResponse result = authService.confirmEmailToken(userRegisterConfirmRequest);
		
		assertEquals(result, authResponse);
		
		Mockito.verify(emailConfirmationTokenService).getConfirmationToken(token);
		Mockito.verify(userService).enableUser(user.getEmail());;
		
	}
	
	@Test
	void testConfirmEmailToken_whenCalledWithConfirmedToken_thenShouldThrowEmailAlreadyConfirmedException() {
		//Data for testing
		UserRegisterConfirmRequest userRegisterConfirmRequest = new UserRegisterConfirmRequest("123456", "test-password");
		String token = "123456";
		User user = new User(1L, "test-username", "test-email@email.com", false, passwordEncoder.encode("test-password"), 0);
	
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime currentTimePlus15Minutes = LocalDateTime.now().plusMinutes(15);
		LocalDateTime currentTimePlus10Minutes = LocalDateTime.now().plusMinutes(10);
		
		EmailConfirmationToken confirmationToken = new EmailConfirmationToken(1L, user, token, currentTime, currentTimePlus15Minutes);
		confirmationToken.setConfirmedAt(currentTimePlus10Minutes);

		//Test case
		Mockito.when(emailConfirmationTokenService.getConfirmationToken(token)).thenReturn(confirmationToken);

		//Verify
		assertThrows(EmailAlreadyConfirmedException.class, () -> {
			authService.confirmEmailToken(userRegisterConfirmRequest);
		});
		
		Mockito.verify(emailConfirmationTokenService).getConfirmationToken(token);
	}
	
	@Test
	void testConfirmEmailToken_whenCalledWithExpiredToken_thenShouldThrowConfirmationTokenExpiredException() {
		//Data for testing
		UserRegisterConfirmRequest userRegisterConfirmRequest = new UserRegisterConfirmRequest("123456", "test-password");
		String token = "123456";
		User user = new User(1L, "test-username", "test-email@email.com", false, passwordEncoder.encode("test-password"), 0);
	
		LocalDateTime currentTime = LocalDateTime.now();
		LocalDateTime currentTimePlus15Minutes = LocalDateTime.now().plusMinutes(15);
		
		EmailConfirmationToken confirmationToken = new EmailConfirmationToken(1L, user, token, currentTime, currentTimePlus15Minutes);

		//Test case
		Mockito.when(emailConfirmationTokenService.getConfirmationToken(token)).thenReturn(confirmationToken);
		Mockito.when(emailConfirmationTokenService.isTokenExpired(token)).thenReturn(true);

		//Verify
		assertThrows(ConfirmationTokenExpiredException.class, () -> {
			authService.confirmEmailToken(userRegisterConfirmRequest);
		});
		
		Mockito.verify(emailConfirmationTokenService).getConfirmationToken(token);
		Mockito.verify(userService).deleteOneUserById(confirmationToken.getUser().getId());
		Mockito.verify(emailConfirmationTokenService).deleteConfirmationToken(token);
	}
	
	@Test
	void testRefreshToken_whenCalledWithValidRequest_thenShouldReturnValidAuthResponse() {
		//Data for testing
		String token = UUID.randomUUID().toString();
		RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(1L, token);
		Long refreshTokenExpiresIn = 604800L;
		
		User user = new User(1L, "test-username", "test-email@email.com", false, passwordEncoder.encode("test-password"), 0);
		RefreshToken refreshToken = new RefreshToken(user, token, 
				Date.from(Instant.now().plusSeconds(refreshTokenExpiresIn)));
		String jwtToken = jwtTokenProvider.generateJwtTokenFromRefreshByUserId(user.getId());
		
		AuthResponse authResponse = new AuthResponse();
		authResponse.setMessage("Refresh token updated.");
		authResponse.setJwtAccessToken("Bearer " + jwtToken);
		authResponse.setUserId(user.getId());
		authResponse.setJwtRefreshToken(refreshTokenService.createRefreshToken(user));

		//Test case
		Mockito.when(refreshTokenService.getByUserId(refreshTokenRequest.getUserId())).thenReturn(refreshToken);
		Mockito.when(refreshTokenService.isRefreshTokenExpired(refreshToken)).thenReturn(true);
		

		//Verify
		AuthResponse result = authService.refreshToken(refreshTokenRequest);
		
		assertEquals(result, authResponse);
		
		Mockito.verify(refreshTokenService).getByUserId(refreshTokenRequest.getUserId());
		Mockito.verify(refreshTokenService).isRefreshTokenExpired(refreshToken);
	}
	
	@Test
	void testRefreshToken_whenCalledWithInvalidRefreshToken_thenShouldThrowRefreshTokenInvalidException() {
		//Data for testing
		String token = UUID.randomUUID().toString();
		RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(1L, token);
		Long refreshTokenExpiresIn = 604800L;
		
		User user = new User(1L, "test-username", "test-email@email.com", false, passwordEncoder.encode("test-password"), 0);
		
		String anotherToken = UUID.randomUUID().toString();
		RefreshToken refreshToken = new RefreshToken(user, anotherToken, 
				Date.from(Instant.now().plusSeconds(refreshTokenExpiresIn)));

		//Test case
		Mockito.when(refreshTokenService.getByUserId(refreshTokenRequest.getUserId())).thenReturn(refreshToken);
		Mockito.when(refreshTokenService.isRefreshTokenExpired(refreshToken)).thenReturn(false);

		//Verify
		assertThrows(RefreshTokenInvalidException.class, () -> {
			authService.refreshToken(refreshTokenRequest);
		});
		
		Mockito.verify(refreshTokenService).getByUserId(refreshTokenRequest.getUserId());
	}
	
	@Test
	void testRefreshToken_whenCalledWithNonexpiredRefreshToken_thenShouldThrowRefreshTokenValidException() {
		//Data for testing
		String token = UUID.randomUUID().toString();
		RefreshTokenRequest refreshTokenRequest = new RefreshTokenRequest(1L, token);
		Long refreshTokenExpiresIn = 604800L;
		
		User user = new User(1L, "test-username", "test-email@email.com", false, passwordEncoder.encode("test-password"), 0);
		
		RefreshToken refreshToken = new RefreshToken(user, token, 
				Date.from(Instant.now().plusSeconds(refreshTokenExpiresIn)));

		//Test case
		Mockito.when(refreshTokenService.getByUserId(refreshTokenRequest.getUserId())).thenReturn(refreshToken);
		Mockito.when(refreshTokenService.isRefreshTokenExpired(refreshToken)).thenReturn(false);

		//Verify
		assertThrows(RefreshTokenValidException.class, () -> {
			authService.refreshToken(refreshTokenRequest);
		});
		
		Mockito.verify(refreshTokenService).getByUserId(refreshTokenRequest.getUserId());
		Mockito.verify(refreshTokenService).isRefreshTokenExpired(refreshToken);
	}

}
