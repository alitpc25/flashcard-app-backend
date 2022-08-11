package com.project.flashcardApp.dtos;

public class FriendshipDto {
	private Long id;
	private UserDto user;
	private UserDto friend;
	private boolean isAccepted = false;
	public FriendshipDto(Long id, UserDto user, UserDto friend) {
		this.id = id;
		this.user = user;
		this.friend = friend;
	}
	public FriendshipDto(Long id, UserDto user, UserDto friend, boolean isAccepted) {
		this.id = id;
		this.user = user;
		this.friend = friend;
		this.isAccepted = isAccepted;
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
	public boolean isAccepted() {
		return isAccepted;
	}
	
}
