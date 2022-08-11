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

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class Message {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(columnDefinition = "serial")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", nullable=false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private User messageSender;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private User messageReceiver;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="chat_id", nullable=false)
	@OnDelete(action = OnDeleteAction.CASCADE)
	@JsonIgnore
	private Chat chat;
	
	private String text;
	
	@Column(name = "sent_at")
	private LocalDateTime sentAt;
	
	private boolean isSeen;

	public Message(User messageSender, User messageReceiver, Chat chat, String text, LocalDateTime sentAt, boolean isSeen) {
		this.messageSender = messageSender;
		this.messageReceiver = messageReceiver;
		this.chat = chat;
		this.text = text;
		this.sentAt = sentAt;
		this.isSeen = isSeen;
	}
	
	public Message(Long id, User messageSender, User messageReceiver, Chat chat, String text, LocalDateTime sentAt, boolean isSeen) {
		this.id = id;
		this.messageSender = messageSender;
		this.messageReceiver = messageReceiver;
		this.chat = chat;
		this.text = text;
		this.sentAt = sentAt;
		this.isSeen = isSeen;
	}
	
	public Message() {}

	public Long getId() {
		return id;
	}

	public User getMessageSender() {
		return messageSender;
	}

	public User getMessageReceiver() {
		return messageReceiver;
	}

	public Chat getChat() {
		return chat;
	}

	public String getText() {
		return text;
	}

	public LocalDateTime getSentAt() {
		return sentAt;
	}

	public boolean isSeen() {
		return isSeen;
	}

	public void setSeen(boolean isSeen) {
		this.isSeen = isSeen;
	}

	@Override
	public int hashCode() {
		return Objects.hash(chat, id, isSeen, messageReceiver, messageSender, sentAt, text);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		return Objects.equals(chat, other.chat) && Objects.equals(id, other.id) && isSeen == other.isSeen
				&& Objects.equals(messageReceiver, other.messageReceiver)
				&& Objects.equals(messageSender, other.messageSender) && Objects.equals(sentAt, other.sentAt)
				&& Objects.equals(text, other.text);
	}
	
}
