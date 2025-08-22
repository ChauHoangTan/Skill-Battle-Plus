package com.example.questionservice.service;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.util.ObjectBuilder;
import com.example.questionservice.config.MessageQueueConfig;
import com.example.questionservice.document.QuestionDocument;
import com.example.questionservice.dto.*;
import com.example.questionservice.enums.QuestionType;
import com.example.questionservice.enums.Visibility;
import com.example.questionservice.model.AnswerOption;
import com.example.questionservice.model.Question;
import com.example.questionservice.model.Tag;
import com.example.questionservice.producer.QuestionProducer;
import com.example.questionservice.repository.QuestionRepository;
import com.example.questionservice.repository.QuestionSearchRepository;
import com.example.questionservice.repository.TagRepository;
import com.example.questionservice.response.ApiResponse;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.modelmapper.ModelMapper;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;

import org.elasticsearch.index.query.BoolQueryBuilder;

@Service
@RequiredArgsConstructor
@Log4j2
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionSearchRepository questionSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;
    private final TagRepository tagRepository;
    private final CsvService csvService;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;
    private final QuestionProducer producer;

    public ResponseEntity<ApiResponse<Page<Question>>> getAll(int limit, int pageNumber) {
        try {
            Pageable pageable = PageRequest.of(limit, pageNumber);
            Page<Question> questionList = questionRepository.findAll(pageable);
            ApiResponse<Page<Question>> response = new ApiResponse<>(true,
                    "Retrieve questions success!", questionList, HttpStatus.OK);
            log.info("Get questions success!");
            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Get All Questions Error! ", e);
            ApiResponse<Page<Question>> response = new ApiResponse<>(false,
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
            question.setVisibility(questionDTO.getVisibility());

            List<AnswerOption> answerOptions = questionDTO.getOptions().stream()
                    .map(dto -> {
                        AnswerOption option = new AnswerOption();
                        option.setText(dto.getText());
                        option.setIsCorrect(dto.getIsCorrect());
                        option.setQuestion(question); // Quan trọng
                        return option;
                    })
                    .toList();

            question.setOptions(answerOptions);

            Question questionSaved = questionRepository.save(question);

            // Send to message queue to insert into Elasticsearch
            questionDTO.setId(questionSaved.getId());
            producer.sendQuestionMessage(
                    MessageQueueConfig.ROUTING_KEY_QUESTION_CREATE,
                    questionDTO
            );

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
        question.setVisibility(questionDTO.getVisibility());
        question.setTags(questionDTO.getTags());
        question.setCreatedBy(userId);

        List<AnswerOption> answerOptions = questionDTO.getOptions().stream()
                .map(dto -> {
                    AnswerOption option = new AnswerOption();
                    option.setText(dto.getText());
                    option.setIsCorrect(dto.getIsCorrect());
                    option.setQuestion(question);
                    return option;
                })
                .toList();

        question.setOptions(answerOptions);

        // Save to DB first
        Question saved = questionRepository.save(question);

        // Then send to message queue
        questionDTO.setId(saved.getId());
        producer.sendQuestionMessage(MessageQueueConfig.ROUTING_KEY_QUESTION_UPDATE, questionDTO);

        return saved.getId();
    }

    @Transactional
    public ResponseEntity<ApiResponse<List<UUID>>> createQuestions(List<QuestionDTO> listQuestionDTO, UUID userId) {
        try {
            List<UUID> listQuestionID = listQuestionDTO.stream()
                    .map(
                            questionDTO -> saveQuestion(questionDTO, userId)
                    ).toList();

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
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

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
                    .toList();

            question.setOptions(answerOptions);

            Question questionSaved = questionRepository.save(question);

            // Send to message queue to update in Elasticsearch
            producer.sendQuestionMessage(
                    MessageQueueConfig.ROUTING_KEY_QUESTION_UPDATE,
                    questionDTO
            );

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

            // Send to message queue to delete from Elasticsearch
            producer.sendQuestionMessage(
                    MessageQueueConfig.ROUTING_KEY_QUESTION_DELETE,
                    new QuestionDTO(id, null, null, null, null, null, null)
            );

            ApiResponse<Void> response = new ApiResponse<>(
                    true,
                    "Delete question success!",
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
            CsvMapper csvMapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();

            // Đọc file CSV thành MappingIterator
            MappingIterator<QuestionCsvDTO> it = csvMapper
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
                        .toList();
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
                        .toList();
                dto.setTags(tags);

                return dto;
            }).toList();

        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV file", e);
        }
    }

    @Transactional
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
                        .toList());
                question.setTags(questionDTO.getTags());

                questionRepository.save(question);

                // Send to message queue for create in Elasticsearch
                producer.sendQuestionMessage(
                        MessageQueueConfig.ROUTING_KEY_QUESTION_CREATE,
                        questionDTO
                );

                return question.getId();
            }).toList();

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
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

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

    public ResponseEntity<ApiResponse<Page<QuestionDocument>>> searchQuestions(String keyword,
                                                                               int limit,
                                                                               int pageNumber,
                                                                               List<String> tags,
                                                                               QuestionType questionType,
                                                                               Visibility visibility) {
        try {
            Pageable pageable = PageRequest.of(pageNumber - 1, limit);
            Page<QuestionDocument> searchResults;

            if(questionType == null && visibility == null) {
                searchResults = questionSearchRepository.findByContentContainingAndTagsIn(keyword, tags, pageable);
            } else {
                List<FieldValue> fieldTags = tags.stream()
                                                .map(FieldValue::of)
                                                .toList();

                NativeQuery query = NativeQuery.builder()
                        .withQuery(q -> q.bool(b -> {
                            // must: content match
                            b.must(m -> m.match(mt -> mt.field("content").query(keyword)));

                            // should: tags terms
                            if (!tags.isEmpty()) {
                                b.should(s -> s.terms(t -> t.field("tags").terms(v -> v.value(fieldTags))));
                            }

                            // filter: questionType
                            if (questionType != null) {
                                b.filter(f -> f.term(t -> t.field("questionType").value(questionType.name())));
                            }

                            // filter: visibility
                            if (visibility != null) {
                                b.filter(f -> f.term(t -> t.field("visibility").value(visibility.name())));
                            }

                            return b;
                        }))
                        .withPageable(pageable)
                        .build();

                SearchHits<QuestionDocument> searchHits = elasticsearchOperations.search(query, QuestionDocument.class);
                log.info("Hits: {}", searchHits.getSearchHits());

                searchResults = new PageImpl<>(
                        searchHits.stream().map(SearchHit::getContent).toList(),
                        pageable,
                        searchHits.getTotalHits()
                );
            }

            log.info("Search questions successfully!");
            log.info("Search Result: {}", searchResults);

            return new ResponseEntity<>(
                    new ApiResponse<>(
                            true,
                            "Search questions successfully!",
                            searchResults,
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

    public ResponseEntity<ApiResponse<QuestionResultDTO>> evaluateQuestion(SubmitQuestionAnswerDTO submitQuestionAnswerDTO) {
        try {
            Optional<Question> question = questionRepository.findById(submitQuestionAnswerDTO.getQuestionId());

            if(question.isEmpty()) {
                log.error("Error Get Question!");
                return new ResponseEntity<>(
                        new ApiResponse<>(
                                false,
                                "Error Get Question!",
                                null,
                                HttpStatus.INTERNAL_SERVER_ERROR
                        ), HttpStatus.INTERNAL_SERVER_ERROR
                );
            }

            List<UUID> userAnswers = submitQuestionAnswerDTO.getSelectedOptionIds();

            QuestionResultDTO questionResult = new QuestionResultDTO();
            List<UUID> correctAnswers = questionRepository.findCorrectAnswersByQuestionId(submitQuestionAnswerDTO.getQuestionId());

            questionResult.setQuestionId(submitQuestionAnswerDTO.getQuestionId());
            questionResult.setUserAnswer(submitQuestionAnswerDTO.getSelectedOptionIds());
            questionResult.setCorrectAnswer(correctAnswers);

            Boolean isCorrect = new HashSet<>(userAnswers).equals(new HashSet<>(correctAnswers));

            questionResult.setIsCorrect(isCorrect);
            questionResult.setScore(isCorrect ? 1 : 0);

            log.info("Evaluate Question Successfully!");

            return new ResponseEntity<>(
                    new ApiResponse<>(
                            true,
                            "Evaluate Question Successfully!",
                            questionResult,
                            HttpStatus.OK
                    ), HttpStatus.OK
            );

        } catch (Exception e) {
            log.error("Error Evaluate Question!", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Error Evaluate Question!",
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ), HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
