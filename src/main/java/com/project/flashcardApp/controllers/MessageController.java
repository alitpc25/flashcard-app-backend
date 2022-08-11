package com.project.flashcardApp.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.flashcardApp.dtos.MessageDto;
import com.project.flashcardApp.services.MessageService;

@RestController
@RequestMapping("chat/privateChat/messages")
public class MessageController {
	
    @Autowired
    private MessageService messageService;
    
    @GetMapping("/user")
    public ResponseEntity<List<MessageDto>> getUserMessagesByChatIdAndMessageSenderId(@RequestParam Long chatId, @RequestParam Long userId) {
    	return ResponseEntity.ok(messageService.getUserMessagesByChatIdAndMessageSenderId(chatId, userId));
    }
    
    @GetMapping("/friend")
    public ResponseEntity<List<MessageDto>> getFriendMessagesByChatIdAndMessageReceiverId(@RequestParam Long chatId, @RequestParam Long friendId) {
    	return ResponseEntity.ok(messageService.getFriendMessagesByChatIdAndMessageReceiverId(chatId, friendId));
    }
    
    @GetMapping("/friend/doesUnseenMessageExist")
    public long doesUnseenMessageOfFriendExist(@RequestParam Long userId, @RequestParam Long friendId) {
    	return messageService.doesUnseenMessageOfFriendExist(userId, friendId);
    }
    
    @PutMapping("/friend/allMessagesSeen")
    public ResponseEntity<Void> makeAllMessagesSeen(@RequestParam Long userId, @RequestParam Long friendId) {
    	messageService.makeAllMessagesSeen(userId, friendId);
    	return ResponseEntity.noContent().build();
    }
    
}
