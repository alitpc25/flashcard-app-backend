package com.project.flashcardApp.requests;

public class WordInsertRequest {
	
	private String englishWord;
	private String turkishWord;
	private Long userId;
	public WordInsertRequest(String englishWord, String turkishWord, Long userId) {
		this.englishWord = englishWord;
		this.turkishWord = turkishWord;
		this.userId = userId;
	}
	public String getEnglishWord() {
		return englishWord;
	}
	public void setEnglishWord(String englishWord) {
		this.englishWord = englishWord;
	}
	public String getTurkishWord() {
		return turkishWord;
	}
	public void setTurkishWord(String turkishWord) {
		this.turkishWord = turkishWord;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
}
