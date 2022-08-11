package com.project.flashcardApp.entities;

import java.time.LocalDateTime;
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
@Table(name="essay")
public class Essay {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "serial")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", nullable=false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private User user;

	private String title;
	
	@Column(columnDefinition="VARCHAR(1200)")
	private String text;
	
	@Column(name = "published_on")
	private LocalDateTime publishedOn;

	public Essay(User user, String title, String text, LocalDateTime publishedOn) {
		this.user = user;
		this.title = title;
		this.text = text;
		this.publishedOn = publishedOn;
	}
	
	public Essay(Long id, User user, String title, String text, LocalDateTime publishedOn) {
		this.id = id;
		this.user = user;
		this.title = title;
		this.text = text;
		this.publishedOn = publishedOn;
	}
	
	public Essay() {}

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public String getTitle() {
		return title;
	}

	public String getText() {
		return text;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LocalDateTime getPublishedOn() {
		return publishedOn;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, publishedOn, text, title, user);
	}

	@Override
	//Deleted publishedOn for test-case equality.
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Essay other = (Essay) obj;
		return Objects.equals(id, other.id) && Objects.equals(text, other.text) 
				&& Objects.equals(title, other.title)
				&& Objects.equals(user, other.user);
	}

	@Override
	public String toString() {
		return "Essay [id=" + id + ", user=" + user + ", title=" + title + ", text=" + text + ", publishedOn="
				+ publishedOn + "]";
	}
	
}
