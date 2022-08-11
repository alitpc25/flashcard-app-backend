package com.project.flashcardApp.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.flashcardApp.dtos.FriendshipDto;
import com.project.flashcardApp.dtos.FriendshipDtoConverter;
import com.project.flashcardApp.entities.Friendship;
import com.project.flashcardApp.exceptions.FriendshipNotFoundException;
import com.project.flashcardApp.exceptions.NoFriendFoundForUserException;
import com.project.flashcardApp.exceptions.NoFriendshipRequestFoundForUserException;
import com.project.flashcardApp.repository.FriendshipRepository;
import com.project.flashcardApp.requests.FriendshipAcceptRequest;

@Service
public class FriendshipService {
	
	private final FriendshipRepository friendshipRepository;
	private final UserService userService;
	private final ChatService chatService;
	private final FriendshipDtoConverter friendshipDtoConverter;

	public FriendshipService(FriendshipRepository friendshipRepository, UserService userService,
			ChatService chatService, FriendshipDtoConverter friendshipDtoConverter) {
		this.friendshipRepository = friendshipRepository;
		this.userService = userService;
		this.chatService = chatService;
		this.friendshipDtoConverter = friendshipDtoConverter;
	}

	public List<FriendshipDto> getAllFriendsByUserId(Long userId) {
		List<Friendship> acceptedFriendships = friendshipRepository.findByFriendIdOrUserIdAndIsAcceptedTrue(userId).orElseThrow(() -> new NoFriendFoundForUserException("User doesn't have any friends."));
		return friendshipDtoConverter.convert(acceptedFriendships);
	}
	
	public List<FriendshipDto> getAllFriendRequestsByUserId(Long userId) {
		List<Friendship> friendshipRequests = friendshipRepository.findByFriendIdAndIsAcceptedFalse(userId).orElseThrow(() -> new NoFriendshipRequestFoundForUserException("User doesn't have any friendship request."));
		return friendshipDtoConverter.convert(friendshipRequests);
	}

	public FriendshipDto sendFriendRequestToFriendId(Long userId, Long friendId) {
		Friendship friendship = new Friendship(userService.getOneUserByIdProtected(userId), userService.getOneUserByIdProtected(friendId));
		return friendshipDtoConverter.convert(friendshipRepository.save(friendship));
	}

	public FriendshipDto acceptFriendshipRequest(FriendshipAcceptRequest friendshipAcceptRequest) {
		Long userId = friendshipAcceptRequest.getUserId();
		Long friendId = friendshipAcceptRequest.getFriendId();
		Friendship friendship = friendshipRepository.findByFriendIdAndUserIdAndIsAcceptedFalse(userId, friendId).orElseThrow(() -> new FriendshipNotFoundException("Friendship not found."));
		friendship.setAccepted(true);
		friendshipRepository.save(friendship);
		chatService.insertOneChat(friendId, userId);
		chatService.insertOneChat(userId, friendId);
		return friendshipDtoConverter.convert(friendship);
	}
	
	@Transactional
	public void declineFriendshipRequest(Long userId, Long friendId) {
		friendshipRepository.deleteByFriendIdOrUserId(friendId, userId);
	}

	public boolean isFriendshipAlreadyRequested(Long userId, Long friendId) {
		return friendshipRepository.existsByUserIdAndFriendIdAndIsAcceptedFalse(userId, friendId) > 0;
	}

	public boolean isFriendshipAlreadyReceived(Long userId, Long friendId) {
		return friendshipRepository.existsByFriendIdAndUserIdAndIsAcceptedFalse(userId, friendId) > 0;
	}

	public boolean isFriendshipExist(Long userId, Long friendId) {
		return friendshipRepository.existsFriendshipByFriendIdAndUserId(userId, friendId) > 0;
	}
	
	protected void deleteAllFriendshipsByUserId(Long userId) {
		friendshipRepository.deleteAllByUserIdOrFriendId(userId);
	}

	@Transactional
	public void deleteFriendship(Long userId, Long friendId) {
		friendshipRepository.deleteByFriendIdOrUserId(userId, friendId);
		chatService.deletePrivateChat(userId, friendId);
	}

}
