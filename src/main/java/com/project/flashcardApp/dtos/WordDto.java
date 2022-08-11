package com.project.flashcardApp.dtos;

import java.util.Objects;

public class WordDto {
	private Long id;
	private UserDto user;
	private String englishWord;
	private String turkishWord;
	public Long getId() {
		return id;
	}
	public UserDto getUser() {
		return user;
	}
	public String getEnglishWord() {
		return englishWord;
	}
	public String getTurkishWord() {
		return turkishWord;
	}
	public WordDto(Long id, UserDto user, String englishWord, String turkishWord) {
		this.id = id;
		this.user = user;
		this.englishWord = englishWord;
		this.turkishWord = turkishWord;
	}
	@Override
	public int hashCode() {
		return Objects.hash(englishWord, id, turkishWord, user);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		WordDto other = (WordDto) obj;
		return Objects.equals(englishWord, other.englishWord) && Objects.equals(id, other.id)
				&& Objects.equals(turkishWord, other.turkishWord) && Objects.equals(user, other.user);
	}
	
}
