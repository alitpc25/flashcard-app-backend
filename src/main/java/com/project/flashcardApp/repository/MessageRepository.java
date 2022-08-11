package com.project.flashcardApp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.project.flashcardApp.entities.Message;

public interface MessageRepository extends JpaRepository<Message, Long> {

	Optional<List<Message>> findByChatId(Long chatId);

	@Query(value="SELECT * FROM message WHERE chat_id=:chatId and user_id=:userId", nativeQuery=true)
	Optional<List<Message>> findByChatIdAndMessageSenderId(Long chatId, Long userId);

	@Query(value="SELECT * FROM message WHERE chat_id=:chatId and user_id=:friendId", nativeQuery=true)
	Optional<List<Message>> findByChatIdAndMessageReceiverId(Long chatId, Long friendId);

	@Modifying
	@Query(value="DELETE FROM message WHERE chat_id=:chatId", nativeQuery=true)
	void deleteByChatId(Long chatId);

	@Query(value="SELECT COUNT(*) FROM message WHERE user_id=:friendId and message_receiver_id=:userId and is_seen=false ",nativeQuery=true)
	long existsByUserIdAndFriendIdAndIsSeenFalse(Long userId, Long friendId);

	@Query(value="SELECT * FROM message WHERE (((user_id=:userId and message_receiver_id=:friendId) or (user_id=:friendId and message_receiver_id=:userId)) and is_seen=false)", nativeQuery=true)
	List<Message> findAllByIsSeenFalse(Long userId, Long friendId);

}
