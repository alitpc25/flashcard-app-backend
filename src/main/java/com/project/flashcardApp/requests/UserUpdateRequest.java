package com.project.flashcardApp.requests;

public class UserUpdateRequest {
	String username;
	String oldPassword; 
	String newPassword;
	
	public UserUpdateRequest(String username, String oldPassword, String newPassword) {
		this.username = username;
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}
	public String getUsername() {
		return username;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public String getNewPassword() {
		return newPassword;
	} 
	
	
}
