package com.example.questionservice.repository;

import com.example.questionservice.document.QuestionDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface QuestionSearchRepository extends ElasticsearchRepository<QuestionDocument, String> {

    // 1. Search full-text content
    Page<QuestionDocument> findByContentContaining(String content, Pageable pageable);

    // 2. Search by content and tags
    Page<QuestionDocument> findByContentContainingAndTagsIn(String content, List<String> tags, Pageable pageable);
}
