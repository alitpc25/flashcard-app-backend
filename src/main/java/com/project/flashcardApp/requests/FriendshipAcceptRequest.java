package com.project.flashcardApp.requests;

public class FriendshipAcceptRequest {
	
	private Long userId;
	private Long friendId;
	
	public FriendshipAcceptRequest(Long userId, Long friendId) {
		this.userId = userId;
		this.friendId = friendId;
	}
	public Long getUserId() {
		return userId;
	}
	public Long getFriendId() {
		return friendId;
	}	

}
