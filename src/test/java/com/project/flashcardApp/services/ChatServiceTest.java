package com.project.flashcardApp.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.project.flashcardApp.dtos.ChatDto;
import com.project.flashcardApp.dtos.ChatDtoConverter;
import com.project.flashcardApp.dtos.MessageDto;
import com.project.flashcardApp.dtos.UserDto;
import com.project.flashcardApp.dtos.UserDtoConverter;
import com.project.flashcardApp.entities.Chat;
import com.project.flashcardApp.entities.User;
import com.project.flashcardApp.exceptions.ChatNotFoundException;
import com.project.flashcardApp.exceptions.NoChatFoundForUserException;
import com.project.flashcardApp.repository.ChatRepository;
import com.project.flashcardApp.requests.MessageSendRequest;

class ChatServiceTest {
	
    private UserService userService;
    private MessageService messageService;
    private ChatRepository chatRepository;
    private ChatDtoConverter chatDtoConverter;
    private UserDtoConverter userDtoConverter;
    
    private ChatService chatService;

	@BeforeEach
	void setUp() throws Exception {
		userService = Mockito.mock(UserService.class);
		messageService = Mockito.mock(MessageService.class);
		chatRepository = Mockito.mock(ChatRepository.class);
		chatDtoConverter = Mockito.mock(ChatDtoConverter.class);
		userDtoConverter = Mockito.mock(UserDtoConverter.class);

		chatService = new ChatService(userService, messageService, chatRepository, chatDtoConverter, userDtoConverter);
	}

	@Test
	void testGetFriendsByUserId_whenValidUserId_thenShouldReturnListOfUserDto() {
		//Data for testing
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		User friend = new User(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		Chat chat = new Chat(1L, user, friend);
		List<Chat> friendships = List.of(chat);
		List<User> friends = new ArrayList<User>();
		friendships.stream().forEach((friendship) -> {
			friends.add(friendship.getFriend());
		});
		List<UserDto> userDtos = friends.stream().map(userDtoConverter::convert).collect(Collectors.toList());
		
		//Test Case
		Mockito.when(chatRepository.findByFriendIdOrUserId(1L)).thenReturn(Optional.of(friendships));
		
		//Verify
		List<UserDto> result = chatService.getFriendsByUserId(1L);
		
		assertEquals(result, userDtos);
		
		Mockito.verify(chatRepository).findByFriendIdOrUserId(1L);
	}
	
	@Test
	void testGetFriendsByUserId_whenNoChatFound_thenShouldThrowNoChatFoundForUserException() {
		//Data for testing

		//Test Case
		Mockito.when(chatRepository.findByFriendIdOrUserId(1L)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(NoChatFoundForUserException.class, () -> {
			chatService.getFriendsByUserId(1L);
		});
		
		Mockito.verify(chatRepository).findByFriendIdOrUserId(1L);
	}
	
	@Test
	void testGetPrivateChat_whenValidUserIdAndFriendId_thenShouldReturnValidChatDto() {
		//Data for testing
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		User friend = new User(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto friendDto = new UserDto(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		Chat chat = new Chat(1L, user, friend);
		ChatDto chatDto = new ChatDto(1L, userDto, friendDto);
		
		//Test Case
		Mockito.when(chatRepository.findByUserIdAndFriendId(1L, 2L)).thenReturn(Optional.of(chat));
		Mockito.when(chatDtoConverter.convert(chat)).thenReturn(chatDto);
		
		//Verify
		ChatDto result = chatService.getPrivateChat(1L, 2L);
		assertEquals(chatDto, result);
		
		Mockito.verify(chatRepository).findByUserIdAndFriendId(1L, 2L);
		Mockito.verify(chatDtoConverter).convert(chat);
	}
	
	@Test
	void testGetPrivateChat_whenNoPrivateChatFoundForGivenUserIdAndFriendId_thenShouldThrowChatNotFoundException() {
		//Data for testing
		
		//Test Case
		Mockito.when(chatRepository.findByUserIdAndFriendId(1L, 2L)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(ChatNotFoundException.class, () -> {
			chatService.getPrivateChat(1L, 2L);
		});
		
		Mockito.verify(chatRepository).findByUserIdAndFriendId(1L, 2L);
	}
	
	@Test
	void testSendPrivateMessage_whenCalledWithValidRequest_thenShouldReturnMessageDto() {
		//Data for testing
		User messageSender = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		User messageReceiver = new User(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		Chat chat = new Chat(1L, messageSender, messageReceiver);
		UserDto messageSenderDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto messageReceiverDto = new UserDto(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		ChatDto chatDto = new ChatDto(1L, messageSenderDto, messageReceiverDto);
		MessageSendRequest messageSendRequest = new MessageSendRequest("test-text");
		LocalDateTime sentAt = LocalDateTime.now();
		MessageDto messageDto = new MessageDto(1L, messageSenderDto, messageReceiverDto, chatDto, "test-text", sentAt);
		
		//Test Case
		Mockito.when(userService.getOneUserByIdProtected(1L)).thenReturn(messageSender);
		Mockito.when(userService.getOneUserByIdProtected(2L)).thenReturn(messageReceiver);
		Mockito.when(chatRepository.findByUserIdAndFriendId(1L, 2L)).thenReturn(Optional.of(chat));
		Mockito.when(messageService.insertMessage(messageSender, messageReceiver, chat, messageSendRequest.getText())).thenReturn(messageDto);

		//Verify
		MessageDto result = chatService.sendPrivateMessage(1L, 2L, messageSendRequest);
		assertEquals(result, messageDto);
		
		Mockito.verify(userService).getOneUserByIdProtected(1L);
		Mockito.verify(userService).getOneUserByIdProtected(2L);
		Mockito.verify(chatRepository).findByUserIdAndFriendId(1L, 2L);
		Mockito.verify(messageService).insertMessage(messageSender, messageReceiver, chat, messageSendRequest.getText());
	}
	
	@Test
	void testSendPrivateMessage_whenCalledWithNonexistChat_thenShouldThrowChatNotFoundException() {
		//Data for testing
		MessageSendRequest messageSendRequest = new MessageSendRequest("test-text");
		
		//Test Case
		Mockito.when(chatRepository.findByUserIdAndFriendId(1L, 2L)).thenReturn(Optional.empty());

		//Verify
		assertThrows(ChatNotFoundException.class, () -> {
			chatService.sendPrivateMessage(1L, 2L, messageSendRequest);
		});
		
		Mockito.verify(chatRepository).findByUserIdAndFriendId(1L, 2L);
	}

}
