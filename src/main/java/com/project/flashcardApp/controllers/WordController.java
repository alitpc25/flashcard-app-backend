package com.project.flashcardApp.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.flashcardApp.dtos.WordDto;
import com.project.flashcardApp.requests.WordInsertRequest;
import com.project.flashcardApp.requests.WordUpdateRequest;
import com.project.flashcardApp.services.WordService;
@RestController
@RequestMapping("/words")
public class WordController {
	
	private WordService wordService;

	public WordController(WordService wordService) {
		this.wordService = wordService;
	}
	
	@GetMapping("/allWords")
	public ResponseEntity<List<WordDto>> getAllWords(@RequestParam Long userId) {
		return ResponseEntity.ok(wordService.getAllWords(userId));
	}
	
	@GetMapping
	public ResponseEntity<Page<WordDto>> getAllWordsPaginated(@RequestParam Integer pageSize, @RequestParam Integer page, @RequestParam Long userId) {
		return ResponseEntity.ok(wordService.getAllWordsPaginated(pageSize, page, userId));
	}
	
	@PostMapping
	public ResponseEntity<WordDto> insertOneWord(@RequestBody WordInsertRequest wordInsertRequest) {
		return ResponseEntity.ok(wordService.insertOneWord(wordInsertRequest));
	}
	
	@GetMapping("/{wordId}")
	public ResponseEntity<WordDto> getOneWordById(@PathVariable Long wordId) {
		return ResponseEntity.ok(wordService.getOneWordById(wordId));
	}
	
	@GetMapping("/getFourDifferentWords/{userId}")
	public ResponseEntity<List<WordDto>> getFourDifferentWords(@PathVariable Long userId) {
		return ResponseEntity.ok(wordService.getFourDifferentWords(userId));
	}
	
	@PutMapping("/{wordId}")
	public ResponseEntity<WordDto> updateOneWordById(@PathVariable Long wordId, @RequestBody WordUpdateRequest wordUpdateRequest) {
		return ResponseEntity.ok(wordService.updateOneWordById(wordId, wordUpdateRequest));
	}
	
	@DeleteMapping("/{wordId}")
	public void deleteOneWordById(@PathVariable Long wordId) {
		wordService.deleteOneWordById(wordId);
	}
	

}
