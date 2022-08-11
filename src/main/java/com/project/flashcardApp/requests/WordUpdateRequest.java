package com.project.flashcardApp.requests;

public class WordUpdateRequest {

	private String englishWord;
	private String turkishWord;
	private Long userId;
	public String getEnglishWord() {
		return englishWord;
	}
	public String getTurkishWord() {
		return turkishWord;
	}
	public Long getUserId() {
		return userId;
	}
	public WordUpdateRequest(String englishWord, String turkishWord, Long userId) {
		this.englishWord = englishWord;
		this.turkishWord = turkishWord;
		this.userId = userId;
	}
	
}
