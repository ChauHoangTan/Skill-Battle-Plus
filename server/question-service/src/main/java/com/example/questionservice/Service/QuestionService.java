package com.example.questionservice.Service;

import com.example.questionservice.Repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class QuestionService {

    final private QuestionRepository questionRepository;

    public ResponseEntity<String> getAll() {

    }

    public ResponseEntity<String> getById(UUID id) {

    }

    public ResponseEntity<String> create() {

    }

    public ResponseEntity<String> update(UUID id) {

    }

    public ResponseEntity<String> delete(UUID id) {

    }
}
