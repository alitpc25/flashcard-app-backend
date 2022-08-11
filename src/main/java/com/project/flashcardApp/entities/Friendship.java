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
public class Friendship {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "serial")
	private Long id;
	
	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User user;

	@ManyToOne
	private User friend;
	
	private boolean isAccepted = false;

	public Friendship(User user, User friend) {
		this.user = user;
		this.friend = friend;
	}
	
	public Friendship(Long id, User user, User friend, boolean isAccepted) {
		this.id = id;
		this.user = user;
		this.friend = friend;
		this.isAccepted = isAccepted;
	}

	public Friendship() {}

	public Long getId() {
		return id;
	}

	public User getUser() {
		return user;
	}

	public User getFriend() {
		return friend;
	}

	public boolean isAccepted() {
		return isAccepted;
	}

	public void setAccepted(boolean isAccepted) {
		this.isAccepted = isAccepted;
	}

	@Override
	public int hashCode() {
		return Objects.hash(friend, id, isAccepted, user);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Friendship other = (Friendship) obj;
		return Objects.equals(friend, other.friend) && Objects.equals(id, other.id) && isAccepted == other.isAccepted
				&& Objects.equals(user, other.user);
	}
	
}
