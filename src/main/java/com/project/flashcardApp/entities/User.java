package com.project.flashcardApp.entities;

import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name="users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "serial")
	private Long id;
	private String username;
	private String email;
	private Boolean enabled = false;
	private String password;
	private int score = 0;	
	
	@OneToMany(mappedBy="user", cascade = CascadeType.ALL)
	private Set<Friendship> friendships;
	
	@OneToMany(mappedBy="user", cascade = CascadeType.ALL)
	private Set<Chat> chats;
	
	public User(Long id, String username, String email, Boolean enabled, String password, int score) {
		this.id = id; 
		this.username = username;
		this.email = email;
		this.enabled = enabled;
		this.password = password;
		this.score = score;
	}
	
	public User(String username, String email, Boolean enabled, String password, int score) {
		this.username = username;
		this.email = email;
		this.enabled = enabled;
		this.password = password;
		this.score = score;
	}
	
	public User() {}

	public Long getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getEmail() {
		return email;
	}

	public Boolean getEnabled() {
		return enabled;
	}

	public String getPassword() {
		return password;
	}

	public int getScore() {
		return score;
	}

	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setScore(int score) {
		this.score = score;
	}

	@Override
	public int hashCode() {
		return Objects.hash(chats, email, enabled, friendships, id, password, score, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		User other = (User) obj;
		return Objects.equals(chats, other.chats) && Objects.equals(email, other.email)
				&& Objects.equals(enabled, other.enabled) && Objects.equals(friendships, other.friendships)
				&& Objects.equals(id, other.id) && Objects.equals(password, other.password) && score == other.score
				&& Objects.equals(username, other.username);
	}

	
}
