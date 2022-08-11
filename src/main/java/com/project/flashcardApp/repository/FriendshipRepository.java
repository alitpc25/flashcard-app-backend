package com.project.flashcardApp.repository;


import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.flashcardApp.entities.Friendship;

public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
	
	@Query(value="SELECT * FROM friendship WHERE (friend_id=:userId or user_id=:userId) and is_accepted=true", nativeQuery=true)
	Optional<List<Friendship>> findByFriendIdOrUserIdAndIsAcceptedTrue(@Param("userId") Long userId);

	@Query(value="SELECT * FROM friendship WHERE friend_id=:userId and is_accepted=false", nativeQuery=true)
	Optional<List<Friendship>> findByFriendIdAndIsAcceptedFalse(@Param("userId") Long userId);

	@Modifying
	@Query(value="DELETE FROM friendship WHERE (user_id=:friendId and friend_id=:userId) or (user_id=:userId and friend_id=:friendId)",nativeQuery=true)
	void deleteByFriendIdOrUserId(Long friendId, Long userId);

	@Query(value="SELECT COUNT(*) FROM friendship WHERE user_id=:userId and friend_id=:friendId and is_accepted=false ",nativeQuery=true)
	long existsByUserIdAndFriendIdAndIsAcceptedFalse(Long userId, Long friendId);
	
	@Query(value="SELECT * FROM friendship WHERE user_id=:friendId and friend_id=:userId and is_accepted=false",nativeQuery=true)
	Optional<Friendship> findByFriendIdAndUserIdAndIsAcceptedFalse(Long userId, Long friendId);

	@Query(value="SELECT COUNT(*) FROM friendship WHERE user_id=:friendId and friend_id=:userId and is_accepted=false ",nativeQuery=true)
	long existsByFriendIdAndUserIdAndIsAcceptedFalse(Long userId, Long friendId);

	@Query(value="SELECT COUNT(*) FROM friendship WHERE ((user_id=:friendId and friend_id=:userId) or (user_id=:userId and friend_id=:friendId)) and is_accepted=true ",nativeQuery=true)
	long existsFriendshipByFriendIdAndUserId(Long userId, Long friendId);

	@Modifying
	@Query(value="DELETE FROM friendship WHERE (friend_id=:userId or user_id=:userId)", nativeQuery=true)
	void deleteAllByUserIdOrFriendId(Long userId);

}
