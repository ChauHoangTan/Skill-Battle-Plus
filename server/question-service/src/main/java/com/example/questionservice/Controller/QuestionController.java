package com.example.questionservice.Controller;

import com.example.questionservice.DTO.QuestionDTO;
import com.example.questionservice.Model.Question;
import com.example.questionservice.Response.ApiResponse;
import com.example.questionservice.Service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {

    final private QuestionService questionService;

    @GetMapping()
    public ResponseEntity<ApiResponse<List<Question>>> getAll() {
        return questionService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Question>> getById(@PathVariable UUID id) {
        return questionService.getById(id);
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<Question>> create(UUID quizId, @RequestBody @Valid QuestionDTO questionDTO) {
        return questionService.create(quizId, questionDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Question>> update(@PathVariable UUID id, @RequestBody @Valid QuestionDTO questionDTO) {
        return questionService.update(questionDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return questionService.delete(id);
    }
}
