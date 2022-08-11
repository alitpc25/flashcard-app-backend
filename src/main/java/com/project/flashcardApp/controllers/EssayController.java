package com.project.flashcardApp.controllers;

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

import com.project.flashcardApp.dtos.EssayDto;
import com.project.flashcardApp.requests.EssayRequest;
import com.project.flashcardApp.services.EssayService;

@RestController
@RequestMapping("/essays")
public class EssayController {
	
	private final EssayService essayService;
	public EssayController(EssayService essayService) {
		this.essayService = essayService;
	}
	
	@GetMapping
	public ResponseEntity<Page<EssayDto>> getAllEssaysPaginated(@RequestParam Integer pageSize, @RequestParam Integer page, @RequestParam Long userId) {
		return ResponseEntity.ok(essayService.getAllEssaysPaginated(pageSize, page, userId));
	}
	
	@GetMapping("/sortByTimeDesc")
	public ResponseEntity<Page<EssayDto>> getAllEssaysPaginatedAndSortedByTimeDesc(@RequestParam Integer pageSize, @RequestParam Integer page, @RequestParam Long userId) {
		return ResponseEntity.ok(essayService.getAllEssaysPaginatedAndSortedByTimeDesc(pageSize, page, userId));
	}
	
	@GetMapping("/sortByTimeAsc")
	public ResponseEntity<Page<EssayDto>> getAllEssaysPaginatedAndSortedByTimeAsc(@RequestParam Integer pageSize, @RequestParam Integer page, @RequestParam Long userId) {
		return ResponseEntity.ok(essayService.getAllEssaysPaginatedAndSortedByTimeAsc(pageSize, page, userId));
	}
	
	@GetMapping("/searchByTitle")
	public ResponseEntity<Page<EssayDto>> getAllEssaysSearchByTitle(@RequestParam Integer pageSize, @RequestParam Integer page, @RequestParam Long userId, @RequestParam String title) {
		return ResponseEntity.ok(essayService.getAllEssaysSearchByTitle(pageSize, page, userId, title));
	}
	
	@GetMapping("/essay")
	public ResponseEntity<EssayDto> getOneEssayById(@RequestParam Long essayId, @RequestParam Long userId) {
		return ResponseEntity.ok(essayService.getOneEssayById(essayId, userId));
	}
	
	@PostMapping()
	public ResponseEntity<EssayDto> insertOneEssay( @RequestBody EssayRequest essayRequest) {
		return ResponseEntity.ok(essayService.insertOneEssay(essayRequest));
	}
	
	@PutMapping("/{essayId}")
	public ResponseEntity<EssayDto> updateOneEssayById(@PathVariable("essayId") Long essayId, @RequestBody EssayRequest essayRequest) {
		return ResponseEntity.ok(essayService.updateOneEssayById(essayId, essayRequest));
	}
	
	@DeleteMapping("/{essayId}")
	public void deleteOneEssayById(@PathVariable("essayId") Long essayId) {
		essayService.deleteOneEssayById(essayId);
	}
}
