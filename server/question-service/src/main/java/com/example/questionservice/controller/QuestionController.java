package com.example.questionservice.controller;

import com.example.questionservice.dto.QuestionDTO;
import com.example.questionservice.dto.QuestionResultDTO;
import com.example.questionservice.dto.SubmitQuestionAnswerDTO;
import com.example.questionservice.enums.QuestionType;
import com.example.questionservice.enums.Visibility;
import com.example.questionservice.model.Question;
import com.example.questionservice.response.ApiResponse;
import com.example.questionservice.service.QuestionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    @GetMapping()
    public ResponseEntity<ApiResponse<Page<Question>>> getAll(@RequestParam("limit") int limit,
                                                              @RequestParam("pageNumber") int pageNumber) {
        return questionService.getAll(limit, pageNumber);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Question>> getById(@PathVariable UUID id) {
        return questionService.getById(id);
    }

    @GetMapping("/export")
    public ResponseEntity<Resource> exportQuestions(
            @RequestParam(required = false) UUID userId,
            @RequestParam(required = false) Visibility visibility,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) QuestionType questionType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate
    ) {
        return questionService.exportQuestions(userId, visibility, tags, questionType, fromDate, toDate);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<Question>>> searchQuestions(@RequestParam("keyword") String keyword,
                                                                       @RequestParam("limit") int limit,
                                                                       @RequestParam("pageNumber") int pageNumber) {
        return questionService.searchQuestions(keyword, limit, pageNumber);
    }

    @PostMapping()
    public ResponseEntity<ApiResponse<Question>> create(@RequestBody @Valid QuestionDTO questionDTO,
                                                        @RequestHeader("X-userId") UUID userId) {
        return questionService.create(questionDTO, userId);
    }

    @PostMapping("/create-list")
    public ResponseEntity<ApiResponse<List<UUID>>> createQuestions(@RequestBody @Valid List<QuestionDTO> listQuestionDTO,
                                                                   @RequestHeader("X-userId") UUID userId) {
        return questionService.createQuestions(listQuestionDTO, userId);
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<List<UUID>>> importQuestions(@RequestParam("file") MultipartFile file,
                                                                   @RequestHeader("X-userId") UUID userId) {
        return questionService.importQuestions(file, userId);
    }

    @PostMapping("/evaluate")
    public ResponseEntity<ApiResponse<QuestionResultDTO>> evaluateQuestion(@RequestBody SubmitQuestionAnswerDTO submitQuestionAnswerDTO) {
        return questionService.evaluateQuestion(submitQuestionAnswerDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Question>> update(@PathVariable UUID id,
                                                        @RequestBody @Valid QuestionDTO questionDTO) {
        return questionService.update(questionDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable UUID id) {
        return questionService.delete(id);
    }

}
