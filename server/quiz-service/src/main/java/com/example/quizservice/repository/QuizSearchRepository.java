package com.example.quizservice.repository;

import com.example.quizservice.document.QuizDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface QuizSearchRepository extends ElasticsearchRepository<QuizDocument, String> {

//    1. Search by title and description
    Page<QuizDocument> findByTitleContainingOrDescriptionContaining(String title, String description, Pageable pageable);

    // 2. Search by title and description and tags
    Page<QuizDocument> findByTitleContainingOrDescriptionContainingAndTagsIn(String title, String description, List<String> tags, Pageable pageable);
}
