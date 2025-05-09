package com.example.questionservice.Repository;

import com.example.questionservice.Model.Question;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QuestionRepository extends JpaRepository<Question, UUID>, JpaSpecificationExecutor<Question> {
    @Query(value = """
        SELECT DISTINCT q.*
        FROM question q
        JOIN question_tags qt ON qt.question_id = q.id
        JOIN tag t ON t.id = qt.tags_id
        WHERE
            to_tsvector('english', q.title || ' ' || q.description || ' ' || t.name) @@ plainto_tsquery('english', :keyword)
            OR to_tsvector('vietnamese', q.title || ' ' || q.description) @@ plainto_tsquery('vietnamese', :keyword)
            OR levenshtein(lower(q.title), lower(:keyword)) <= 2
            OR levenshtein(lower(q.description), lower(:keyword)) <= 2
        ORDER BY
            GREATEST(
                ts_rank(to_tsvector('english', q.title || ' ' || q.description || ' ' || t.name), plainto_tsquery('english', :keyword)),
                ts_rank(to_tsvector('vietnamese', q.title || ' ' || q.description), plainto_tsquery('vietnamese', :keyword))
            ) DESC
        """,
            countQuery = """
        SELECT COUNT(DISTINCT q.id)
        FROM question q
        JOIN question_tags qt ON qt.question_id = q.id
        JOIN tag t ON t.id = qt.tags_id
        WHERE
            to_tsvector('english', q.title || ' ' || q.description || ' ' || t.name) @@ plainto_tsquery('english', :keyword)
            OR to_tsvector('vietnamese', q.title || ' ' || q.description) @@ plainto_tsquery('vietnamese', :keyword)
            OR levenshtein(lower(q.title), lower(:keyword)) <= 2
            OR levenshtein(lower(q.description), lower(:keyword)) <= 2
        """,
            nativeQuery = true)
    Page<Question> search(@Param("keyword") String keyword, Pageable pageable);
}
