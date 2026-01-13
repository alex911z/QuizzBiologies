package com.example.quizz_biologies.repository;

import com.example.quizz_biologies.model.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {
    List<QuizResult> findTop10ByUsernameOrderByCreatedAtDesc(String username);
    List<QuizResult> findByUsernameOrderByCreatedAtDesc(String username);
}
