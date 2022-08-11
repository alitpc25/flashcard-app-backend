package com.project.flashcardApp.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.flashcardApp.dtos.UserDto;
import com.project.flashcardApp.requests.UserUpdateRequest;
import com.project.flashcardApp.services.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
	
	private UserService userService;
	
	// Constructor injection
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping
	public ResponseEntity<List<UserDto>> getAllUsers() {
		return ResponseEntity.ok(userService.getAllUsers());
	}
	
	@GetMapping("/{userId}")
	public ResponseEntity<UserDto> getOneUserById(@PathVariable Long userId) {
		return ResponseEntity.ok(userService.getOneUserById(userId));
	}
	
	@PutMapping("/{userId}")
	public ResponseEntity<UserDto> updateOneUserById(@PathVariable Long userId, @RequestBody UserUpdateRequest userToUpdate) {
		return ResponseEntity.ok(userService.updateOneUserById(userId, userToUpdate));
	}
	
	@DeleteMapping("/{userId}")
	public void deleteOneUserById(@PathVariable Long userId) {
		userService.deleteOneUserById(userId);
	}
	
	@PutMapping("/updateScoreByUserId/{userId}")
	public ResponseEntity<UserDto> updateScoreOfOneUserById(@PathVariable Long userId) {
		return ResponseEntity.ok(userService.updateScoreOfOneUserById(userId));
	}
	
}












