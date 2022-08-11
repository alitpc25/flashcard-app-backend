package com.project.flashcardApp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RefreshTokenValidException extends RuntimeException {
	public RefreshTokenValidException(String message) {
		super(message);
	}
}
