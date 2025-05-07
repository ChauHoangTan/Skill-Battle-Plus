package com.example.quizservice.Service;

import com.example.quizservice.DTO.QuizRequestDTO;
import com.example.quizservice.Model.Quiz;
import com.example.quizservice.Repository.QuizRepository;
import com.example.quizservice.Response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class QuizService {

    final private QuizRepository quizRepository;

    public ResponseEntity<ApiResponse<List<Quiz>>> getAll() {
        try {
            List<Quiz> quizList = quizRepository.findAll();

            ApiResponse<List<Quiz>> response = new ApiResponse<>(
                    true,
                    "Get All Quizzes Success!",
                    quizList,
                    HttpStatus.OK
            );

            log.info("Get All Quizzes Successfully!");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Failed to retrieve quizzes!");

            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Failed to retrieve quizzes!",
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<ApiResponse<Quiz>> getById(UUID id) {
        try {
            Optional<Quiz> quiz = quizRepository.findById(id);

            if(quiz.isEmpty()) {
                return new ResponseEntity<>(
                        new ApiResponse<>(
                                false,
                                "Failed to retrieve quizzes!",
                                null,
                                HttpStatus.INTERNAL_SERVER_ERROR
                        ),
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }

            ApiResponse<Quiz> response = new ApiResponse<>(
                    true,
                    "Get All Quizzes Success!",
                    quiz.get(),
                    HttpStatus.OK
            );

            log.info("Get Quiz Successfully!");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Failed to retrieve quiz!");

            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Failed to retrieve quiz!",
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<ApiResponse<Quiz>> create(QuizRequestDTO quizRequestDTO) {
        try {
            Quiz quiz = Quiz.builder()
                    .title(quizRequestDTO.getTitle())
                    .description(quizRequestDTO.getDescription())
                    .difficulty(quizRequestDTO.getDifficulty())
                    .questions(quizRequestDTO.getQuestions())
                    .build();

            Quiz quizSaved = quizRepository.save(quiz);

            ApiResponse<Quiz> response = new ApiResponse<>(
                    true,
                    "Create Quiz Successfully!",
                    quizSaved,
                    HttpStatus.OK
            );

            log.info("Create Quiz Successfully!");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Failed to save quiz!");

            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Failed to save quiz!",
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<ApiResponse<Quiz>> update(QuizRequestDTO quizRequestDTO) {
        try {
            Quiz quiz = Quiz.builder()
                    .id(quizRequestDTO.getId())
                    .title(quizRequestDTO.getTitle())
                    .description(quizRequestDTO.getDescription())
                    .difficulty(quizRequestDTO.getDifficulty())
                    .questions(quizRequestDTO.getQuestions())
                    .build();

            Quiz quizSaved = quizRepository.save(quiz);

            ApiResponse<Quiz> response = new ApiResponse<>(
                    true,
                    "Update Quiz Successfully!",
                    quizSaved,
                    HttpStatus.OK
            );

            log.info("Update Quiz Successfully!");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Failed to update quiz!");

            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Failed to update quiz!",
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<ApiResponse<Void>> deleteById(UUID id) {
        try {
            quizRepository.deleteById(id);

            ApiResponse<Void> response = new ApiResponse<>(
                    true,
                    "Delete Quiz Successfully!",
                    null,
                    HttpStatus.OK
            );

            log.info("Delete Quiz Successfully!");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
            );

        } catch (Exception e) {
            log.error("Failed to delete quiz!");

            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Failed to delete quiz!",
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
