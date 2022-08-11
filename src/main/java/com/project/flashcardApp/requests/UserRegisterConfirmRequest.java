package com.project.flashcardApp.requests;

public class UserRegisterConfirmRequest {
	private String token;
	private String password;
	
	public UserRegisterConfirmRequest(String token, String password) {
		this.token = token;
		this.password = password;
	}
	
	public UserRegisterConfirmRequest() {}

	public String getToken() {
		return token;
	}

	public String getPassword() {
		return password;
	}
	
}
