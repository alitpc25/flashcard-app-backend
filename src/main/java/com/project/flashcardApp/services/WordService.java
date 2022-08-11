package com.project.flashcardApp.services;

import java.util.ArrayList;
import java.util.List;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

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

@Service
public class WordService {
	
	private final WordRepository wordRepository;
	private final UserService userService;
	private final WordDtoConverter wordDtoConverter;

	public WordService(WordRepository wordRepository, UserService userService, WordDtoConverter wordDtoConverter) {
		this.wordRepository = wordRepository;
		this.userService = userService;
		this.wordDtoConverter = wordDtoConverter;
	}
	
	
	public List<WordDto> getAllWords(Long userId) {
		List<Word> words = wordRepository.findByUserId(userId).orElseThrow(() -> new WordNotFoundException("Word not found."));
		return wordDtoConverter.convert(words);
	}
	
	public Page<WordDto> getAllWordsPaginated(Integer pageSize, Integer page, Long userId) {
		Pageable pageable = PageRequest.of(page-1,pageSize);
		Page<Word> words = wordRepository.findByUserId(userId, pageable).orElseThrow(() -> new WordNotFoundException("Word not found."));
		return wordDtoConverter.convert(words);
	}

	public WordDto insertOneWord(WordInsertRequest wordInsertRequest) {
		User user = userService.getOneUserByIdProtected(wordInsertRequest.getUserId());
		if (wordRepository.findByEnglishWordAndUserId(wordInsertRequest.getEnglishWord(), wordInsertRequest.getUserId()).isPresent()) {
			throw new WordAlreadyExistException("Word already inserted.");
		}
		
		Word toSaveWord = new Word(user, wordInsertRequest.getEnglishWord(), wordInsertRequest.getTurkishWord());
		return wordDtoConverter.convert(wordRepository.save(toSaveWord));
	}

	public WordDto getOneWordById(Long wordId) {
		return wordDtoConverter.convert(wordRepository.findById(wordId).orElseThrow(() -> new WordNotFoundException("Word not found.")));
	}
	
	public List<WordDto> getFourDifferentWords(Long userId) {
		List<Word> words = wordRepository.findByUserId(userId).orElseThrow(() -> new WordNotFoundException("Word not found."));
		if (words.size() < 10) {
			throw new PracticeNotAllowedException("User must have more than 10 words to be able to practice.");
		}
		ArrayList<Integer> randomNumbers = new ArrayList<>();
		List<Word> fourDifferentWords = new ArrayList<>();
		while(randomNumbers.size() < 4) {
			int randomNum = (int) Math.floor(Math.random() * words.size());
			if (!randomNumbers.contains(randomNum)) {
				randomNumbers.add(randomNum);
				fourDifferentWords.add(words.get(randomNum));
			}
		}
		return wordDtoConverter.convert(fourDifferentWords);
	}

	public WordDto updateOneWordById(Long wordId, WordUpdateRequest wordUpdateRequest) {
		Word word = wordRepository.findById(wordId).orElseThrow(() -> new WordNotFoundException("Word not found."));
		word.setEnglishWord(wordUpdateRequest.getEnglishWord());
		word.setTurkishWord(wordUpdateRequest.getTurkishWord());
		wordRepository.save(word);
		return wordDtoConverter.convert(word);
	}

	public void deleteOneWordById(Long wordId) {
		wordRepository.deleteById(wordId);
	}

}
