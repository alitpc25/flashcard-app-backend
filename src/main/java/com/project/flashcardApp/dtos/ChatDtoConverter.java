package com.project.flashcardApp.dtos;

import org.springframework.stereotype.Component;

import com.project.flashcardApp.entities.Chat;

@Component
public class ChatDtoConverter {
	
	private final UserDtoConverter userDtoConverter;
	
	public ChatDtoConverter(UserDtoConverter userDtoConverter) {
		super();
		this.userDtoConverter = userDtoConverter;
	}

	public ChatDto convert(Chat from) {
		return new ChatDto(from.getId(), userDtoConverter.convert(from.getUser()), userDtoConverter.convert(from.getFriend()));
	}
}
