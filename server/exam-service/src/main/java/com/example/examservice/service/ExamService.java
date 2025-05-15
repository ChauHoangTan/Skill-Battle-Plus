package com.example.examservice.service;

import com.example.examservice.dto.ExamDTO;
import com.example.examservice.dto.SubmitExamRequestDTO;
import com.example.examservice.model.Exam;
import com.example.examservice.model.ExamQuiz;
import com.example.examservice.model.ExamResult;
import com.example.examservice.repository.ExamRepository;
import com.example.examservice.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ExamService {

    private final ExamRepository examRepository;
    private final ModelMapper modelMapper;
    private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:8085/exams").build();

    public ResponseEntity<ApiResponse<Page<ExamDTO>>> getAllExams(int limit, int pageNumber) {
        try {
            Pageable pageable = PageRequest.of(limit, pageNumber);
            Page<Exam> page = examRepository.findAll(pageable);

            Page<ExamDTO> finalPage = page.map(exam -> modelMapper.map(exam, ExamDTO.class));

            log.info("Get All Exams Successfully!");

            return new ResponseEntity<>(
                    new ApiResponse<>(
                            true,
                            "Retrieve all exams successfully!",
                            finalPage,
                            HttpStatus.OK
                    ),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Failed to get all exams!",
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<ApiResponse<ExamDTO>> getExamById(UUID id) {
        try {
            Optional<Exam> exam = examRepository.findById(id);

            if(exam.isEmpty()) {
                log.error("Failed to get exam by id!");
                return new ResponseEntity<>(
                        new ApiResponse<>(
                                false,
                                "Failed to get exam by id!",
                                null,
                                HttpStatus.INTERNAL_SERVER_ERROR
                        ),
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }

            ExamDTO result = modelMapper.map(exam.get(), ExamDTO.class);

            return new ResponseEntity<>(
                    new ApiResponse<>(
                            true,
                            "Get exam successfully!",
                            result,
                            HttpStatus.OK
                    ), HttpStatus.OK
            );

        } catch (Exception e) {
            log.error("Failed to get exam by id!", e);

            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Failed to get exam by id!",
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<ApiResponse<ExamDTO>> createExam(ExamDTO examDTO) {
        try {
            Exam e = Exam.builder()
                    .title(examDTO.getTitle())
                    .description(examDTO.getDescription())
                    .totalScore(examDTO.getTotalScore())
                    .mode(examDTO.getMode())
                    .examQuizzes(examDTO.getExamQuizzes().stream()
                            .map(examQuizDTO -> modelMapper.map(examQuizDTO, ExamQuiz.class))
                            .toList())
                    .duration(examDTO.getDuration())
                    .visibility(examDTO.getVisibility())
                    .build();

            Exam finalExam = examRepository.save(e);

            return new ResponseEntity<>(
                    new ApiResponse<>(
                            true,
                            "Create Exam Successfully!",
                            modelMapper.map(finalExam, ExamDTO.class),
                            HttpStatus.OK
                    ), HttpStatus.OK
            );

        } catch (Exception e) {
            log.error("Error create Exam!", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Error Create Exam!",
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ), HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<ApiResponse<ExamDTO>> updateExam(ExamDTO examDTO, UUID id) {
        try {
            Optional<Exam> examStored = examRepository.findById(id);

            if(examStored.isEmpty()) {
                log.error("Error Get Exam Id!");
                return new ResponseEntity<>(
                        new ApiResponse<>(
                                false,
                                "Error Get Exam Id!",
                                null,
                                HttpStatus.INTERNAL_SERVER_ERROR
                        ), HttpStatus.INTERNAL_SERVER_ERROR
                );
            }

            Exam finalExam = examStored.get();
            finalExam.setTitle(examDTO.getTitle());
            finalExam.setDescription(examDTO.getDescription());
            finalExam.setTotalScore(examDTO.getTotalScore());
            finalExam.setMode(examDTO.getMode());
            finalExam.setExamQuizzes(
                    examDTO.getExamQuizzes().stream()
                            .map(examQuizDTO -> modelMapper.map(examQuizDTO, ExamQuiz.class))
                            .toList()
            );
            finalExam.setDuration(examDTO.getDuration());
            finalExam.setVisibility(examDTO.getVisibility());

            examRepository.save(finalExam);

            return new ResponseEntity<>(
                    new ApiResponse<>(
                            true,
                            "Update Exam Successfully!",
                            modelMapper.map(finalExam, ExamDTO.class),
                            HttpStatus.OK
                    ), HttpStatus.OK
            );

        } catch (Exception e) {
            log.error("Error Update Exam!", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Error Update Exam!",
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ), HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<ApiResponse<Void>> deleteExam(UUID id) {
        try {
            examRepository.deleteById(id);

            return new ResponseEntity<>(
                    new ApiResponse<>(
                            true,
                            "Delete Exam Successfully!",
                            null,
                            HttpStatus.OK
                    ), HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Error Delete Exam!", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Error Delete Exam!",
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ), HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<ExamResult>> submitExam(SubmitExamRequestDTO submitExamRequestDTO) {
        try {
            ApiResponse<E> examQuiz submitExamRequestDTO.getQuizResults().stream()
                    .map()
        } catch (Exception e) {
            log.error("Error Submit Exam!", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Error Submit Exam!",
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ), HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
