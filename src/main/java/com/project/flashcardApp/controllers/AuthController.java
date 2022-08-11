package com.project.flashcardApp.controllers;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.project.flashcardApp.requests.RefreshTokenRequest;
import com.project.flashcardApp.requests.UserChangeForgottenPasswordRequest;
import com.project.flashcardApp.requests.UserForgotPasswordRequest;
import com.project.flashcardApp.requests.UserLoginRequest;
import com.project.flashcardApp.requests.UserRegisterConfirmRequest;
import com.project.flashcardApp.requests.UserRegisterRequest;
import com.project.flashcardApp.responses.AuthResponse;
import com.project.flashcardApp.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {
	
	private final AuthService authService;

	public AuthController(AuthService authService) {
		this.authService = authService;
	}

	@PostMapping("/login")
	public ResponseEntity<AuthResponse> login(@RequestBody UserLoginRequest loginRequest) {
		return ResponseEntity.ok(authService.login(loginRequest));
	}
	
	@PostMapping("/login/forgotPassword")
	public ResponseEntity<AuthResponse> forgotPassword(@RequestBody UserForgotPasswordRequest userForgotPasswordRequest) {
		return ResponseEntity.ok(authService.forgotPassword(userForgotPasswordRequest));
	}
	
	@PostMapping("/login/changePassword")
	public ResponseEntity<AuthResponse> changePassword(@RequestBody UserChangeForgottenPasswordRequest userChangeForgottenPasswordRequest) {
		return ResponseEntity.ok(authService.changePassword(userChangeForgottenPasswordRequest));
	}

	@PostMapping("/register")
	public ResponseEntity<AuthResponse> register(@RequestBody UserRegisterRequest registerRequest) {
		return ResponseEntity.ok(authService.register(registerRequest));

	}
	
	@PostMapping("/register/confirm")
	public ResponseEntity<AuthResponse> confirmEmailToken(@RequestBody UserRegisterConfirmRequest userRegisterConfirmRequest) {
		return ResponseEntity.ok(authService.confirmEmailToken(userRegisterConfirmRequest));

	}
	
	@PostMapping("/refreshToken")
	public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
		return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest));
	}
	
	@GetMapping("/accessToken/{userId}")
	public ResponseEntity<AuthResponse> accessToken(@PathVariable Long userId) {
		return ResponseEntity.ok(authService.accessToken(userId));
	}

}
