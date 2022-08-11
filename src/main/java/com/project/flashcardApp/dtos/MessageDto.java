package com.project.flashcardApp.dtos;

import java.time.LocalDateTime;
import java.util.Objects;
public class MessageDto {
	private Long id;
	private UserDto messageSender;
	private UserDto messageReceiver;
	private ChatDto chat;
	private String text;
	private LocalDateTime sentAt;
	
	public Long getId() {
		return id;
	}
	public UserDto getMessageSender() {
		return messageSender;
	}
	public UserDto getMessageReceiver() {
		return messageReceiver;
	}
	public ChatDto getChat() {
		return chat;
	}
	public String getText() {
		return text;
	}
	public LocalDateTime getSentAt() {
		return sentAt;
	}
	public MessageDto(Long id, UserDto messageSender, UserDto messageReceiver, ChatDto chat, String text,
			LocalDateTime sentAt) {
		this.id = id;
		this.messageSender = messageSender;
		this.messageReceiver = messageReceiver;
		this.chat = chat;
		this.text = text;
		this.sentAt = sentAt;
	}
	@Override
	public int hashCode() {
		return Objects.hash(chat, id, messageReceiver, messageSender, sentAt, text);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MessageDto other = (MessageDto) obj;
		return Objects.equals(chat, other.chat) && Objects.equals(id, other.id)
				&& Objects.equals(messageReceiver, other.messageReceiver)
				&& Objects.equals(messageSender, other.messageSender) && Objects.equals(sentAt, other.sentAt)
				&& Objects.equals(text, other.text);
	}
	
}
