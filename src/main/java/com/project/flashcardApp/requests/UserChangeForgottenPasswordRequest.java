package com.project.flashcardApp.requests;

public class UserChangeForgottenPasswordRequest {
	String email;
	String newPassword;
	String confirmPassword; 
	String token;
	
	public UserChangeForgottenPasswordRequest(String email, String newPassword, String confirmPassword, String token) {
		this.email = email;
		this.newPassword = newPassword;
		this.confirmPassword = confirmPassword;
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public String getConfirmPassword() {
		return confirmPassword;
	}

	public String getToken() {
		return token;
	}
	
}
