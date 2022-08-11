package com.project.flashcardApp.websocket;

public class NotificationResponse {
	private Long id;
	private Long userId;
	private Long friendId;
	private String text;
	private String sentAt;
	
	public NotificationResponse(Long userId, Long friendId, String text, String sentAt) {
		this.userId = userId;
		this.friendId = friendId;
		this.text = text;
		this.sentAt = sentAt;
	}
	public Long getId() {
		return id;
	}
	public Long getUserId() {
		return userId;
	}
	public Long getFriendId() {
		return friendId;
	}
	public String getText() {
		return text;
	}
	public String getSentAt() {
		return sentAt;
	}
	
	
}
