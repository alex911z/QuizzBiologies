package com.example.quizz_biologies.repository;

import com.example.quizz_biologies.entity.TestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TestRepository extends JpaRepository<TestEntity, Long> {
    List<TestEntity> findByTeacherId(Long teacherId);
}
