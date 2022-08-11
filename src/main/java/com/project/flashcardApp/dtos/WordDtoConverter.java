package com.project.flashcardApp.dtos;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.project.flashcardApp.entities.Word;

@Component
public class WordDtoConverter {
	
	private final UserDtoConverter userDtoConverter;
	public WordDtoConverter(UserDtoConverter userDtoConverter) {
		this.userDtoConverter = userDtoConverter;
	}
	
	public WordDto convert(Word from) {
		return new WordDto(from.getId(), userDtoConverter.convert(from.getUser()), from.getEnglishWord(), from.getTurkishWord());
	}
	
	public List<WordDto> convert(List<Word> from) {
		return from.stream().map(this::convert).collect(Collectors.toList());
	}
	
	public Page<WordDto> convert(Page<Word> from) {
		return from.map(this::convert);
	}
}
