package com.project.flashcardApp.dtos;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.project.flashcardApp.entities.Friendship;

@Component
public class FriendshipDtoConverter {
	
	private final UserDtoConverter userDtoConverter;
	
	
	public FriendshipDtoConverter(UserDtoConverter userDtoConverter) {
		this.userDtoConverter = userDtoConverter;
	}


	public FriendshipDto convert(Friendship from) {
		return new FriendshipDto(from.getId(), userDtoConverter.convert(from.getUser()), userDtoConverter.convert(from.getFriend()));
	}
	
	public List<FriendshipDto> convert(List<Friendship> from) {
		return from.stream().map(this::convert).collect(Collectors.toList());
	}
}
