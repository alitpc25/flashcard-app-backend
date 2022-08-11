package com.project.flashcardApp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class WordAlreadyExistException extends RuntimeException {
	public WordAlreadyExistException(String message) {
		super(message);
	}
}
