package com.project.flashcardApp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.flashcardApp.entities.Chat;

public interface ChatRepository extends JpaRepository<Chat, Long> {

	@Query(value="SELECT * FROM chat WHERE (friend_id=:friendId and user_id=:userId)", nativeQuery=true)
	Optional<Chat> findByUserIdAndFriendId(Long userId, Long friendId);
	
	@Query(value="SELECT * FROM chat WHERE (friend_id=:userId or user_id=:userId)", nativeQuery=true)
	Optional<List<Chat>> findByFriendIdOrUserId(@Param("userId") Long userId);

	@Modifying
	@Query(value="DELETE FROM chat WHERE (friend_id=:userId or user_id=:userId)", nativeQuery=true)
	void deleteAllByUserIdOrFriendId(Long userId);
}
