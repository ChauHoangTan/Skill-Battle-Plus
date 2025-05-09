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
        SELECT DISTINCT ON (q.id) q.*,
            ts_rank(to_tsvector('english', q.content || ' ' || t.name), plainto_tsquery('english', :keyword)) AS rank
        FROM question q
        JOIN question_tags qt ON qt.question_id = q.id
        JOIN tag t ON t.id = qt.tags_id
        WHERE
            to_tsvector('english', q.content || ' ' || t.name) @@ plainto_tsquery('english', :keyword)
        ORDER BY
            q.id, rank
        DESC
        """,
            countQuery = """
        SELECT DISTINCT q.*
        FROM question q
        JOIN question_tags qt ON qt.question_id = q.id
        JOIN tag t ON t.id = qt.tags_id
        WHERE
            to_tsvector('english', q.content || ' ' || t.name) @@ plainto_tsquery('english', :keyword)
        """,
            nativeQuery = true)
    Page<Question> searchByFTS(@Param("keyword") String keyword, Pageable pageable);

    @Query(value = """
        SELECT q.*,
                 LEAST(
                     levenshtein(lower(q.title), lower(:keyword)),
                     levenshtein(lower(q.description), lower(:keyword))
                 ) AS rank
         FROM question q
         WHERE
              levenshtein(lower(q.title), lower(:keyword)) <= 2
              OR levenshtein(lower(q.description), lower(:keyword)) <= 2
              OR lower(q.title) LIKE CONCAT('%', lower(:keyword), '%')
              OR lower(q.description) LIKE CONCAT('%', lower(:keyword), '%')
         ORDER BY rank ASC;
        """,
        countQuery = """
         SELECT COUNT(*)
         FROM question q
         WHERE
              levenshtein(lower(q.title), lower(:keyword)) <= 2
              OR levenshtein(lower(q.description), lower(:keyword)) <= 2
              OR lower(q.title) LIKE CONCAT('%', lower(:keyword), '%')
              OR lower(q.description) LIKE CONCAT('%', lower(:keyword), '%')
        """,
            nativeQuery = true)
    Page<Question> searchByFuzzyMatch(@Param("keyword") String keyword, Pageable pageable);

}
