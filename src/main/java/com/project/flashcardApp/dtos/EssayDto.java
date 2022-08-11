package com.project.flashcardApp.dtos;

import java.time.LocalDateTime;
import java.util.Objects;

public class EssayDto {
	private Long id;
	private UserDto user;
	private String title;
	private String text;
	private LocalDateTime publishedOn;
	public Long getId() {
		return id;
	}
	public UserDto getUser() {
		return user;
	}
	public String getTitle() {
		return title;
	}
	public String getText() {
		return text;
	}
	public LocalDateTime getPublishedOn() {
		return publishedOn;
	}
	public EssayDto(Long id, UserDto user, String title, String text, LocalDateTime publishedOn) {
		this.id = id;
		this.user = user;
		this.title = title;
		this.text = text;
		this.publishedOn = publishedOn;
	}
	@Override
	public int hashCode() {
		return Objects.hash(id, publishedOn, text, title, user);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EssayDto other = (EssayDto) obj;
		return Objects.equals(id, other.id) && Objects.equals(publishedOn, other.publishedOn)
				&& Objects.equals(text, other.text) && Objects.equals(title, other.title)
				&& Objects.equals(user, other.user);
	}
	
}
