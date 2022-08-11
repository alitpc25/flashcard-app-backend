package com.project.flashcardApp.services;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.project.flashcardApp.dtos.EssayDto;
import com.project.flashcardApp.dtos.EssayDtoConverter;
import com.project.flashcardApp.dtos.UserDto;
import com.project.flashcardApp.entities.Essay;
import com.project.flashcardApp.entities.User;
import com.project.flashcardApp.exceptions.EssayNotFoundException;
import com.project.flashcardApp.exceptions.NoEssayFoundForUserException;
import com.project.flashcardApp.repository.EssayRepository;
import com.project.flashcardApp.requests.EssayRequest;

class EssayServiceTest {
	
	private EssayRepository essayRepository;
	private UserService userService;
	private EssayDtoConverter essayDtoConverter;
	
	private EssayService essayService;
	
	@BeforeEach
	void setUp() throws Exception {
		essayRepository = Mockito.mock(EssayRepository.class);
		userService = Mockito.mock(UserService.class);
		essayDtoConverter = Mockito.mock(EssayDtoConverter.class);
		
		essayService = new EssayService(essayRepository, userService, essayDtoConverter);
	}

	@Test
	void testGetAllEssaysPaginated_whenCalledWithValidRequest_thenShouldReturnPageOfEssayDto() {
		//Data for testing
		Integer pageSize = 1;
		Integer page = 1;
		Long userId = 1L;
		
		Pageable pageable = PageRequest.of(page-1,pageSize);
		
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		LocalDateTime publishedOn = LocalDateTime.now();
		Essay essay = new Essay(1L, user, "test-title", "test-text", publishedOn);
		Page<Essay> pagedEssays = new PageImpl<>(List.of(essay));
		
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		EssayDto essayDto = new EssayDto(1L, userDto, "test-title", "test-text", publishedOn);
		Page<EssayDto> pagedEssayDtos = new PageImpl<>(List.of(essayDto));
		
		
		//Test Case
		Mockito.when(essayRepository.findByUserId(1L, pageable)).thenReturn(Optional.of(pagedEssays));
		Mockito.when(essayDtoConverter.convert(pagedEssays)).thenReturn(pagedEssayDtos);
		
		//Verify
		Page<EssayDto> result = essayService.getAllEssaysPaginated(pageSize, page, userId);
		assertEquals(result, pagedEssayDtos);
		
		Mockito.verify(essayRepository).findByUserId(1L, pageable);
		Mockito.verify(essayDtoConverter).convert(pagedEssays);
		
	}
	
	@Test
	void testGetAllEssaysPaginated_whenCalledWithNonexistEssays_thenShouldThrowNoEssayFoundForUserException() {
		//Data for testing
		Integer pageSize = 1;
		Integer page = 1;
		Long userId = 1L;
		
		Pageable pageable = PageRequest.of(page-1,pageSize);
		
		
		//Test Case
		Mockito.when(essayRepository.findByUserId(1L, pageable)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(NoEssayFoundForUserException.class, () -> {
			essayService.getAllEssaysPaginated(pageSize, page, userId);
		});
		
		Mockito.verify(essayRepository).findByUserId(1L, pageable);
		
	}
	
	@Test
	void testGetAllEssaysPaginatedAndSortedByTimeDesc_whenCalledWithValidRequest_thenShouldReturnPageOfEssayDto() {
		//Data for testing
		Integer pageSize = 1;
		Integer page = 1;
		Long userId = 1L;
		
		Pageable pageable = PageRequest.of(page-1,pageSize, Sort.by("publishedOn").descending());
		
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		LocalDateTime publishedOn = LocalDateTime.now();
		Essay essay = new Essay(1L, user, "test-title", "test-text", publishedOn);
		Page<Essay> pagedEssays = new PageImpl<>(List.of(essay));
		
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		EssayDto essayDto = new EssayDto(1L, userDto, "test-title", "test-text", publishedOn);
		Page<EssayDto> pagedEssayDtos = new PageImpl<>(List.of(essayDto));
		
		
		//Test Case
		Mockito.when(essayRepository.findByUserId(1L, pageable)).thenReturn(Optional.of(pagedEssays));
		Mockito.when(essayDtoConverter.convert(pagedEssays)).thenReturn(pagedEssayDtos);
		
		//Verify
		Page<EssayDto> result = essayService.getAllEssaysPaginatedAndSortedByTimeDesc(pageSize, page, userId);
		assertEquals(result, pagedEssayDtos);
		
		Mockito.verify(essayRepository).findByUserId(1L, pageable);
		Mockito.verify(essayDtoConverter).convert(pagedEssays);
		
	}
	
	@Test
	void testGetAllEssaysPaginatedAndSortedByTimeDesc_whenCalledWithNonexistEssays_thenShouldThrowNoEssayFoundForUserException() {
		//Data for testing
		Integer pageSize = 1;
		Integer page = 1;
		Long userId = 1L;
		
		Pageable pageable = PageRequest.of(page-1,pageSize, Sort.by("publishedOn").descending());
		
		
		//Test Case
		Mockito.when(essayRepository.findByUserId(1L, pageable)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(NoEssayFoundForUserException.class, () -> {
			essayService.getAllEssaysPaginatedAndSortedByTimeDesc(pageSize, page, userId);
		});
		
		Mockito.verify(essayRepository).findByUserId(1L, pageable);
		
	}
	
	@Test
	void testGetAllEssaysPaginatedAndSortedByTimeAsc_whenCalledWithValidRequest_thenShouldReturnPageOfEssayDto() {
		//Data for testing
		Integer pageSize = 1;
		Integer page = 1;
		Long userId = 1L;
		
		Pageable pageable = PageRequest.of(page-1,pageSize, Sort.by("publishedOn").ascending());
		
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		LocalDateTime publishedOn = LocalDateTime.now();
		Essay essay = new Essay(1L, user, "test-title", "test-text", publishedOn);
		Page<Essay> pagedEssays = new PageImpl<>(List.of(essay));
		
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		EssayDto essayDto = new EssayDto(1L, userDto, "test-title", "test-text", publishedOn);
		Page<EssayDto> pagedEssayDtos = new PageImpl<>(List.of(essayDto));
		
		
		//Test Case
		Mockito.when(essayRepository.findByUserId(1L, pageable)).thenReturn(Optional.of(pagedEssays));
		Mockito.when(essayDtoConverter.convert(pagedEssays)).thenReturn(pagedEssayDtos);
		
		//Verify
		Page<EssayDto> result = essayService.getAllEssaysPaginatedAndSortedByTimeAsc(pageSize, page, userId);
		assertEquals(result, pagedEssayDtos);
		
		Mockito.verify(essayRepository).findByUserId(1L, pageable);
		Mockito.verify(essayDtoConverter).convert(pagedEssays);
		
	}
	
	@Test
	void testGetAllEssaysPaginatedAndSortedByTimeAsc_whenCalledWithNonexistEssays_thenShouldThrowNoEssayFoundForUserException() {
		//Data for testing
		Integer pageSize = 1;
		Integer page = 1;
		Long userId = 1L;
		
		Pageable pageable = PageRequest.of(page-1,pageSize, Sort.by("publishedOn").ascending());
		
		
		//Test Case
		Mockito.when(essayRepository.findByUserId(1L, pageable)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(NoEssayFoundForUserException.class, () -> {
			essayService.getAllEssaysPaginatedAndSortedByTimeAsc(pageSize, page, userId);
		});
		
		Mockito.verify(essayRepository).findByUserId(1L, pageable);
		
	}
	
	@Test
	void testGetAllEssaysSearchByTitle_whenCalledWithValidRequest_thenShouldReturnPageOfEssayDto() {
		//Data for testing
		Integer pageSize = 1;
		Integer page = 1;
		Long userId = 1L;
		
		Pageable pageable = PageRequest.of(page-1,pageSize, Sort.by("publishedOn").descending());
		
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		LocalDateTime publishedOn = LocalDateTime.now();
		Essay essay = new Essay(1L, user, "test-title", "test-text", publishedOn);
		Page<Essay> pagedEssays = new PageImpl<>(List.of(essay));
		
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		EssayDto essayDto = new EssayDto(1L, userDto, "test-title", "test-text", publishedOn);
		Page<EssayDto> pagedEssayDtos = new PageImpl<>(List.of(essayDto));
		
		
		//Test Case
		Mockito.when(essayRepository.findByTitleContainingIgnoreCaseAndUserId("test-title", pageable, 1L)).thenReturn(Optional.of(pagedEssays));
		Mockito.when(essayDtoConverter.convert(pagedEssays)).thenReturn(pagedEssayDtos);
		
		//Verify
		Page<EssayDto> result = essayService.getAllEssaysSearchByTitle(pageSize, page, userId, "test-title");
		assertEquals(result, pagedEssayDtos);
		
		Mockito.verify(essayRepository).findByTitleContainingIgnoreCaseAndUserId("test-title", pageable, 1L);
		Mockito.verify(essayDtoConverter).convert(pagedEssays);
		
	}
	
	@Test
	void testGetAllEssaysSearchByTitle_whenCalledWithNonexistEssays_thenShouldThrowNoEssayFoundForUserException() {
		//Data for testing
		Integer pageSize = 1;
		Integer page = 1;
		Long userId = 1L;
		
		Pageable pageable = PageRequest.of(page-1,pageSize, Sort.by("publishedOn").descending());
		
		
		//Test Case
		Mockito.when(essayRepository.findByTitleContainingIgnoreCaseAndUserId("test-wrong-title", pageable, 1L)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(NoEssayFoundForUserException.class, () -> {
			essayService.getAllEssaysSearchByTitle(pageSize, page, userId, "test-wrong-title");
		});
		
		Mockito.verify(essayRepository).findByTitleContainingIgnoreCaseAndUserId("test-wrong-title", pageable, 1L);
		
	}
	
	@Test
	void testGetOneEssayById_whenCalledWithValidRequest_thenShouldReturnEssayDto() {
		//Data for testing
		Long essayId = 1L;
		Long userId = 1L;
		
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		LocalDateTime publishedOn = LocalDateTime.now();
		Essay essay = new Essay(1L, user, "test-title", "test-text", publishedOn);
		
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		EssayDto essayDto = new EssayDto(1L, userDto, "test-title", "test-text", publishedOn);
		
		
		//Test Case
		Mockito.when(essayRepository.findByIdAndUserId(essayId, userId)).thenReturn(Optional.of(essay));
		Mockito.when(essayDtoConverter.convert(essay)).thenReturn(essayDto);
		
		//Verify
		EssayDto result = essayService.getOneEssayById(essayId, userId);
		assertEquals(result, essayDto);
		
		Mockito.verify(essayRepository).findByIdAndUserId(essayId, userId);
		Mockito.verify(essayDtoConverter).convert(essay);
		
	}
	
	@Test
	void testGetOneEssayById_whenCalledWithNonexistingEssay_thenShouldThrowEssayNotFoundException() {
		//Data for testing
		Long essayId = 1L;
		Long userId = 1L;
		
		//Test Case
		Mockito.when(essayRepository.findByIdAndUserId(essayId, userId)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(EssayNotFoundException.class, () -> {
			essayService.getOneEssayById(essayId, userId);
		});
		
		Mockito.verify(essayRepository).findByIdAndUserId(essayId, userId);
		
	}
	
	@Test
	void testInsertOneEssay_whenCalledWithValidRequest_thenShouldReturnEssayDto() {
		//Data for testing
		EssayRequest essayRequest = new EssayRequest("test-title","test-text", 1L);
		
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		LocalDateTime publishedOn = LocalDateTime.now();
		Essay essay = new Essay(user, essayRequest.getTitle(), essayRequest.getText(), publishedOn);
		Essay savedEssay = new Essay(1L, user, "test-title", "test-text", publishedOn);
		
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		EssayDto essayDto = new EssayDto(1L, userDto, "test-title","test-text", publishedOn);
		
		//Test Case
		Mockito.when(userService.getOneUserByIdProtected(essayRequest.getUserId())).thenReturn(user);
		Mockito.when(essayRepository.save(essay)).thenReturn(savedEssay);
		Mockito.when(essayDtoConverter.convert(savedEssay)).thenReturn(essayDto);
		
		//Verify
		EssayDto result = essayService.insertOneEssay(essayRequest);
		
		Mockito.verify(userService).getOneUserByIdProtected(essayRequest.getUserId());
		Mockito.verify(essayRepository).save(essay);
		Mockito.verify(essayDtoConverter).convert(savedEssay);
		
		assertEquals(essayDto, result);
	}
	
	@Test
	void testUpdateOneEssayById_whenCalledWithValidRequest_thenShouldReturnEssayDto() {
		//Data for testing
		Long essayId = 1L;
		Long userId = 1L;
		EssayRequest essayRequest = new EssayRequest("new-test-title","new-test-text",userId);
		
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		LocalDateTime publishedOn = LocalDateTime.now();
		Essay essay = new Essay(1L, user, "test-title", "test-text", publishedOn);
		
		LocalDateTime updatedOn = LocalDateTime.now();
		Essay toSaveEssay = new Essay(1L, user, essayRequest.getTitle(), essayRequest.getText(), updatedOn);
		Essay toSaveEssayReturn = new Essay(1L, user, essayRequest.getTitle(), essayRequest.getText(), updatedOn);
		
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		EssayDto toSaveEssayDto = new EssayDto(1L, userDto, "new-test-title", "new-test-text", updatedOn);
		
		//Test Case
		Mockito.when(essayRepository.findById(essayId)).thenReturn(Optional.of(essay));
		Mockito.when(essayRepository.save(toSaveEssay)).thenReturn(toSaveEssayReturn);
		Mockito.when(essayDtoConverter.convert(toSaveEssayReturn)).thenReturn(toSaveEssayDto);
		
		//Verify
		EssayDto result = essayService.updateOneEssayById(essayId, essayRequest);
		
		Mockito.verify(essayRepository).findById(essayId);
		Mockito.verify(essayRepository).save(toSaveEssay);
		Mockito.verify(essayDtoConverter).convert(toSaveEssayReturn);
		
		assertEquals(result, toSaveEssayDto);
		
	}
	
	@Test
	void testUpdateOneEssayById_whenCalledWithNonexistingEssay_thenShouldThrowEssayNotFoundException() {
		//Data for testing
		Long essayId = 1L;
		Long userId = 1L;
		EssayRequest essayRequest = new EssayRequest("new-test-title","new-test-text",userId);
		
		//Test Case
		Mockito.when(essayRepository.findById(essayId)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(EssayNotFoundException.class, () -> {
			essayService.updateOneEssayById(essayId, essayRequest);
		});
		
		Mockito.verify(essayRepository).findById(essayId);
		
	}
}
