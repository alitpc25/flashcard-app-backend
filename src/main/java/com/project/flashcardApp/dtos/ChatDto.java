package com.project.flashcardApp.dtos;

import java.util.Objects;

public class ChatDto {
	private Long id;
	private UserDto user;
	private UserDto friend;
	public ChatDto(Long id, UserDto user, UserDto friend) {
		this.id = id;
		this.user = user;
		this.friend = friend;
	}
	public Long getId() {
		return id;
	}
	public UserDto getUser() {
		return user;
	}
	public UserDto getFriend() {
		return friend;
	}
	@Override
	public int hashCode() {
		return Objects.hash(friend, id, user);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChatDto other = (ChatDto) obj;
		return Objects.equals(friend, other.friend) && Objects.equals(id, other.id) && Objects.equals(user, other.user);
	}
	
}
