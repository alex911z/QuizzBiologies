package com.example.quizz_biologies.repository;

import com.example.quizz_biologies.entity.TestQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestQuestionRepository extends JpaRepository<TestQuestion, Long> {
    List<TestQuestion> findByTestId(Long testId);
}
