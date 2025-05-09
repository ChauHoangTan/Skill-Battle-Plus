package com.example.questionservice.Service;

import com.example.questionservice.DTO.AnswerOptionDTO;
import com.example.questionservice.DTO.ListQuestionDTO;
import com.example.questionservice.DTO.QuestionCsvDTO;
import com.example.questionservice.DTO.QuestionDTO;
import com.example.questionservice.Enum.QuestionType;
import com.example.questionservice.Enum.Visibility;
import com.example.questionservice.Model.AnswerOption;
import com.example.questionservice.Model.Question;
import com.example.questionservice.Model.Tag;
import com.example.questionservice.Repository.QuestionRepository;
import com.example.questionservice.Repository.TagRepository;
import com.example.questionservice.Response.ApiResponse;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class QuestionService {

    final private QuestionRepository questionRepository;
    final private TagRepository tagRepository;
    final private CsvService csvService;
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

            if (question.isEmpty()) {
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

    public ResponseEntity<ApiResponse<Question>> create(QuestionDTO questionDTO, UUID userId) {
        try {
            Question question = new Question();
            question.setContent(questionDTO.getContent());
            question.setQuestionType(questionDTO.getQuestionType());
            question.setCreatedBy(userId);

            List<AnswerOption> answerOptions = questionDTO.getOptions().stream()
                    .map(dto -> {
                        AnswerOption option = new AnswerOption();
                        option.setText(dto.getText());
                        option.setIsCorrect(dto.getIsCorrect());
                        option.setQuestion(question); // Quan trọng
                        return option;
                    })
                    .collect(Collectors.toList());

            question.setOptions(answerOptions);

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

    public UUID saveQuestion(QuestionDTO questionDTO, UUID userId) {
        Question question = new Question();
        question.setContent(questionDTO.getContent());
        question.setQuestionType(questionDTO.getQuestionType());
        question.setCreatedBy(userId);

        List<AnswerOption> answerOptions = questionDTO.getOptions().stream()
                .map(dto -> {
                    AnswerOption option = new AnswerOption();
                    option.setText(dto.getText());
                    option.setIsCorrect(dto.getIsCorrect());
                    option.setQuestion(question);
                    return option;
                })
                .collect(Collectors.toList());

        question.setOptions(answerOptions);

        return questionRepository.save(question).getId();
    }

    public ResponseEntity<ApiResponse<List<UUID>>> createQuestions(ListQuestionDTO listQuestionDTO, UUID userId) {
        try {
            List<UUID> listQuestionID = listQuestionDTO.getQuestionDTOList().stream()
                    .map(
                            questionDTO -> saveQuestion(questionDTO, userId)
                    ).collect(Collectors.toList());

            ApiResponse<List<UUID>> response = new ApiResponse<>(
                    true,
                    "Create questions success!",
                    listQuestionID,
                    HttpStatus.OK
            );
            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
            );

        } catch (Exception e) {
            log.error("Error creating questions!", e);
            ApiResponse<List<UUID>> response = new ApiResponse<>(
                    false,
                    "Fail to create questions!",
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
            Question question = questionRepository.findById(questionDTO.getId()).orElseThrow();
            question.setId(questionDTO.getId());
            question.setQuizId(questionDTO.getQuizId());
            question.setContent(questionDTO.getContent());
            question.setQuestionType(questionDTO.getQuestionType());

            List<AnswerOption> answerOptions = questionDTO.getOptions().stream()
                    .map(dto -> {
                        AnswerOption option = new AnswerOption();
                        option.setId(dto.getId());
                        option.setText(dto.getText());
                        option.setIsCorrect(dto.getIsCorrect());
                        option.setQuestion(question); // Quan trọng
                        return option;
                    })
                    .collect(Collectors.toList());

            question.setOptions(answerOptions);

            Question questionSaved = questionRepository.save(question);

            ApiResponse<Question> response = new ApiResponse<>(
                    true,
                    "Updating question success!",
                    questionSaved,
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

    public List<QuestionDTO> scanListQuestionsCSV(MultipartFile file) {
        try {
            // Tạo đối tượng CsvMapper để xử lý CSV
            CsvMapper mapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();

            // Đọc file CSV thành MappingIterator
            MappingIterator<QuestionCsvDTO> it = mapper
                    .readerFor(QuestionCsvDTO.class)
                    .with(schema)
                    .readValues(file.getInputStream());

            // Convert từ QuestionCSVDTO sang QuestionDTO
            return it.readAll().stream().map(csv -> {
                QuestionDTO dto = new QuestionDTO();
                dto.setId(csv.getId() == null || csv.getId().isEmpty() ? null : UUID.fromString(csv.getId()));
                dto.setContent(csv.getContent());
                dto.setQuestionType(QuestionType.valueOf(csv.getQuestionType()));
                dto.setVisibility(Visibility.valueOf(csv.getVisibility()));

                // Parse answer options
                List<AnswerOptionDTO> options = Arrays.stream(csv.getOptions().split("\\|"))
                        .map(s -> {
                            String[] parts = s.split(":");
                            return new AnswerOptionDTO(null, parts[0], Boolean.parseBoolean(parts[1]));
                        })
                        .collect(Collectors.toList());
                dto.setOptions(options);

                // Parse tags
                List<Tag> tags = Arrays.stream(csv.getTags().split("\\|"))
                        .map(tagName -> {
                            Optional<Tag> existingTag = tagRepository.findByName(tagName);
                            if (existingTag.isPresent()) {
                                return existingTag.get();  // Nếu tag đã tồn tại thì lấy từ DB
                            } else {
                                Tag tag = new Tag();
                                tag.setName(tagName);
                                return tagRepository.save(tag);  // Nếu tag chưa tồn tại thì lưu mới
                            }
                        })
                        .collect(Collectors.toList());
                dto.setTags(tags);

                return dto;
            }).collect(Collectors.toList());

        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file", e);
        }
    }

    public ResponseEntity<ApiResponse<List<UUID>>> importQuestions(MultipartFile file, UUID userId) {
        try {
            List<QuestionDTO> questionDTOList = scanListQuestionsCSV(file);

            // Lưu các câu hỏi vào cơ sở dữ liệu và lấy ID
            List<UUID> questionIds = questionDTOList.stream().map(questionDTO -> {
                Question question = new Question();

                if (questionDTO.getId() != null) {
                    Optional<Question> questionSaved = questionRepository.findById(questionDTO.getId());
                    if (questionSaved.isPresent()) {
                        question = questionSaved.get();
                    }
                }

                question.setContent(questionDTO.getContent());
                question.setQuestionType(questionDTO.getQuestionType());
                question.setVisibility(questionDTO.getVisibility());
                question.setCreatedBy(userId);

                Question finalQuestion = question;
                question.setOptions(questionDTO.getOptions().stream()
                        .map(optionDTO -> {
                            AnswerOption answerOption = new AnswerOption();
                            answerOption.setText(optionDTO.getText());
                            answerOption.setIsCorrect(optionDTO.getIsCorrect());
                            answerOption.setQuestion(finalQuestion);
                            return answerOption;
                        })
                        .collect(Collectors.toList()));
                question.setTags(questionDTO.getTags());

                questionRepository.save(question);
                return question.getId();
            }).collect(Collectors.toList());

            // Trả về ResponseEntity với thông tin thành công
            ApiResponse<List<UUID>> response = new ApiResponse<>(
                    true,
                    "Import questions successfully!",
                    questionIds,
                    HttpStatus.OK
            );

            log.info("Import questions successfully!");

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Failed to import questions!", e);

            ApiResponse<List<UUID>> response = new ApiResponse<>(
                    false,
                    "Failed to import questions!",
                    null,
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public ResponseEntity<Resource> exportQuestions(
            UUID userId,
            Visibility visibility,
            List<String> tags,
            QuestionType questionType,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        try {
            // Lấy danh sách questions theo filter
            Specification<Question> specification = QuestionSpecification.filterOptions(
                    userId, visibility, tags, questionType, fromDate, toDate
            );
            List<Question> questions = questionRepository.findAll(specification);

            // Ghi ra CSV
            ByteArrayInputStream csvStream = csvService.writeToCSV(questions);
            InputStreamResource resource = new InputStreamResource(csvStream);

            log.info("Export CSV Successfully!");

            String filename = "questions.csv";
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .contentType(MediaType.parseMediaType("text/csv"))
                    .body(resource);
        } catch (Exception e) {
            log.error("Failed to export CSV!", e);
            return new ResponseEntity<>(
               null,
               HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    public ResponseEntity<ApiResponse<Page<Question>>> searchQuestions(String keyword, int limit, int pageNumber) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, limit);
            Page<Question> page = questionRepository.search(keyword, pageable);

            log.info("Search questions successfully!");

            return new ResponseEntity<>(
                    new ApiResponse<>(
                            true,
                            "Search questions successfully!",
                            page,
                            HttpStatus.OK
                    ),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Failed to search questions!", e);
            return new ResponseEntity<>(
                  new ApiResponse<>(
                          false,
                          "Failed to search questions!",
                          null,
                          HttpStatus.INTERNAL_SERVER_ERROR
                  ),
                  HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
