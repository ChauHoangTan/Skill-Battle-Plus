package com.example.questionservice.Service;

import com.example.questionservice.DTO.QuestionDTO;
import com.example.questionservice.Model.AnswerOption;
import com.example.questionservice.Model.Question;
import com.example.questionservice.Repository.QuestionRepository;
import com.example.questionservice.Response.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class QuestionService {

    final private QuestionRepository questionRepository;
    final private ModelMapper mapper;

    public ResponseEntity<ApiResponse<List<Question>>> getAll() {
        try {
            List<Question> questionList = questionRepository.findAll();
            ApiResponse<List<Question>> response = new ApiResponse<>(true,
                    "Retrieve questions success!", questionList, HttpStatus.OK);
            log.info("Get questions success!");
            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Get All Questions Error! ", e);
            ApiResponse<List<Question>> response = new ApiResponse<>(false,
                    "Failed to retrieve questions", null, HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<ApiResponse<Question>> getById(UUID id) {
        try {
            Optional<Question> question = questionRepository.findById(id);

            if(question.isEmpty()) {
                ApiResponse<Question> response = new ApiResponse<>(
                        false,
                        "Fail to retrieve question!",
                        null,
                        HttpStatus.BAD_REQUEST
                );
                return new ResponseEntity<>(
                        response,
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }

            ApiResponse<Question> response = new ApiResponse<>(
                    true,
                    "Get question success!",
                    question.get(),
                    HttpStatus.OK
            );

            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Error get question by Id: {}", id);
            ApiResponse<Question> response = new ApiResponse<>(false,
                    "Failed to retrieve question!", null, HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<ApiResponse<Question>> create(UUID quizId, QuestionDTO questionDTO) {
        try {
            List<AnswerOption> answerOptions = questionDTO.getOptions()
                    .stream()
                    .map(optionDTO -> mapper.map(optionDTO, AnswerOption.class))
                    .toList();

            Question question = Question.builder()
                    .quizId(questionDTO.getQuizId())
                    .content(questionDTO.getContent())
                    .questionType(questionDTO.getQuestionType())
                    .options(answerOptions)
                    .build();

            Question questionSaved = questionRepository.save(question);

            ApiResponse<Question> response = new ApiResponse<>(
                    true,
                    "Create question success!",
                    questionSaved,
                    HttpStatus.OK
            );
            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
            );

        } catch (Exception e) {
            log.error("Error creating question!", e);
            ApiResponse<Question> response = new ApiResponse<>(
                    false,
                    "Fail to create question!",
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<ApiResponse<Question>> update(QuestionDTO questionDTO) {
        try {
            List<AnswerOption> answerOptions = questionDTO.getOptions()
                    .stream()
                    .map(AnswerOptionDTO -> mapper.map(AnswerOptionDTO, AnswerOption.class))
                    .toList();

            Question question = Question.builder()
                    .id(questionDTO.getId())
                    .quizId(questionDTO.getQuizId())
                    .content(questionDTO.getContent())
                    .questionType(questionDTO.getQuestionType())
                    .options(answerOptions)
                    .build();

            questionRepository.save(question);

            ApiResponse<Question> response = new ApiResponse<>(
                    true,
                    "Updating question success!",
                    question,
                    HttpStatus.OK
            );

            log.info("Updating question success! {}", questionDTO);

            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Error updating question");

            ApiResponse<Question> response = new ApiResponse<>(
                    false,
                    "Failed to update question!",
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<ApiResponse<Void>> delete(UUID id) {
        try {
            questionRepository.deleteById(id);

            ApiResponse<Void> response = new ApiResponse<>(
                    true,
                    "Updating question success!",
                    null,
                    HttpStatus.OK
            );

            log.info("Delete question succeed! {}", id);

            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
            );

        } catch (Exception e) {
            log.error("Error delete question");

            ApiResponse<Void> response = new ApiResponse<>(
                    false,
                    "Failed to delete question!",
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );

            return new ResponseEntity<>(
                    response,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
