package com.project.flashcardApp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class NoFriendFoundForUserException extends RuntimeException {
	public NoFriendFoundForUserException(String message) {
	      super(message);
	   }
}
