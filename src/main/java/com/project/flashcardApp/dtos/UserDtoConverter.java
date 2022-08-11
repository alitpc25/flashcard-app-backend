package com.project.flashcardApp.dtos;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;
import com.project.flashcardApp.entities.User;

@Component
public class UserDtoConverter {
	public UserDto convert(User from) {
		return new UserDto(from.getId(), from.getUsername(), from.getEmail(), from.getEnabled(), from.getPassword(), from.getScore());
	}
	
	public List<UserDto> convert(List<User> from) {
		return from.stream().map(this::convert).collect(Collectors.toList());
	}
}