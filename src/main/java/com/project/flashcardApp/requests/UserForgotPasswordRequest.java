package com.project.flashcardApp.requests;

public class UserForgotPasswordRequest {
	private String email;

	public UserForgotPasswordRequest(String email) {
		this.email = email;
	}
	
	public UserForgotPasswordRequest() {}

	public String getEmail() {
		return email;
	}
	
}
