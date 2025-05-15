package com.example.examservice.controller;

import com.example.examservice.dto.ExamDTO;
import com.example.examservice.dto.SubmitExamRequestDTO;
import com.example.examservice.model.ExamResult;
import com.example.examservice.response.ApiResponse;
import com.example.examservice.Service.ExamService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/exams")
public class ExamController {

    private final ExamService examService;

    @GetMapping("")
    public ResponseEntity<ApiResponse<Page<ExamDTO>>> getAllExams(@RequestParam("limit") int limit,
                                                               @RequestParam("pageNumber") int pageNumber) {
        return examService.getAllExams(limit, pageNumber);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamDTO>> getExamById(@PathVariable("id") UUID id) {
        return examService.getExamById(id);
    }

    @PostMapping("")
    public ResponseEntity<ApiResponse<ExamDTO>> createExam(@RequestBody ExamDTO examDTO) {
        return examService.createExam(examDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExamDTO>> updateExam(@RequestBody ExamDTO examDTO, @PathVariable("id") UUID id) {
        return examService.updateExam(examDTO, id);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteExamById(@PathVariable("id") UUID id) {
        return examService.deleteExam(id);
    }

    @PostMapping("/submit")
    public ResponseEntity<ApiResponse<ExamResult>> submitExam(@RequestBody SubmitExamRequestDTO submitExamRequestDTO) {
        return examService.submitExam(submitExamRequestDTO);
    }
}
