package com.project.flashcardApp.requests;

public class UserUpdateRequest {
	String oldPassword; 
	String newPassword;
	
	public UserUpdateRequest(String oldPassword, String newPassword) {
		this.oldPassword = oldPassword;
		this.newPassword = newPassword;
	}
	public String getOldPassword() {
		return oldPassword;
	}
	public String getNewPassword() {
		return newPassword;
	} 
	
	
}
