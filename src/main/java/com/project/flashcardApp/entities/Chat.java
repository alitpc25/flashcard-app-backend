package com.project.flashcardApp.entities;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Entity
public class Chat {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "serial")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User user;
	
	@ManyToOne
	@JoinColumn(name="friend_id", nullable=false)
	private User friend;

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public User getFriend() {
		return friend;
	}

	public Chat(User user, User friend) {
		this.user = user;
		this.friend = friend;
	}
	
	public Chat(Long id, User user, User friend) {
		this.id = id;
		this.user = user;
		this.friend = friend;
	}
	
	public Chat() {}

	@Override
	public int hashCode() {
		return Objects.hash(friend, id, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Chat other = (Chat) obj;
		return Objects.equals(friend, other.friend) && Objects.equals(id, other.id) && Objects.equals(user, other.user);
	}
	
}
