package com.project.flashcardApp.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import com.project.flashcardApp.dtos.FriendshipDto;
import com.project.flashcardApp.dtos.FriendshipDtoConverter;
import com.project.flashcardApp.dtos.UserDto;
import com.project.flashcardApp.entities.Friendship;
import com.project.flashcardApp.entities.User;
import com.project.flashcardApp.exceptions.FriendshipNotFoundException;
import com.project.flashcardApp.exceptions.NoFriendFoundForUserException;
import com.project.flashcardApp.exceptions.NoFriendshipRequestFoundForUserException;
import com.project.flashcardApp.repository.FriendshipRepository;
import com.project.flashcardApp.requests.FriendshipAcceptRequest;

class FriendshipServiceTest {
	
	private FriendshipRepository friendshipRepository;
	private UserService userService;
	private ChatService chatService;
	private FriendshipDtoConverter friendshipDtoConverter;
	
	private FriendshipService friendshipService;

	@BeforeEach
	void setUp() throws Exception {
		friendshipRepository = Mockito.mock(FriendshipRepository.class);
		userService = Mockito.mock(UserService.class);
		chatService = Mockito.mock(ChatService.class);
		friendshipDtoConverter = Mockito.mock(FriendshipDtoConverter.class);
		
		friendshipService = new FriendshipService(friendshipRepository, userService, chatService, friendshipDtoConverter);
	}

	@Test
	void testGetAllFriendsByUserId_whenCalledWithValidRequest_thenShouldReturnListOfFriendshipDto() {
		//Data for testing
		Long userId = 1L;
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		User friend = new User(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto friendDto = new UserDto(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		Friendship friendship = new Friendship(1L, user, friend, true);
		List<Friendship> acceptedFriendships = List.of(friendship);
		FriendshipDto friendshipDto = new FriendshipDto(1L, userDto, friendDto, true);
		List<FriendshipDto> acceptedFriendshipsDto = List.of(friendshipDto);
		
		
		//Test case
		Mockito.when(friendshipRepository.findByFriendIdOrUserIdAndIsAcceptedTrue(userId)).thenReturn(Optional.of(acceptedFriendships));
		Mockito.when(friendshipDtoConverter.convert(acceptedFriendships)).thenReturn(acceptedFriendshipsDto);
		
		//Verify
		List<FriendshipDto> result = friendshipService.getAllFriendsByUserId(userId);
		assertEquals(result, acceptedFriendshipsDto);
		
		Mockito.verify(friendshipRepository).findByFriendIdOrUserIdAndIsAcceptedTrue(userId);
		Mockito.verify(friendshipDtoConverter).convert(acceptedFriendships);
	}
	
	@Test
	void testGetAllFriendsByUserId_whenCalledWithNonexistentAcceptedFriendships_thenShouldThrowNoFriendFoundForUserException() {
		//Data for testing
		Long userId = 1L;	
		
		//Test case
		Mockito.when(friendshipRepository.findByFriendIdOrUserIdAndIsAcceptedTrue(userId)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(NoFriendFoundForUserException.class, () -> {
			friendshipService.getAllFriendsByUserId(userId);
		});
		
		Mockito.verify(friendshipRepository).findByFriendIdOrUserIdAndIsAcceptedTrue(userId);
	}
	
	@Test
	void testGetAllFriendRequestsByUserId_whenCalledWithValidRequest_thenShouldReturnListOfFriendshipDto() {
		//Data for testing
		Long userId = 1L;
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		User friend = new User(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto friendDto = new UserDto(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		Friendship friendship = new Friendship(1L, user, friend, true);
		List<Friendship> friendshipRequests = List.of(friendship);
		FriendshipDto friendshipDto = new FriendshipDto(1L, userDto, friendDto, true);
		List<FriendshipDto> friendshipRequestsDto = List.of(friendshipDto);
		
		
		//Test case
		Mockito.when(friendshipRepository.findByFriendIdAndIsAcceptedFalse(userId)).thenReturn(Optional.of(friendshipRequests));
		Mockito.when(friendshipDtoConverter.convert(friendshipRequests)).thenReturn(friendshipRequestsDto);
		
		//Verify
		List<FriendshipDto> result = friendshipService.getAllFriendRequestsByUserId(userId);
		assertEquals(result, friendshipRequestsDto);
		
		Mockito.verify(friendshipRepository).findByFriendIdAndIsAcceptedFalse(userId);
		Mockito.verify(friendshipDtoConverter).convert(friendshipRequests);
	}
	
	@Test
	void testGetAllFriendsByUserId_whenCalledWithNonexistentAcceptedFriendships_thenShouldThrowNoFriendshipRequestFoundForUserException() {
		//Data for testing
		Long userId = 1L;	
		
		//Test case
		Mockito.when(friendshipRepository.findByFriendIdAndIsAcceptedFalse(userId)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(NoFriendshipRequestFoundForUserException.class, () -> {
			friendshipService.getAllFriendRequestsByUserId(userId);
		});
		
		Mockito.verify(friendshipRepository).findByFriendIdAndIsAcceptedFalse(userId);
	}
	
	@Test
	void testSendFriendRequestToFriendId_whenCalledWithValidRequest_thenShouldReturnFriendshipDto() {
		//Data for testing
		Long userId = 1L;
		Long friendId = 2L;
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		User friend = new User(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto friendDto = new UserDto(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		Friendship friendship = new Friendship(user, friend);
		Friendship savedFriendship = new Friendship(1L, user, friend, false);
		FriendshipDto savedFriendshipDto = new FriendshipDto(1L, userDto, friendDto, false);
		
		
		//Test case
		Mockito.when(userService.getOneUserByIdProtected(userId)).thenReturn(user);
		Mockito.when(userService.getOneUserByIdProtected(friendId)).thenReturn(friend);
		Mockito.when(friendshipRepository.save(friendship)).thenReturn(savedFriendship);
		Mockito.when(friendshipDtoConverter.convert(savedFriendship)).thenReturn(savedFriendshipDto);
		
		//Verify
		FriendshipDto result = friendshipService.sendFriendRequestToFriendId(userId, friendId);
		assertEquals(result, savedFriendshipDto);
		
		Mockito.verify(userService).getOneUserByIdProtected(userId);
		Mockito.verify(userService).getOneUserByIdProtected(friendId);
		Mockito.verify(friendshipRepository).save(friendship);
		Mockito.verify(friendshipDtoConverter).convert(savedFriendship);
	}
	
	@Test
	void testAcceptFriendshipRequest_whenCalledWithValidRequest_thenShouldReturnFriendshipDto() {
		//Data for testing
		Long userId = 1L;
		Long friendId = 2L;
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		User friend = new User(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto friendDto = new UserDto(2L, "test-username-2", "test-email-2@email.com", true, "test-password-2", 0);
		Friendship friendship = new Friendship(1L, user, friend, false);
		Friendship updatedFriendship = new Friendship(1L, user, friend, true);
		Friendship savedFriendship = new Friendship(1L, user, friend, true);
		FriendshipDto savedFriendshipDto = new FriendshipDto(1L, userDto, friendDto, false);
		FriendshipAcceptRequest friendshipAcceptRequest = new FriendshipAcceptRequest(userId, friendId);
		
		
		//Test case
		Mockito.when(friendshipRepository.findByFriendIdAndUserIdAndIsAcceptedFalse(userId, friendId)).thenReturn(Optional.of(friendship));
		Mockito.when(friendshipRepository.save(updatedFriendship)).thenReturn(savedFriendship);
		Mockito.when(friendshipDtoConverter.convert(savedFriendship)).thenReturn(savedFriendshipDto);
		
		//Verify
		FriendshipDto result = friendshipService.acceptFriendshipRequest(friendshipAcceptRequest);
		assertEquals(result, savedFriendshipDto);
		
		Mockito.verify(friendshipRepository).findByFriendIdAndUserIdAndIsAcceptedFalse(userId, friendId);
		Mockito.verify(friendshipRepository).save(updatedFriendship);
		Mockito.verify(friendshipDtoConverter).convert(savedFriendship);
	}
	
	@Test
	void testAcceptFriendshipRequest_whenCalledWithNonexistentFriendship_thenShouldThrowFriendshipNotFoundException() {
		//Data for testing
		Long userId = 1L;
		Long friendId = 2L;
		FriendshipAcceptRequest friendshipAcceptRequest = new FriendshipAcceptRequest(userId, friendId);
		
		//Test case
		Mockito.when(friendshipRepository.findByFriendIdAndUserIdAndIsAcceptedFalse(userId, friendId)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(FriendshipNotFoundException.class, () -> {
			friendshipService.acceptFriendshipRequest(friendshipAcceptRequest);
		});
		
		Mockito.verify(friendshipRepository).findByFriendIdAndUserIdAndIsAcceptedFalse(userId, friendId);
	}
	
	@Test
	void testIsFriendshipAlreadyRequested_whenCalledWithExistentFriendship_thenShouldReturnTrue() {
		//Data for testing
		Long userId = 1L;
		Long friendId = 2L;
		
		//Test case
		Mockito.when(friendshipRepository.existsByUserIdAndFriendIdAndIsAcceptedFalse(userId, friendId)).thenReturn(1L);
		
		//Verify
		boolean result = friendshipService.isFriendshipAlreadyRequested(userId, friendId);
		assertEquals(result, true);
		
		Mockito.verify(friendshipRepository).existsByUserIdAndFriendIdAndIsAcceptedFalse(userId, friendId);
	}
	
	@Test
	void testIsFriendshipAlreadyRequested_whenCalledWithNonexistentFriendship_thenShouldReturnFalse() {
		//Data for testing
		Long userId = 1L;
		Long friendId = 2L;
		
		//Test case
		Mockito.when(friendshipRepository.existsByUserIdAndFriendIdAndIsAcceptedFalse(userId, friendId)).thenReturn(0L);
		
		//Verify
		boolean result = friendshipService.isFriendshipAlreadyRequested(userId, friendId);
		assertEquals(result, false);
		
		Mockito.verify(friendshipRepository).existsByUserIdAndFriendIdAndIsAcceptedFalse(userId, friendId);
	}
	
	@Test
	void testIsFriendshipAlreadyReceived_whenCalledWithExistentFriendship_thenShouldReturnTrue() {
		//Data for testing
		Long userId = 1L;
		Long friendId = 2L;
		
		//Test case
		Mockito.when(friendshipRepository.existsByFriendIdAndUserIdAndIsAcceptedFalse(userId, friendId)).thenReturn(1L);
		
		//Verify
		boolean result = friendshipService.isFriendshipAlreadyReceived(userId, friendId);
		assertEquals(result, true);
		
		Mockito.verify(friendshipRepository).existsByFriendIdAndUserIdAndIsAcceptedFalse(userId, friendId);
	}
	
	@Test
	void testIsFriendshipAlreadyReceived_whenCalledWithNonexistentFriendship_thenShouldReturnFalse() {
		//Data for testing
		Long userId = 1L;
		Long friendId = 2L;
		
		//Test case
		Mockito.when(friendshipRepository.existsByFriendIdAndUserIdAndIsAcceptedFalse(userId, friendId)).thenReturn(0L);
		
		//Verify
		boolean result = friendshipService.isFriendshipAlreadyReceived(userId, friendId);
		assertEquals(result, false);
		
		Mockito.verify(friendshipRepository).existsByFriendIdAndUserIdAndIsAcceptedFalse(userId, friendId);
	}

	@Test
	void testIsFriendshipExist_whenCalledWithExistentFriendship_thenShouldReturnTrue() {
		//Data for testing
		Long userId = 1L;
		Long friendId = 2L;
		
		//Test case
		Mockito.when(friendshipRepository.existsFriendshipByFriendIdAndUserId(userId, friendId)).thenReturn(1L);
		
		//Verify
		boolean result = friendshipService.isFriendshipExist(userId, friendId);
		assertEquals(result, true);
		
		Mockito.verify(friendshipRepository).existsFriendshipByFriendIdAndUserId(userId, friendId);
	}
	
	@Test
	void testIsFriendshipExist_whenCalledWithNonexistentFriendship_thenShouldReturnFalse() {
		//Data for testing
		Long userId = 1L;
		Long friendId = 2L;
		
		//Test case
		Mockito.when(friendshipRepository.existsFriendshipByFriendIdAndUserId(userId, friendId)).thenReturn(0L);
		
		//Verify
		boolean result = friendshipService.isFriendshipExist(userId, friendId);
		assertEquals(result, false);
		
		Mockito.verify(friendshipRepository).existsFriendshipByFriendIdAndUserId(userId, friendId);
	}
}
