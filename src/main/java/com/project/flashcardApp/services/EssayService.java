package com.project.flashcardApp.services;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.project.flashcardApp.dtos.EssayDto;
import com.project.flashcardApp.dtos.EssayDtoConverter;
import com.project.flashcardApp.entities.Essay;
import com.project.flashcardApp.entities.User;
import com.project.flashcardApp.exceptions.EssayNotFoundException;
import com.project.flashcardApp.exceptions.NoEssayFoundForUserException;
import com.project.flashcardApp.repository.EssayRepository;
import com.project.flashcardApp.requests.EssayRequest;

@Service
public class EssayService {

	private final EssayRepository essayRepository;
	private final UserService userService;
	private final EssayDtoConverter essayDtoConverter;

	public EssayService(EssayRepository essayRepository, UserService userService, EssayDtoConverter essayDtoConverter) {
		this.essayRepository = essayRepository;
		this.userService = userService;
		this.essayDtoConverter = essayDtoConverter;
	}

	public Page<EssayDto> getAllEssaysPaginated(Integer pageSize, Integer page, Long userId) {
		Pageable pageable = PageRequest.of(page-1,pageSize);
		Page<Essay> essays = essayRepository.findByUserId(userId, pageable).orElseThrow(() -> new NoEssayFoundForUserException("No essay found for user."));
		return essayDtoConverter.convert(essays);
	}
	
	public Page<EssayDto> getAllEssaysPaginatedAndSortedByTimeDesc(Integer pageSize, Integer page, Long userId) {
		Pageable pageable = PageRequest.of(page-1,pageSize, Sort.by("publishedOn").descending());
		Page<Essay> essays = essayRepository.findByUserId(userId, pageable).orElseThrow(() -> new NoEssayFoundForUserException("No essay found for user."));
		return essayDtoConverter.convert(essays);
	}
	
	public Page<EssayDto> getAllEssaysPaginatedAndSortedByTimeAsc(Integer pageSize, Integer page, Long userId) {
		Pageable pageable = PageRequest.of(page-1,pageSize, Sort.by("publishedOn").ascending());
		Page<Essay> essays = essayRepository.findByUserId(userId, pageable).orElseThrow(() -> new NoEssayFoundForUserException("No essay found for user."));
		return essayDtoConverter.convert(essays);
	}
	
	public Page<EssayDto> getAllEssaysSearchByTitle(Integer pageSize, Integer page, Long userId, String title) {
		Pageable pageable = PageRequest.of(page-1,pageSize, Sort.by("publishedOn").descending());
		Page<Essay> essays = essayRepository.findByTitleContainingIgnoreCaseAndUserId(title, pageable, userId).orElseThrow(() -> new NoEssayFoundForUserException("No essay found for user with title." + title));
		return essayDtoConverter.convert(essays);
	}

	public EssayDto getOneEssayById(Long essayId, Long userId) {
		Essay essay = essayRepository.findByIdAndUserId(essayId, userId).orElseThrow(() -> new EssayNotFoundException("Essay not found."));
		return essayDtoConverter.convert(essay);
	}

	public EssayDto insertOneEssay(EssayRequest essayRequest) {
		User user = userService.getOneUserByIdProtected(essayRequest.getUserId());	
		Essay essay = new Essay(user, essayRequest.getTitle(), essayRequest.getText(), LocalDateTime.now());
		return essayDtoConverter.convert(essayRepository.save(essay));
	}

	public EssayDto updateOneEssayById(Long essayId, EssayRequest essayRequest) {
		Essay essay = essayRepository.findById(essayId).orElseThrow(() -> new EssayNotFoundException("Essay not found."));
		essay.setText(essayRequest.getText());
		essay.setTitle(essayRequest.getTitle());
		return essayDtoConverter.convert(essayRepository.save(essay));
	}

	public void deleteOneEssayById(Long essayId) {
		essayRepository.deleteById(essayId);
	}
		
}
