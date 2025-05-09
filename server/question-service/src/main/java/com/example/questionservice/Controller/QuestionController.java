package com.example.questionservice.Controller;

import com.example.questionservice.DTO.ListQuestionDTO;
import com.example.questionservice.DTO.QuestionDTO;
import com.example.questionservice.DTO.SearchDTO;
import com.example.questionservice.Enum.QuestionType;
import com.example.questionservice.Enum.Visibility;
import com.example.questionservice.Model.Question;
import com.example.questionservice.Response.ApiResponse;
import com.example.questionservice.Service.QuestionService;
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
import java.util.Optional;
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
