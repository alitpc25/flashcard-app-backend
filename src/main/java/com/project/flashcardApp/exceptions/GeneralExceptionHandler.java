package com.project.flashcardApp.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GeneralExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(EmailAlreadyConfirmedException.class)
    public ResponseEntity<?> emailAlreadyConfirmedException(EmailAlreadyConfirmedException exception) {
	   return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(EmailAlreadyInUseException.class)
    public ResponseEntity<?> emailAlreadyInUseException(EmailAlreadyInUseException exception) {
	   return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(EmailInvalidException.class)
    public ResponseEntity<?> emailInvalidException(EmailInvalidException exception) {
	   return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<?> invalidPasswordException(InvalidPasswordException exception) {
	   return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(RefreshTokenInvalidException.class)
    public ResponseEntity<?> refreshTokenInvalidException(RefreshTokenInvalidException exception) {
	   return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ConfirmationTokenExpiredException.class)
    public ResponseEntity<?> tokenExpiredException(ConfirmationTokenExpiredException exception) {
	   return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ConfirmationTokenNotFoundException.class)
    public ResponseEntity<?> tokenIsWrongException(ConfirmationTokenNotFoundException exception) {
	   return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(UsernameAlreadyInUseException.class)
    public ResponseEntity<?> usernameAlreadyInUseException(UsernameAlreadyInUseException exception) {
	   return new ResponseEntity<>(exception.getMessage(), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> userNotFoundException(UserNotFoundException exception) {
	   return new ResponseEntity<>(exception.getMessage(), HttpStatus.NOT_FOUND);
    }
}
