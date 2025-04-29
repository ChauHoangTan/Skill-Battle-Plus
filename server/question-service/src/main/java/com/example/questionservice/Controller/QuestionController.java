package com.example.questionservice.Controller;

import com.example.questionservice.Service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {

    final private QuestionService questionService;

    @GetMapping()
    public ResponseEntity<String> getAll() {
        return questionService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getById(@PathVariable UUID id) {
        return questionService.getById(id);
    }

    @PostMapping()
    public ResponseEntity<String> create() {
        return questionService.create();
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable UUID id) {
        return questionService.update(id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable UUID id) {
        return questionService.delete(id);
    }
}
