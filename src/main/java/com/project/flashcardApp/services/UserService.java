package com.project.flashcardApp.services;

import java.util.List;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.flashcardApp.dtos.UserDto;
import com.project.flashcardApp.dtos.UserDtoConverter;
import com.project.flashcardApp.entities.User;
import com.project.flashcardApp.exceptions.InvalidPasswordException;
import com.project.flashcardApp.exceptions.UserNotFoundException;
import com.project.flashcardApp.repository.UserRepository;
import com.project.flashcardApp.requests.UserChangeForgottenPasswordRequest;
import com.project.flashcardApp.requests.UserUpdateRequest;

@Service
public class UserService {
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final UserDtoConverter userDtoConverter;
	private final ChatService chatService;
	private final FriendshipService friendshipService;
	
	// Constructor injection
	public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, UserDtoConverter userDtoConverter,
			@Lazy ChatService chatService, @Lazy FriendshipService friendshipService) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userDtoConverter = userDtoConverter;
		this.chatService = chatService;
		this.friendshipService = friendshipService;
	}

	public List<UserDto> getAllUsers() {
		return userDtoConverter.convert(userRepository.findAll());
	}

	public UserDto saveOneUser(User userToCreate) {
		return userDtoConverter.convert(userRepository.save(userToCreate));
	}

	public UserDto getOneUserById(Long userId) {
		return userDtoConverter.convert(userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found.")));
	}
	
	protected User getOneUserByIdProtected(Long userId) {
		return userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found."));
	}

	public UserDto updateOneUserById(Long userId, UserUpdateRequest userToUpdate) {
		User foundUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found."));
		if(!passwordEncoder.matches(userToUpdate.getOldPassword(), foundUser.getPassword())) {
			throw new InvalidPasswordException("Password is wrong.");
		}
		
		foundUser.setPassword(passwordEncoder.encode(userToUpdate.getNewPassword()));
		return userDtoConverter.convert(userRepository.save(foundUser));
	}
	
	protected UserDto changeForgottenPassword(String email, UserChangeForgottenPasswordRequest userChangeForgottenPasswordRequest) {
		User foundUser = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found."));
		foundUser.setPassword(passwordEncoder.encode(userChangeForgottenPasswordRequest.getNewPassword()));
		return userDtoConverter.convert(userRepository.save(foundUser));
	}
	
	public UserDto updateScoreOfOneUserById(Long userId) {
		User foundUser = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("User not found."));
		foundUser.setScore(foundUser.getScore()+1);
		return userDtoConverter.convert(userRepository.save(foundUser));
	}

	@Transactional
	public void deleteOneUserById(Long userId) {
		chatService.deleteAllChatsByUserId(userId);
		friendshipService.deleteAllFriendshipsByUserId(userId);
		userRepository.deleteById(userId);
	}

	protected User getOneUserByUsername(String username) {
		return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found."));
	}
	
	protected User getOneUserByEmail(String email) {
		return userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found."));
	}
	
	protected boolean userExistsByUsername(String username) {
		return userRepository.existsByUsername(username);
	}
	
	protected boolean userExistsByEmail(String username) {
		return userRepository.existsByEmail(username);
	}

	protected void enableUser(String email) {
		User userToUpdate = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("User not found."));
		userToUpdate.setEnabled(true);
	}
}
