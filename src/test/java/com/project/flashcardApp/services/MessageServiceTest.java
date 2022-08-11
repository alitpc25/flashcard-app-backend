package com.project.flashcardApp.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.project.flashcardApp.dtos.ChatDto;
import com.project.flashcardApp.dtos.MessageDto;
import com.project.flashcardApp.dtos.MessageDtoConverter;
import com.project.flashcardApp.dtos.UserDto;
import com.project.flashcardApp.entities.Chat;
import com.project.flashcardApp.entities.Message;
import com.project.flashcardApp.entities.User;
import com.project.flashcardApp.exceptions.NoMessageFoundException;
import com.project.flashcardApp.repository.MessageRepository;

class MessageServiceTest {
	
	private MessageRepository messageRepository;
	private MessageDtoConverter messageDtoConverter;
	
	private MessageService messageService;

	@BeforeEach
	void setUp() throws Exception {
		messageRepository = Mockito.mock(MessageRepository.class);
		messageDtoConverter = Mockito.mock(MessageDtoConverter.class);
		
		messageService = new MessageService(messageRepository, messageDtoConverter);
	}

	@Test
	void testInsertMessage_whenCalledWithValidRequest_thenShouldReturnValidMessageDto() {
		//Data for testing
		User messageSender = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		User messageReceiver = new User(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		UserDto messageSenderDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto messageReceiverDto = new UserDto(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		Chat chat = new Chat(1L, messageSender, messageReceiver);
		ChatDto chatDto = new ChatDto(1L, messageSenderDto, messageReceiverDto);
		
		LocalDateTime sentAt = LocalDateTime.now();
		
		Message message = new Message(messageSender, messageReceiver, chat, "test-message-text", sentAt, false);
		Message savedMessage = new Message(1L, messageSender, messageReceiver, chat, "test-message-text", sentAt, false);
		MessageDto savedMessageDto = new MessageDto(1L, messageSenderDto, messageReceiverDto, chatDto, "test-message-text", sentAt);
		
		//Test case
		Mockito.when(messageRepository.save(message)).thenReturn(savedMessage);
		Mockito.when(messageDtoConverter.convert(savedMessage)).thenReturn(savedMessageDto);
		
		//Verify
		MessageDto result = messageService.insertMessage(messageSender, messageReceiver, chat, "test-message-text");
		assertEquals(result, savedMessageDto);
		
		Mockito.verify(messageRepository).save(message);
		Mockito.verify(messageDtoConverter).convert(savedMessage);
	}

	@Test
	void testGetMessagesByChatId_whenCalledWithValidRequest_thenShouldReturnListOfMessageDto() {
		//Data for testing
		Long chatId = 1L;
		User messageSender = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		User messageReceiver = new User(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		UserDto messageSenderDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto messageReceiverDto = new UserDto(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		Chat chat = new Chat(1L, messageSender, messageReceiver);
		ChatDto chatDto = new ChatDto(1L, messageSenderDto, messageReceiverDto);
		
		LocalDateTime sentAt = LocalDateTime.now();
		
		Message message = new Message(messageSender, messageReceiver, chat, "test-message-text", sentAt, false);
		List<Message> messages = List.of(message);
		MessageDto messageDto = new MessageDto(1L, messageSenderDto, messageReceiverDto, chatDto, "test-message-text", sentAt);
		List<MessageDto> messagesDto = List.of(messageDto);
		
		//Test case
		Mockito.when(messageRepository.findByChatId(chatId)).thenReturn(Optional.of(messages));
		Mockito.when(messageDtoConverter.convert(messages)).thenReturn(messagesDto);
		
		//Verify
		List<MessageDto> result = messageService.getMessagesByChatId(chatId);
		assertEquals(result, messagesDto);
		
		Mockito.verify(messageRepository).findByChatId(chatId);
		Mockito.verify(messageDtoConverter).convert(messages);
	}

	@Test
	void testGetMessagesByChatId_whenCalledWithNonexistentMessages_thenShouldThrowNoMessageFoundException() {
		//Data for testing
		Long chatId = 1L;
		
		//Test case
		Mockito.when(messageRepository.findByChatId(chatId)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(NoMessageFoundException.class, () -> {
			messageService.getMessagesByChatId(chatId);
		});
		
		Mockito.verify(messageRepository).findByChatId(chatId);
	}
	
	@Test
	void testGetUserMessagesByChatIdAndMessageSenderId_whenCalledWithValidRequest_thenShouldReturnListOfMessageDto() {
		//Data for testing
		Long chatId = 1L;
		Long messageSenderId = 1L;
		User messageSender = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		User messageReceiver = new User(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		UserDto messageSenderDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto messageReceiverDto = new UserDto(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		Chat chat = new Chat(1L, messageSender, messageReceiver);
		ChatDto chatDto = new ChatDto(1L, messageSenderDto, messageReceiverDto);
		
		LocalDateTime sentAt = LocalDateTime.now();
		
		Message message = new Message(messageSender, messageReceiver, chat, "test-message-text", sentAt, false);
		List<Message> messages = List.of(message);
		MessageDto messageDto = new MessageDto(1L, messageSenderDto, messageReceiverDto, chatDto, "test-message-text", sentAt);
		List<MessageDto> messagesDto = List.of(messageDto);
		
		//Test case
		Mockito.when(messageRepository.findByChatIdAndMessageSenderId(chatId, messageSenderId)).thenReturn(Optional.of(messages));
		Mockito.when(messageDtoConverter.convert(messages)).thenReturn(messagesDto);
		
		//Verify
		List<MessageDto> result = messageService.getUserMessagesByChatIdAndMessageSenderId(chatId, messageSenderId);
		assertEquals(result, messagesDto);
		
		Mockito.verify(messageRepository).findByChatIdAndMessageSenderId(chatId, messageSenderId);
		Mockito.verify(messageDtoConverter).convert(messages);
	}

	@Test
	void testGetUserMessagesByChatIdAndMessageSenderId_whenCalledWithNonexistentMessages_thenShouldThrowNoMessageFoundException() {
		//Data for testing
		Long chatId = 1L;
		Long messageSenderId = 1L;
		
		//Test case
		Mockito.when(messageRepository.findByChatIdAndMessageSenderId(chatId, messageSenderId)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(NoMessageFoundException.class, () -> {
			messageService.getUserMessagesByChatIdAndMessageSenderId(chatId, messageSenderId);
		});
		
		Mockito.verify(messageRepository).findByChatIdAndMessageSenderId(chatId, messageSenderId);
	}
	
	@Test
	void testGetFriendMessagesByChatIdAndMessageReceiverId_whenCalledWithValidRequest_thenShouldReturnListOfMessageDto() {
		//Data for testing
		Long chatId = 1L;
		Long messageReceiverId = 1L;
		User messageSender = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		User messageReceiver = new User(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		UserDto messageSenderDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto messageReceiverDto = new UserDto(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		Chat chat = new Chat(1L, messageSender, messageReceiver);
		ChatDto chatDto = new ChatDto(1L, messageSenderDto, messageReceiverDto);
		
		LocalDateTime sentAt = LocalDateTime.now();
		
		Message message = new Message(messageSender, messageReceiver, chat, "test-message-text", sentAt, false);
		List<Message> messages = List.of(message);
		MessageDto messageDto = new MessageDto(1L, messageSenderDto, messageReceiverDto, chatDto, "test-message-text", sentAt);
		List<MessageDto> messagesDto = List.of(messageDto);
		
		//Test case
		Mockito.when(messageRepository.findByChatIdAndMessageReceiverId(chatId, messageReceiverId)).thenReturn(Optional.of(messages));
		Mockito.when(messageDtoConverter.convert(messages)).thenReturn(messagesDto);
		
		//Verify
		List<MessageDto> result = messageService.getFriendMessagesByChatIdAndMessageReceiverId(chatId, messageReceiverId);
		assertEquals(result, messagesDto);
		
		Mockito.verify(messageRepository).findByChatIdAndMessageReceiverId(chatId, messageReceiverId);
		Mockito.verify(messageDtoConverter).convert(messages);
	}

	@Test
	void testGetFriendMessagesByChatIdAndMessageReceiverId_whenCalledWithNonexistentMessages_thenShouldThrowNoMessageFoundException() {
		//Data for testing
		Long chatId = 1L;
		Long messageReceiverId = 1L;
		
		//Test case
		Mockito.when(messageRepository.findByChatIdAndMessageReceiverId(chatId, messageReceiverId)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(NoMessageFoundException.class, () -> {
			messageService.getFriendMessagesByChatIdAndMessageReceiverId(chatId, messageReceiverId);
		});
		
		Mockito.verify(messageRepository).findByChatIdAndMessageReceiverId(chatId, messageReceiverId);
	}

}
