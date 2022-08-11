package com.project.flashcardApp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class EssayNotFoundException extends RuntimeException {
	public EssayNotFoundException(String message) {
	      super(message);
	   }
}
