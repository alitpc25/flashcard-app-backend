package com.project.flashcardApp.services;

import java.time.LocalDateTime;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.project.flashcardApp.dtos.MessageDto;
import com.project.flashcardApp.dtos.MessageDtoConverter;
import com.project.flashcardApp.entities.Chat;
import com.project.flashcardApp.entities.Message;
import com.project.flashcardApp.entities.User;
import com.project.flashcardApp.exceptions.NoMessageFoundException;
import com.project.flashcardApp.repository.MessageRepository;

@Service
public class MessageService {
	
	private final MessageRepository messageRepository;
	private final MessageDtoConverter messageDtoConverter;
	public MessageService(MessageRepository messageRepository, MessageDtoConverter messageDtoConverter) {
		this.messageDtoConverter = messageDtoConverter;
		this.messageRepository = messageRepository;
	}
	

	public MessageDto insertMessage(User messageSender, User messageReceiver, Chat chat, String text) {
		Message message = new Message(messageSender, messageReceiver, chat, text, LocalDateTime.now(), false);
		return messageDtoConverter.convert(messageRepository.save(message));
	}

	public List<MessageDto> getMessagesByChatId(Long chatId) {
		List<Message> messages = messageRepository.findByChatId(chatId).orElseThrow(() -> new NoMessageFoundException("No message found."));
		return messageDtoConverter.convert(messages);
	}

	public List<MessageDto> getUserMessagesByChatIdAndMessageSenderId(Long chatId, Long userId) {
		List<Message> messages = messageRepository.findByChatIdAndMessageSenderId(chatId, userId).orElseThrow(() -> new NoMessageFoundException("No message found."));
		return messageDtoConverter.convert(messages);
	}

	public List<MessageDto> getFriendMessagesByChatIdAndMessageReceiverId(Long chatId, Long friendId) {
		List<Message> messages = messageRepository.findByChatIdAndMessageReceiverId(chatId, friendId).orElseThrow(() -> new NoMessageFoundException("No message found."));
		return messageDtoConverter.convert(messages);
	}

	public void deleteAllMessagesByChatId(Long chatId) {
		messageRepository.deleteByChatId(chatId);
	}

	public long doesUnseenMessageOfFriendExist(Long userId, Long friendId) {
		return messageRepository.existsByUserIdAndFriendIdAndIsSeenFalse(userId, friendId) / 2;
	}

	@Transactional
	public void makeAllMessagesSeen(Long userId, Long friendId) {
		List<Message> unseenMessagesOfTheChat = messageRepository.findAllByIsSeenFalse(userId, friendId);
		unseenMessagesOfTheChat.stream().forEach(message -> {
			message.setSeen(true);
		});
	}
	
}
