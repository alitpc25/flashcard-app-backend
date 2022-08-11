package com.project.flashcardApp.dtos;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.project.flashcardApp.entities.Message;

@Component
public class MessageDtoConverter {
	
	private final UserDtoConverter userDtoConverter;
	private final ChatDtoConverter chatDtoConverter;

	public MessageDtoConverter(UserDtoConverter userDtoConverter, ChatDtoConverter chatDtoConverter) {
		super();
		this.userDtoConverter = userDtoConverter;
		this.chatDtoConverter = chatDtoConverter;
	}

	public MessageDto convert(Message from) {
		return new MessageDto(from.getId(), userDtoConverter.convert(from.getMessageSender()), userDtoConverter.convert(from.getMessageReceiver()), chatDtoConverter.convert(from.getChat()), from.getText(), from.getSentAt());
	}
	
	public List<MessageDto> convert(List<Message> from) {
		return from.stream().map(this::convert).collect(Collectors.toList());
	}
}
