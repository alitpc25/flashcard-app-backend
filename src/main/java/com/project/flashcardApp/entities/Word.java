package com.project.flashcardApp.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="word")
public class Word {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "serial")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", nullable=false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private User user;

	private String englishWord;
	private String turkishWord;
	
	public Word() {}
	
	public Word(User user, String englishWord, String turkishWord) {
		this.user = user;
		this.englishWord = englishWord;
		this.turkishWord = turkishWord;
	}
	
	public Word(Long id, User user, String englishWord, String turkishWord) {
		this.id = id;
		this.user = user;
		this.englishWord = englishWord;
		this.turkishWord = turkishWord;
	}
	
	public Long getId() {
		return id;
	}
	public User getUser() {
		return user;
	}
	public String getEnglishWord() {
		return englishWord;
	}
	public String getTurkishWord() {
		return turkishWord;
	}
	public void setEnglishWord(String englishWord) {
		this.englishWord = englishWord;
	}
	public void setTurkishWord(String turkishWord) {
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
		Word other = (Word) obj;
		return Objects.equals(englishWord, other.englishWord) && Objects.equals(id, other.id)
				&& Objects.equals(turkishWord, other.turkishWord) && Objects.equals(user, other.user);
	}
	
	
}
