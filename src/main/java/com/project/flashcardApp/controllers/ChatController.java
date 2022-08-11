package com.project.flashcardApp.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.flashcardApp.dtos.ChatDto;
import com.project.flashcardApp.dtos.MessageDto;
import com.project.flashcardApp.dtos.UserDto;
import com.project.flashcardApp.requests.MessageSendRequest;
import com.project.flashcardApp.services.ChatService;

@RestController
@RequestMapping("/chat")
public class ChatController {
	
    private final ChatService chatService;
    
    public ChatController(ChatService chatService) {
		this.chatService = chatService;
	}

	@GetMapping("/friends")
    public ResponseEntity<List<UserDto>> getFriendsByUserId(@RequestParam Long userId) {
    	return ResponseEntity.ok(chatService.getFriendsByUserId(userId));
    }
    
    @GetMapping("/privateChat/toUser")
    public ResponseEntity<ChatDto> getPrivateChatToUser(@RequestParam Long userId, @RequestParam Long friendId) {
    	return ResponseEntity.ok(chatService.getPrivateChat(userId, friendId));
    }
    
    @GetMapping("/privateChat/toFriend")
    public ResponseEntity<ChatDto> getPrivateChatToFriend(@RequestParam Long userId, @RequestParam Long friendId) {
    	return ResponseEntity.ok(chatService.getPrivateChat(userId, friendId));
    }
	
    @PostMapping("/privateChat")
    public ResponseEntity<MessageDto> sendPrivateMessage(@RequestParam Long userId, @RequestParam Long friendId, @RequestBody MessageSendRequest messageSendRequest) {
    	return ResponseEntity.ok(chatService.sendPrivateMessage(userId, friendId, messageSendRequest));
    }
    
    @DeleteMapping("/privateChat")
    public void deletePrivateChatMessages(@RequestParam Long userId, @RequestParam Long friendId) {
    	chatService.deletePrivateChatMessages(userId, friendId);
    }

}