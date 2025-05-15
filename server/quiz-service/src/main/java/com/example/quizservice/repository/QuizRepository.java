package com.example.quizservice.repository;

import com.example.quizservice.model.Quiz;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.UUID;

public interface QuizRepository extends JpaRepository<Quiz, UUID> {

    @Query(value = """
        SELECT DISTINCT ON (q.id) q.*,
            ts_rank(to_tsvector('english', q.description || ' ' || q.title  || ' ' || t.name ), plainto_tsquery('english', :keyword)) as rank
        FROM quiz q
        JOIN quiz_tags qt ON q.id = qt.quiz_id
        JOIN tag t ON t.id = qt.tags_id
        WHERE
            to_tsvector('english', q.description || ' ' || q.title  || ' ' || t.name ) @@ plainto_tsquery('english', :keyword)
        ORDER BY
            q.id, rank
        """
        ,countQuery = """
        SELECT DISTINCT ON (q.id) q.*
        FROM quiz q
        JOIN quiz_tags qt ON q.id = qt.quiz_id
        JOIN tag t ON t.id = qt.tags_id
        WHERE
            to_tsvector('english', q.description || ' ' || q.title  || ' ' || t.name ) @@ plainto_tsquery('english', :keyword)
        """, nativeQuery = true)
    Page<Quiz> searchByFTS(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = """
        SELECT DISTINCT ON (q.id) q.*
        FROM quiz q
        JOIN quiz_tags qt ON q.id = qt.quiz_id
        JOIN tag t ON t.id = qt.tags_id
        WHERE
            to_tsvector('english', q.description || ' ' || q.title  || ' ' || t.name ) @@ plainto_tsquery('english', :keyword)
        ORDER BY
            q.id
    """,
    countQuery = """
        SELECT COUNT(*)
        FROM quiz q
        JOIN quiz_tags qt ON q.id = qt.quiz_id
        JOIN tag t ON t.id = qt.tags_id
        WHERE
            to_tsvector('english', q.description || ' ' || q.title  || ' ' || t.name ) @@ plainto_tsquery('english', :keyword)
    """, nativeQuery = true)
    Page<Quiz> searchByFuzzyMatch(@Param("keyword") String keyword, Pageable pageable);
}
