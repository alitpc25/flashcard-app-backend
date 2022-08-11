package com.project.flashcardApp.dtos;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import com.project.flashcardApp.entities.Essay;

@Component
public class EssayDtoConverter {
	
	private final UserDtoConverter userDtoConverter;
	public EssayDtoConverter(UserDtoConverter userDtoConverter) {
		this.userDtoConverter = userDtoConverter;
	}
	
	
	public EssayDto convert(Essay from) {
		return new EssayDto(from.getId(), userDtoConverter.convert(from.getUser()), from.getTitle(), from.getText(), from.getPublishedOn());
	}
	
	public Page<EssayDto> convert(Page<Essay> from) {
		return from.map(this::convert);
	}
}
