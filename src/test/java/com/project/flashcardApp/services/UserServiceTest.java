package com.project.flashcardApp.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.project.flashcardApp.dtos.UserDto;
import com.project.flashcardApp.dtos.UserDtoConverter;
import com.project.flashcardApp.entities.User;
import com.project.flashcardApp.exceptions.InvalidPasswordException;
import com.project.flashcardApp.exceptions.UserNotFoundException;
import com.project.flashcardApp.repository.UserRepository;
import com.project.flashcardApp.requests.UserUpdateRequest;

class UserServiceTest {
	
	private UserRepository userRepository;
	private PasswordEncoder passwordEncoder;
	private UserDtoConverter userDtoConverter;
	private ChatService chatService;
	private FriendshipService friendshipService;
	
	private UserService userService;

	@BeforeEach
	void setUp() throws Exception {
		userRepository = Mockito.mock(UserRepository.class);
		passwordEncoder = Mockito.mock(PasswordEncoder.class);
		userDtoConverter = Mockito.mock(UserDtoConverter.class);
		chatService = Mockito.mock(ChatService.class);
		friendshipService = Mockito.mock(FriendshipService.class);
		
		userService = new UserService(userRepository, passwordEncoder, userDtoConverter, chatService, friendshipService);
	}

	@Test
	void testGetAllUsers_whenCalledWithValidRequest_thenShouldReturnListOfUserDto() {
		//Data
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		List<User> users = List.of(user);
		List<UserDto> usersDto = List.of(userDto);
		
		//Test case
		Mockito.when(userRepository.findAll()).thenReturn(users);
		Mockito.when(userDtoConverter.convert(users)).thenReturn(usersDto);
		
		//Verify
		List<UserDto> result = userService.getAllUsers();
		assertEquals(result, usersDto);
		
		Mockito.verify(userRepository).findAll();
		Mockito.verify(userDtoConverter).convert(users);
		
	}
	
	@Test
	void testSaveOneUser_whenCalledWithValidRequest_thenShouldReturnUserDto() {
		//Data
		User user = new User("test-username", "test-email@email.com", true, "test-password", 0);
		User savedUser = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		
		
		//Test case
		Mockito.when(userRepository.save(user)).thenReturn(savedUser);
		Mockito.when(userDtoConverter.convert(savedUser)).thenReturn(userDto);
		
		//Verify
		UserDto result = userService.saveOneUser(user);
		assertEquals(result, userDto);
		
		Mockito.verify(userRepository).save(user);
		Mockito.verify(userDtoConverter).convert(savedUser);
		
	}
	
	@Test
	void testGetOneUserById_whenCalledWithValidRequest_thenShouldReturnUserDto() {
		//Data
		Long userId = 1L;
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		
		
		//Test case
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		Mockito.when(userDtoConverter.convert(user)).thenReturn(userDto);
		
		//Verify
		UserDto result = userService.getOneUserById(userId);
		assertEquals(result, userDto);
		
		Mockito.verify(userRepository).findById(userId);
		Mockito.verify(userDtoConverter).convert(user);
		
	}
	
	@Test
	void testGetOneUserById_whenCalledWithNonexistentUserId_thenShouldThrowUserNotFoundException() {
		//Data
		Long userId = 1L;
		
		
		//Test case
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(UserNotFoundException.class, () -> {
			userService.getOneUserById(userId);
		});
		
		Mockito.verify(userRepository).findById(userId);
		
	}
	
	@Test
	void testGetOneUserByIdProtected_whenCalledWithValidRequest_thenShouldReturnUserDto() {
		//Data
		Long userId = 1L;
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		
		
		//Test case
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		
		//Verify
		User result = userService.getOneUserByIdProtected(userId);
		assertEquals(result, user);
		
		Mockito.verify(userRepository).findById(userId);
		
	}
	
	@Test
	void testGetOneUserByIdProtected_whenCalledWithNonexistentUserId_thenShouldThrowUserNotFoundException() {
		//Data
		Long userId = 1L;
		
		
		//Test case
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(UserNotFoundException.class, () -> {
			userService.getOneUserByIdProtected(userId);
		});
		
		Mockito.verify(userRepository).findById(userId);
		
	}
	
	// TODO: Password encoder'i test etmeye gerek var mı?
	@Test
	void testUpdateOneUserById_whenCalledWithValidRequest_thenShouldReturnUserDto() {
		//Data
		Long userId = 1L;
		UserUpdateRequest request = new UserUpdateRequest("test-password", "test-new-password");
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		User userToUpdate = new User(1L, "test-username", "test-email@email.com", true, passwordEncoder.encode("test-new-password"), 0);
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-new-password", 0);
		
		
		//Test case
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		Mockito.when(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).thenReturn(true);
		Mockito.when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);
		Mockito.when(userDtoConverter.convert(userToUpdate)).thenReturn(userDto);
		
		//Verify
		UserDto result = userService.updateOneUserById(userId, request);
		assertEquals(result, userDto);
		
		Mockito.verify(userRepository).findById(userId);
		Mockito.verify(userRepository).save(userToUpdate);
		Mockito.verify(userDtoConverter).convert(userToUpdate);
		
	}
	
	@Test
	void testUpdateOneUserById_whenCalledWithNonexistentUserId_thenShouldThrowUserNotFoundException() {
		//Data
		Long userId = 1L;
		UserUpdateRequest request = new UserUpdateRequest("test-password", "test-new-password");
		
		//Test case
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(UserNotFoundException.class, () -> {
			userService.updateOneUserById(userId, request);
		});
		
		Mockito.verify(userRepository).findById(userId);
		
	}
	
	@Test
	void testUpdateOneUserById_whenCalledWithUnmatchedPassword_thenShouldThrowInvalidPasswordException() {
		//Data
		Long userId = 1L;
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserUpdateRequest request = new UserUpdateRequest("test-password", "test-new-password");
		
		//Test case
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		Mockito.when(passwordEncoder.matches(request.getOldPassword(), user.getPassword())).thenReturn(false);
		
		//Verify
		assertThrows(InvalidPasswordException.class, () -> {
			userService.updateOneUserById(userId, request);
		});
		
		Mockito.verify(userRepository).findById(userId);
		Mockito.verify(passwordEncoder).matches(request.getOldPassword(), user.getPassword());
		
	}
	
	@Test
	void testUpdateScoreOfOneUserById_whenCalledWithValidRequest_thenShouldReturnUserDto() {
		//Data
		Long userId = 1L;
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		User userToUpdate = new User(1L, "test-username", "test-email@email.com", true, "test-password", 1);
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 1);
		
		
		//Test case
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
		Mockito.when(userRepository.save(userToUpdate)).thenReturn(userToUpdate);
		Mockito.when(userDtoConverter.convert(userToUpdate)).thenReturn(userDto);
		
		//Verify
		UserDto result = userService.updateScoreOfOneUserById(userId);
		assertEquals(result, userDto);
		
		Mockito.verify(userRepository).findById(userId);
		Mockito.verify(userRepository).save(userToUpdate);
		Mockito.verify(userDtoConverter).convert(userToUpdate);
		
	}
	
	@Test
	void testUpdateScoreOfOneUserById_whenCalledWithNonexistentUserId_thenShouldThrowUserNotFoundException() {
		//Data
		Long userId = 1L;
		
		
		//Test case
		Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(UserNotFoundException.class, () -> {
			userService.updateScoreOfOneUserById(userId);
		});
		
		Mockito.verify(userRepository).findById(userId);
		
	}

	@Test
	void testGetOneUserByUsername_whenCalledWithValidRequest_thenShouldReturnUser() {
		//Data
		String username = "test-username";
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		
		//Test case
		Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));
		
		//Verify
		User result = userService.getOneUserByUsername(username);
		assertEquals(result, user);
		
		Mockito.verify(userRepository).findByUsername(username);
		
	}
	
	@Test
	void testGetOneUserByUsername_whenCalledWithNonexistentUsername_thenShouldThrowUserNotFoundException() {
		//Data
		String username = "test-username";
		
		//Test case
		Mockito.when(userRepository.findByUsername(username)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(UserNotFoundException.class, () -> {
			userService.getOneUserByUsername(username);
		});
		
		Mockito.verify(userRepository).findByUsername(username);
		
	}
	
	@Test
	void testGetOneUserByEmail_whenCalledWithValidRequest_thenShouldReturnUser() {
		//Data
		String email = "test-email@email.com";
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		
		//Test case
		Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
		
		//Verify
		User result = userService.getOneUserByEmail(email);
		assertEquals(result, user);
		
		Mockito.verify(userRepository).findByEmail(email);
		
	}
	
	@Test
	void testGetOneUserByEmail_whenCalledWithNonexistentEmail_thenShouldThrowUserNotFoundException() {
		//Data
		String email = "test-email@email.com";
		
		//Test case
		Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(UserNotFoundException.class, () -> {
			userService.getOneUserByEmail(email);
		});
		
		Mockito.verify(userRepository).findByEmail(email);
		
	}
	
	@Test
	void testUserExistsByUsername_whenCalledWithValidRequest_thenShouldReturnTrue() {
		//Data
		String username = "test-username";
		
		//Test case
		Mockito.when(userRepository.existsByUsername(username)).thenReturn(true);
		
		//Verify
		boolean result = userService.userExistsByUsername(username);
		assertEquals(result, true);
		
		Mockito.verify(userRepository).existsByUsername(username);
		
	}
	
	@Test
	void testUserExistsByUsername_whenCalledWithNonexistentUsername_thenShouldReturnFalse() {
		//Data
		String username = "test-username";
		
		//Test case
		Mockito.when(userRepository.existsByUsername(username)).thenReturn(false);
		
		//Verify
		boolean result = userService.userExistsByUsername(username);
		assertEquals(result, false);
		
		Mockito.verify(userRepository).existsByUsername(username);
		
	}
	
	@Test
	void testUserExistsByEmail_whenCalledWithValidRequest_thenShouldReturnTrue() {
		//Data
		String email = "test-email@email.com";
		
		//Test case
		Mockito.when(userRepository.existsByEmail(email)).thenReturn(true);
		
		//Verify
		boolean result = userService.userExistsByEmail(email);
		assertEquals(result, true);
		
		Mockito.verify(userRepository).existsByEmail(email);
		
	}
	
	@Test
	void testUserExistsByEmail_whenCalledWithNonexistentEmail_thenShouldReturnFalse() {
		//Data
		String email = "test-email@email.com";
		
		//Test case
		Mockito.when(userRepository.existsByEmail(email)).thenReturn(false);
		
		//Verify
		boolean result = userService.userExistsByEmail(email);
		assertEquals(result, false);
		
		Mockito.verify(userRepository).existsByEmail(email);
		
	}

}
