package com.example.quizservice.controller;

import com.example.quizservice.dto.QuizRequestDTO;
import com.example.quizservice.dto.SubmitQuizAnswerDTO;
import com.example.quizservice.model.Quiz;
import com.example.quizservice.response.ApiResponse;
import com.example.quizservice.service.QuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {

    private final QuizService quizService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<Page<Quiz>>> getAll(@RequestParam("limit") int limit,
                                                          @RequestParam("pageNumber") int pageNumber) {
        return quizService.getAll(limit, pageNumber);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Quiz>> getById(@PathVariable UUID id) {
        return quizService.getById(id);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Quiz>>> searchQuizzes(@RequestParam("keyword") String keyword,
                                                                 @RequestParam("limit") int limit,
                                                                 @RequestParam("pageNumber") int pageNumber) {
        return quizService.searchQuizzes(keyword, limit, pageNumber);
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<Quiz>> create(@RequestBody QuizRequestDTO quizRequestDTO,
                                                    @RequestHeader("X-username") String username,
                                                    @RequestHeader("X-roles") String roles,
                                                    @RequestHeader("X-userId") String userId) {
        return quizService.create(quizRequestDTO, username, roles, userId);
    }

    @PostMapping("/import/{quizId}")
    public ResponseEntity<ApiResponse<Void>> importQuestions(@RequestParam("file") MultipartFile multipartFile,
                                                             @PathVariable("quizId") UUID quizId,
                                                             @RequestHeader("X-userId") UUID userId) {
        return quizService.importQuestions(multipartFile, quizId, userId);
    }

    @PostMapping("/{quizId}/submit/")
    public ResponseEntity<ApiResponse<SubmitQuizAnswerDTO>> submitQuiz(@PathVariable("quizId") UUID quizId,
                                                                       @RequestBody SubmitQuizAnswerDTO submitQuizAnswerDTO) {
        return quizService.submitQuiz(quizId, submitQuizAnswerDTO);
    }

    @PostMapping("/{quizId}/evaluate")
    public ResponseEntity<ApiResponse<SubmitQuizAnswerDTO>> evaluateQuiz(@PathVariable("quizId") UUID quizId,
                                                                         @RequestBody SubmitQuizAnswerDTO submitQuizAnswerDTO) {
        return quizService.evaluateQuiz(quizId, submitQuizAnswerDTO);
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
