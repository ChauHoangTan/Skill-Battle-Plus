package com.example.quizservice.repository;

import com.example.quizservice.model.QuizResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuizResultRepository extends JpaRepository<QuizResult, UUID> {
}
