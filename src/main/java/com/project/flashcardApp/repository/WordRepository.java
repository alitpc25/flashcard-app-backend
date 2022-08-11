package com.project.flashcardApp.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.flashcardApp.entities.Word;

public interface WordRepository extends JpaRepository<Word, Long> {

	Optional<Page<Word>> findByUserId(Long userId, Pageable pageable);
	Optional<List<Word>> findByUserId(Long userId);
	Optional<Word> findByEnglishWordAndUserId(String englishWord, Long userId);

}
