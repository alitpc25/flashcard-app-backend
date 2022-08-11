package com.project.flashcardApp.requests;

public class MessageSendRequest {
	private String text;

	public MessageSendRequest(String text) {
		this.text = text;
	}
	
	public MessageSendRequest() {}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
}
