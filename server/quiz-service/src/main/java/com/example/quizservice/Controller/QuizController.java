package com.example.quizservice.Controller;

import com.example.quizservice.DTO.QuizRequestDTO;
import com.example.quizservice.Model.Quiz;
import com.example.quizservice.Response.ApiResponse;
import com.example.quizservice.Service.QuizService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Quiz>>> searchQuizzes(@Param("keyword") String keyword,
                                                                 @Param("limit") int limit,
                                                                 @Param("pageNumber") int pageNumber) {
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

    @PutMapping("")
    public ResponseEntity<ApiResponse<Quiz>> update(@RequestBody QuizRequestDTO quizRequestDTO) {
        return quizService.update(quizRequestDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return quizService.deleteById(id);
    }
}
