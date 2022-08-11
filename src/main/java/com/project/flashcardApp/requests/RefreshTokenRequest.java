package com.project.flashcardApp.requests;

public class RefreshTokenRequest {
	private Long userId;
	private String refreshToken;
	public RefreshTokenRequest(Long userId, String refreshToken) {
		this.userId = userId;
		this.refreshToken = refreshToken;
	}
	
	public RefreshTokenRequest() {}

	public Long getUserId() {
		return userId;
	}


	public String getRefreshToken() {
		return refreshToken;
	}
}
