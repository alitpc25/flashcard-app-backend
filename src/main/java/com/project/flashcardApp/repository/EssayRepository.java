package com.project.flashcardApp.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.flashcardApp.entities.Essay;

public interface EssayRepository extends JpaRepository<Essay, Long> {

	Optional<Page<Essay>> findByUserId(Long userId, Pageable pageable);

	Optional<Page<Essay>> findByTitleContainingIgnoreCaseAndUserId(String title, Pageable pageable, Long userId );

	Optional<Essay> findByIdAndUserId(Long essayId, Long userId);

}
