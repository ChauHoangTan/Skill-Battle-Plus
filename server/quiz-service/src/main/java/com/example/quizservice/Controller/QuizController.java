package com.example.quizservice.Controller;

import com.example.quizservice.DTO.QuizRequestDTO;
import com.example.quizservice.Model.Quiz;
import com.example.quizservice.Response.ApiResponse;
import com.example.quizservice.Service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    final private QuizService quizService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<List<Quiz>>> getAll() {
        return quizService.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Quiz>> getById(@PathVariable UUID id) {
        return quizService.getById(id);
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<Quiz>> create(@RequestBody QuizRequestDTO quizRequestDTO) {
        return quizService.create(quizRequestDTO);
    }

    @PutMapping("")
    public ResponseEntity<ApiResponse<Quiz>> update(@RequestBody QuizRequestDTO quizRequestDTO) {
        return quizService.update(quizRequestDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return quizService.deleteById(id);
    }
}
