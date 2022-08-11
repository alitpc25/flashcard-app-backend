package com.project.flashcardApp.utilities.registration;

public interface EmailSender {
	
	void send(String to, String email, String subject);
	
}
