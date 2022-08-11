package com.project.flashcardApp.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.flashcardApp.dtos.FriendshipDto;
import com.project.flashcardApp.requests.FriendshipAcceptRequest;
import com.project.flashcardApp.services.FriendshipService;

@RestController
@RequestMapping("/friends")
public class FriendshipController {
	
	private FriendshipService friendshipService;
	
	public FriendshipController(FriendshipService friendshipService) {
		this.friendshipService = friendshipService;
	}

	@GetMapping()
	public ResponseEntity<List<FriendshipDto>> getAllFriendsByUserId(@RequestParam Long userId) {
		return ResponseEntity.ok(friendshipService.getAllFriendsByUserId(userId));
	}
	
	@GetMapping("/isFriendshipAlreadyRequested")
	public boolean isFriendshipAlreadyRequested(@RequestParam Long userId, @RequestParam Long friendId) {
		return friendshipService.isFriendshipAlreadyRequested(userId, friendId);
	}
	
	@GetMapping("/isFriendshipAlreadyReceived")
	public boolean isFriendshipAlreadyReceived(@RequestParam Long userId, @RequestParam Long friendId) {
		return friendshipService.isFriendshipAlreadyReceived(userId, friendId);
	}
	
	@GetMapping("/isFriendshipExist")
	public boolean isFriendshipExist(@RequestParam Long userId, @RequestParam Long friendId) {
		return friendshipService.isFriendshipExist(userId, friendId);
	}
	
	@GetMapping("/addFriend")
	public ResponseEntity<FriendshipDto> sendFriendRequestToFriendId(@RequestParam Long userId, @RequestParam Long friendId) {
		return ResponseEntity.ok(friendshipService.sendFriendRequestToFriendId(userId, friendId));
	}
	
	@GetMapping("/friendRequests")
	public ResponseEntity<List<FriendshipDto>> getAllFriendRequestsByUserId(@RequestParam Long userId) {
		return ResponseEntity.ok(friendshipService.getAllFriendRequestsByUserId(userId));
	}
	
	@PutMapping("/acceptFriendshipRequest")
	public ResponseEntity<FriendshipDto> acceptFriendshipRequest(@RequestBody FriendshipAcceptRequest friendshipAcceptRequest) {
		return ResponseEntity.ok(friendshipService.acceptFriendshipRequest(friendshipAcceptRequest));
	}
	
	@DeleteMapping("/declineFriendshipRequest")
	public void declineFriendshipRequest(@RequestParam Long userId, @RequestParam Long friendId) {
		friendshipService.declineFriendshipRequest(userId, friendId);
	}
	
	@DeleteMapping()
	public void deleteFriendship(@RequestParam Long userId, @RequestParam Long friendId) {
		friendshipService.deleteFriendship(userId, friendId);
	}
	
}
