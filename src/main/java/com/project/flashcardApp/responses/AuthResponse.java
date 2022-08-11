package com.project.flashcardApp.responses;

import java.util.Objects;

public class AuthResponse {
	
	String jwtAccessToken;
	String jwtRefreshToken;
	String message;
	Long userId;
	public AuthResponse(String jwtAccessToken, String jwtRefreshToken, String message, Long userId) {
		super();
		this.jwtAccessToken = jwtAccessToken;
		this.jwtRefreshToken = jwtRefreshToken;
		this.message = message;
		this.userId = userId;
	}
	
	public AuthResponse() {}
	
	public String getJwtAccessToken() {
		return jwtAccessToken;
	}
	public String getJwtRefreshToken() {
		return jwtRefreshToken;
	}
	public String getMessage() {
		return message;
	}
	public Long getUserId() {
		return userId;
	}
	public void setJwtAccessToken(String jwtAccessToken) {
		this.jwtAccessToken = jwtAccessToken;
	}
	public void setJwtRefreshToken(String jwtRefreshToken) {
		this.jwtRefreshToken = jwtRefreshToken;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(jwtAccessToken, jwtRefreshToken, message, userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AuthResponse other = (AuthResponse) obj;
		return Objects.equals(jwtAccessToken, other.jwtAccessToken)
				&& Objects.equals(jwtRefreshToken, other.jwtRefreshToken) && Objects.equals(message, other.message)
				&& Objects.equals(userId, other.userId);
	}
	
}
