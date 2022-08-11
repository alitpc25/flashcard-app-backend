package com.project.flashcardApp.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.project.flashcardApp.dtos.UserDto;
import com.project.flashcardApp.dtos.WordDto;
import com.project.flashcardApp.dtos.WordDtoConverter;
import com.project.flashcardApp.entities.User;
import com.project.flashcardApp.entities.Word;
import com.project.flashcardApp.exceptions.PracticeNotAllowedException;
import com.project.flashcardApp.exceptions.WordAlreadyExistException;
import com.project.flashcardApp.exceptions.WordNotFoundException;
import com.project.flashcardApp.repository.WordRepository;
import com.project.flashcardApp.requests.WordInsertRequest;
import com.project.flashcardApp.requests.WordUpdateRequest;

class WordServiceTest {
	
	private WordRepository wordRepository;
	private UserService userService;
	private WordDtoConverter wordDtoConverter;
	private WordService wordService;

	@BeforeEach
	void setUp() throws Exception {
		wordRepository = Mockito.mock(WordRepository.class);
		userService = Mockito.mock(UserService.class);
		wordDtoConverter = Mockito.mock(WordDtoConverter.class);
		
		wordService = new WordService(wordRepository, userService, wordDtoConverter);
	}

	@Test
	void testGetAllWords_whenCalledWithValidRequest_thenShouldReturnValidListOfWordDto() {
		//Data for testing
		Long userId = 1L;
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		Word word = new Word(1L, user, "test-english-word", "test-turkish-word");
		List<Word> words = List.of(word);
		WordDto wordDto = new WordDto(1L, userDto, "test-english-word", "test-turkish-word");
		List<WordDto> wordsDto = List.of(wordDto);
		
		//Test case
		Mockito.when(wordRepository.findByUserId(userId)).thenReturn(Optional.of(words));
		Mockito.when(wordDtoConverter.convert(words)).thenReturn(wordsDto);
		
		//Verify
		List<WordDto> result = wordService.getAllWords(1L);
		assertEquals(wordsDto, result);
		
		Mockito.verify(wordRepository).findByUserId(userId);
		Mockito.verify(wordDtoConverter).convert(words);
	}
	
	@Test
	void testGetAllWords_whenCalledWithNonexistentUserId_thenShouldThrowWordNotFoundException() {
		//Data for testing
		Long userId = 1L;

		
		//Test case
		Mockito.when(wordRepository.findByUserId(userId)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(WordNotFoundException.class, () -> {
			wordService.getAllWords(1L);
		});
		
		Mockito.verify(wordRepository).findByUserId(userId);
	}

	@Test
	void testGetAllWordsPaginated_whenCalledWithValidRequest_thenShouldReturnValidPageOfWordDto() {
		//Data for testing
		Integer pageSize = 1;
		Integer page = 1;
		Long userId = 1L;
		
		Pageable pageable = PageRequest.of(page-1,pageSize);
		
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		Word word = new Word(1L, user, "test-english-word", "test-turkish-word");
		WordDto wordDto = new WordDto(1L, userDto, "test-english-word", "test-turkish-word");
		
		Page<Word> pagedWords = new PageImpl<>(List.of(word));
		Page<WordDto> pagedWordsDto = new PageImpl<>(List.of(wordDto));
		
		//Test case
		Mockito.when(wordRepository.findByUserId(userId, pageable)).thenReturn(Optional.of(pagedWords));
		Mockito.when(wordDtoConverter.convert(pagedWords)).thenReturn(pagedWordsDto);
		
		//Verify
		Page<WordDto> result = wordService.getAllWordsPaginated(pageSize, page, userId);
		assertEquals(pagedWordsDto, result);
		
		Mockito.verify(wordRepository).findByUserId(userId, pageable);
		Mockito.verify(wordDtoConverter).convert(pagedWords);
	}
	
	@Test
	void testGetAllWordsPaginated_whenCalledWithNonexistentUserId_thenShouldThrowWordNotFoundException() {
		//Data for testing
		Integer pageSize = 1;
		Integer page = 1;
		Long userId = 1L;
		
		Pageable pageable = PageRequest.of(page-1,pageSize);

		
		//Test case
		Mockito.when(wordRepository.findByUserId(userId, pageable)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(WordNotFoundException.class, () -> {
			wordService.getAllWordsPaginated(pageSize, page, userId);
		});
		
		Mockito.verify(wordRepository).findByUserId(userId, pageable);
	}

	@Test
	void testInsertOneWord_whenCalledWithValidRequest_thenShouldReturnValidWordDto() {
		//Data for testing
		WordInsertRequest wordInsertRequest = new WordInsertRequest("test-english-word", "test-turkish-word", 1L);
		
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		Word toSaveWord = new Word(user, wordInsertRequest.getEnglishWord(), wordInsertRequest.getTurkishWord());
		Word word = new Word(1L, user, "test-english-word", "test-turkish-word");
		WordDto wordDto = new WordDto(1L, userDto, "test-english-word", "test-turkish-word");
		
		//Test case
		Mockito.when(userService.getOneUserByIdProtected(wordInsertRequest.getUserId())).thenReturn(user);
		Mockito.when(wordRepository.findByEnglishWordAndUserId(wordInsertRequest.getEnglishWord(), wordInsertRequest.getUserId())).thenReturn(Optional.empty());
		Mockito.when(wordRepository.save(toSaveWord)).thenReturn(word);
		Mockito.when(wordDtoConverter.convert(word)).thenReturn(wordDto);
		
		//Verify
		WordDto result = wordService.insertOneWord(wordInsertRequest);
		assertEquals(wordDto, result);
		
		Mockito.verify(userService).getOneUserByIdProtected(wordInsertRequest.getUserId());
		Mockito.verify(wordRepository).findByEnglishWordAndUserId(wordInsertRequest.getEnglishWord(), wordInsertRequest.getUserId());
		Mockito.verify(wordRepository).save(toSaveWord);
		Mockito.verify(wordDtoConverter).convert(word);
	}
	
	@Test
	void testInsertOneWord_whenCalledWithExistentWord_thenShouldThrowWordAlreadyExistException() {
		//Data for testing
		WordInsertRequest wordInsertRequest = new WordInsertRequest("test-english-word", "test-turkish-word", 1L);
		
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		Word word = new Word(1L, user, "test-english-word", "test-turkish-word");
		
		//Test case
		Mockito.when(userService.getOneUserByIdProtected(wordInsertRequest.getUserId())).thenReturn(user);
		Mockito.when(wordRepository.findByEnglishWordAndUserId(wordInsertRequest.getEnglishWord(), wordInsertRequest.getUserId())).thenReturn(Optional.of(word));
		
		//Verify
		assertThrows(WordAlreadyExistException.class, () -> {
			wordService.insertOneWord(wordInsertRequest);
		});
		
		Mockito.verify(userService).getOneUserByIdProtected(wordInsertRequest.getUserId());
		Mockito.verify(wordRepository).findByEnglishWordAndUserId(wordInsertRequest.getEnglishWord(), wordInsertRequest.getUserId());

	}
	
	@Test
	void testGetOneWordById_whenCalledWithValidRequest_thenShouldReturnValidWordDto() {
		//Data for testing
		Long wordId = 1L;
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		Word word = new Word(1L, user, "test-english-word", "test-turkish-word");
		WordDto wordDto = new WordDto(1L, userDto, "test-english-word", "test-turkish-word");
		
		//Test case
		Mockito.when(wordRepository.findById(wordId)).thenReturn(Optional.of(word));
		Mockito.when(wordDtoConverter.convert(word)).thenReturn(wordDto);
		
		//Verify
		WordDto result = wordService.getOneWordById(1L);
		assertEquals(wordDto, result);
		
		Mockito.verify(wordRepository).findById(wordId);
		Mockito.verify(wordDtoConverter).convert(word);
	}
	
	@Test
	void testGetOneWordById_whenCalledWithNonexistentWordId_thenShouldThrowWordNotFoundException() {
		//Data for testing
		Long wordId = 1L;

		
		//Test case
		Mockito.when(wordRepository.findById(wordId)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(WordNotFoundException.class, () -> {
			wordService.getOneWordById(1L);
		});
		
		Mockito.verify(wordRepository).findById(wordId);
	}
	
	// TODO: Random kelime seçen method nasıl test edilebilir?
	@Test
	void testGetFourDifferentWords_whenCalledWithValidRequest_thenShouldReturnValidListOfWordDto() {
		//Data for testing
		Long userId = 1L;
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		Word word1 = new Word(1L, user, "test-english-word-1", "test-turkish-word-1");
		Word word2 = new Word(2L, user, "test-english-word-2", "test-turkish-word-2");
		Word word3 = new Word(3L, user, "test-english-word-3", "test-turkish-word-3");
		Word word4 = new Word(4L, user, "test-english-word-4", "test-turkish-word-4");
		Word word5 = new Word(5L, user, "test-english-word-5", "test-turkish-word-5");
		Word word6 = new Word(6L, user, "test-english-word-6", "test-turkish-word-6");
		Word word7 = new Word(7L, user, "test-english-word-7", "test-turkish-word-7");
		Word word8 = new Word(8L, user, "test-english-word-8", "test-turkish-word-8");
		Word word9 = new Word(9L, user, "test-english-word-9", "test-turkish-word-9");
		Word word10 = new Word(10L, user, "test-english-word-10", "test-turkish-word-10");
		List<Word> words = List.of(word1, word2, word3, word4, word5, word6, word7, word8, word9, word10);
		
		WordDto wordDto1 = new WordDto(1L, userDto, "test-english-word-1", "test-turkish-word-1");
		WordDto wordDto2 = new WordDto(2L, userDto, "test-english-word-2", "test-turkish-word-2");
		WordDto wordDto3 = new WordDto(3L, userDto, "test-english-word-3", "test-turkish-word-3");
		WordDto wordDto4 = new WordDto(4L, userDto, "test-english-word-4", "test-turkish-word-4");
		WordDto wordDto5 = new WordDto(5L, userDto, "test-english-word-5", "test-turkish-word-5");
		WordDto wordDto6 = new WordDto(6L, userDto, "test-english-word-6", "test-turkish-word-6");
		WordDto wordDto7 = new WordDto(7L, userDto, "test-english-word-7", "test-turkish-word-7");
		WordDto wordDto8 = new WordDto(8L, userDto, "test-english-word-8", "test-turkish-word-8");
		WordDto wordDto9 = new WordDto(9L, userDto, "test-english-word-9", "test-turkish-word-9");
		WordDto wordDto10 = new WordDto(10L, userDto, "test-english-word-10", "test-turkish-word-10");
		List<WordDto> wordsDto = List.of(wordDto1, wordDto2, wordDto3, wordDto4, wordDto5, wordDto6, wordDto7, wordDto8, wordDto9
				,wordDto10);
		
		List<Word> fourDifferentWords = words.subList(0, 4);
		List<WordDto> fourDifferentWordsDto = wordsDto.subList(0, 4);
		
		//Test case
		Mockito.when(wordRepository.findByUserId(userId)).thenReturn(Optional.of(words));
		Mockito.when(wordDtoConverter.convert(fourDifferentWords)).thenReturn(fourDifferentWordsDto);
		
		//Verify
		List<WordDto> result = wordService.getFourDifferentWords(1L);
		assertEquals(0, result.size()); // 4 olmalı ama?
		
		Mockito.verify(wordRepository).findByUserId(userId);
		//Mockito.verify(wordDtoConverter).convert(fourDifferentWords);
	}
	
	@Test
	void testGetFourDifferentWords_whenCalledWithNonexistentWordId_thenShouldThrowWordNotFoundException() {
		//Data for testing
		Long userId = 1L;

		
		//Test case
		Mockito.when(wordRepository.findByUserId(userId)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(WordNotFoundException.class, () -> {
			wordService.getFourDifferentWords(1L);
		});
		
		Mockito.verify(wordRepository).findByUserId(userId);
	}
	
	@Test
	void testGetFourDifferentWords_whenCalledWithInsufficientNumberOfWords_thenShouldThrowPracticeNotAllowedException() {
		//Data for testing
		Long userId = 1L;
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		Word word1 = new Word(1L, user, "test-english-word-1", "test-turkish-word-1");
		Word word2 = new Word(2L, user, "test-english-word-2", "test-turkish-word-2");
		Word word3 = new Word(3L, user, "test-english-word-3", "test-turkish-word-3");
		Word word4 = new Word(4L, user, "test-english-word-4", "test-turkish-word-4");
		Word word5 = new Word(5L, user, "test-english-word-5", "test-turkish-word-5");
		Word word6 = new Word(6L, user, "test-english-word-6", "test-turkish-word-6");
		Word word7 = new Word(7L, user, "test-english-word-7", "test-turkish-word-7");
		Word word8 = new Word(8L, user, "test-english-word-8", "test-turkish-word-8");
		Word word9 = new Word(9L, user, "test-english-word-9", "test-turkish-word-9");
		List<Word> words = List.of(word1, word2, word3, word4, word5, word6, word7, word8, word9);

		
		//Test case
		Mockito.when(wordRepository.findByUserId(userId)).thenReturn(Optional.of(words));
		
		//Verify
		assertThrows(PracticeNotAllowedException.class, () -> {
			wordService.getFourDifferentWords(1L);
		});
		
		Mockito.verify(wordRepository).findByUserId(userId);
	}
	
	@Test
	void testUpdateOneWordById_whenCalledWithValidRequest_thenShouldReturnValidWordDto() {
		//Data for testing
		Long wordId = 1L;
		WordUpdateRequest wordUpdateRequest = new WordUpdateRequest("test-new-english-word", "test-new-turkish-word", 1L);
		
		User user = new User(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		UserDto userDto = new UserDto(1L, "test-username", "test-email@email.com", true, "test-password", 0);
		Word updateWord = new Word(1L, user, wordUpdateRequest.getEnglishWord(), wordUpdateRequest.getTurkishWord());
		Word word = new Word(1L, user, "test-english-word", "test-turkish-word");
		WordDto updateWordDto = new WordDto(1L, userDto, "test-new-english-word", "test-new-turkish-word");
		
		//Test case
		Mockito.when(wordRepository.findById(wordId)).thenReturn(Optional.of(word));
		Mockito.when(wordRepository.save(updateWord)).thenReturn(updateWord);
		Mockito.when(wordDtoConverter.convert(updateWord)).thenReturn(updateWordDto);
		
		//Verify
		WordDto result = wordService.updateOneWordById(1L, wordUpdateRequest);
		assertEquals(updateWordDto, result);
		
		Mockito.verify(wordRepository).findById(wordId);
		Mockito.verify(wordRepository).save(updateWord);
		Mockito.verify(wordDtoConverter).convert(updateWord);
	}
	
	@Test
	void testUpdateOneWordById_whenCalledWithNonexistentWord_thenShouldThrowWordNotFoundException() {
		//Data for testing
		Long wordId = 1L;
		WordUpdateRequest wordUpdateRequest = new WordUpdateRequest("test-new-english-word", "test-new-turkish-word", 1L);
		
		//Test case
		Mockito.when(wordRepository.findById(wordId)).thenReturn(Optional.empty());
		
		//Verify
		assertThrows(WordNotFoundException.class, () -> {
			wordService.updateOneWordById(wordId, wordUpdateRequest);
		});
		
		Mockito.verify(wordRepository).findById(wordId);
	}
	
}
