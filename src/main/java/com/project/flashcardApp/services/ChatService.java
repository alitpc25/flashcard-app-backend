package com.project.flashcardApp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;

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

@Service
public class ChatService {
	
    private final UserService userService;
    private final MessageService messageService;
    private final ChatRepository chatRepository;
    private final ChatDtoConverter chatDtoConverter;
    private final UserDtoConverter userDtoConverter;

	public ChatService(UserService userService, MessageService messageService, ChatRepository chatRepository,
			ChatDtoConverter chatDtoConverter, UserDtoConverter userDtoConverter) {
		this.userService = userService;
		this.messageService = messageService;
		this.chatRepository = chatRepository;
		this.chatDtoConverter = chatDtoConverter;
		this.userDtoConverter = userDtoConverter;
	}

	public List<UserDto> getFriendsByUserId(Long userId) {
		List<Chat> friendships = chatRepository.findByFriendIdOrUserId(userId).orElseThrow(() -> new NoChatFoundForUserException("No chat found four user."));
		List<User> friends = new ArrayList<User>();
		friendships.stream().forEach((friendship) -> {
			friends.add(friendship.getFriend());
		});
		return friends.stream().map(userDtoConverter::convert).collect(Collectors.toList());
	}

	public ChatDto getPrivateChat(Long userId, Long friendId) {
		return chatDtoConverter.convert(chatRepository.findByUserIdAndFriendId(userId, friendId).orElseThrow(() -> new ChatNotFoundException("Private chat doesn't exist.")));
	}

	public MessageDto sendPrivateMessage(Long userId, Long friendId, MessageSendRequest messageSendRequest) {
		Chat messageSenderChat = chatRepository.findByUserIdAndFriendId(userId, friendId).orElseThrow(() -> new ChatNotFoundException("Private chat doesn't exist."));
		Chat messageReceiverChat = chatRepository.findByUserIdAndFriendId(friendId, userId).orElseThrow(() -> new ChatNotFoundException("Private chat doesn't exist."));
		User messageSender = userService.getOneUserByIdProtected(userId);
		User messageReceiver = userService.getOneUserByIdProtected(friendId);
		messageService.insertMessage(messageSender, messageReceiver, messageReceiverChat, messageSendRequest.getText());
		return messageService.insertMessage(messageSender, messageReceiver, messageSenderChat, messageSendRequest.getText());
	}

	public void insertOneChat(Long userId, Long friendId) {
		Chat chat = new Chat(userService.getOneUserByIdProtected(userId), userService.getOneUserByIdProtected(friendId));
		chatRepository.save(chat);
	}
	
	protected void deleteAllChatsByUserId(Long userId) {
		chatRepository.deleteAllByUserIdOrFriendId(userId);
	}

	@Transactional
	public void deletePrivateChatMessages(Long userId, Long friendId) {
		Chat chat = chatRepository.findByUserIdAndFriendId(userId, friendId).orElseThrow(() -> new ChatNotFoundException("Private chat doesn't exist."));
		messageService.deleteAllMessagesByChatId(chat.getId());
	}
	
	@Transactional
	public void deletePrivateChat(Long userId, Long friendId) {
		Chat chatUser = chatRepository.findByUserIdAndFriendId(userId, friendId).orElseThrow(() -> new ChatNotFoundException("Private chat doesn't exist."));
		Chat chatFriend = chatRepository.findByUserIdAndFriendId(friendId, userId).orElseThrow(() -> new ChatNotFoundException("Private chat doesn't exist."));
		chatRepository.delete(chatUser);
		chatRepository.delete(chatFriend);
	}

}
