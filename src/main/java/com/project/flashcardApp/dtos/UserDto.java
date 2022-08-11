package com.project.flashcardApp.dtos;

import java.util.Objects;

public class UserDto {
	private Long id;
	private String username;
	private String email;
	private Boolean enabled = false;
	private String password;
	private int score;
	public UserDto(Long id, String username, String email, Boolean enabled, String password, int score) {
		this.id = id;
		this.username = username;
		this.email = email;
		this.enabled = enabled;
		this.password = password;
		this.score = score;
	}
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
	@Override
	public int hashCode() {
		return Objects.hash(email, enabled, id, password, score, username);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDto other = (UserDto) obj;
		return Objects.equals(email, other.email) && Objects.equals(enabled, other.enabled)
				&& Objects.equals(id, other.id) && Objects.equals(password, other.password) && score == other.score
				&& Objects.equals(username, other.username);
	}
	@Override
	public String toString() {
		return "UserDto [id=" + id + ", username=" + username + ", email=" + email + ", enabled=" + enabled
				+ ", password=" + password + ", score=" + score + "]";
	}
	
	
}
