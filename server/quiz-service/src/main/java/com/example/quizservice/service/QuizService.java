package com.example.quizservice.service;

import com.example.quizservice.dto.AnswerOptionDTO;
import com.example.quizservice.dto.QuestionCsvDTO;
import com.example.quizservice.dto.QuestionDTO;
import com.example.quizservice.dto.QuizRequestDTO;
import com.example.quizservice.enums.QuestionType;
import com.example.quizservice.enums.Visibility;
import com.example.quizservice.model.Quiz;
import com.example.quizservice.model.Tag;
import com.example.quizservice.repository.QuizRepository;
import com.example.quizservice.repository.TagRepository;
import com.example.quizservice.response.ApiResponse;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Log4j2
public class QuizService {

    private final QuizRepository quizRepository;
    private final TagRepository tagRepository;
    private final WebClient webClient = WebClient.builder().baseUrl("http://localhost:8083/questions").build();
    private static final String CREATE_LIST_URI = "create-list";

    public ResponseEntity<ApiResponse<Page<Quiz>>> getAll(int limit, int pageNumber) {
        try {
            Pageable pageable = PageRequest.of(limit, pageNumber);
            Page<Quiz> quizList = quizRepository.findAll(pageable);

            ApiResponse<Page<Quiz>> response = new ApiResponse<>(
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
            log.error("Failed to retrieve quizzes!", e);

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

            if (quiz.isEmpty()) {
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
            log.error("Failed to retrieve quiz!", e);

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

    @Transactional
    public ResponseEntity<ApiResponse<Quiz>> create(QuizRequestDTO quizRequestDTO, String username,
                                                    String roles, String userId) {
        try {
            List<UUID> questionsID = new ArrayList<>(quizRequestDTO.getOldQuestions());

            Quiz quiz = Quiz.builder()
                    .title(quizRequestDTO.getTitle())
                    .description(quizRequestDTO.getDescription())
                    .difficulty(quizRequestDTO.getDifficulty())
                    .visibility(quizRequestDTO.getVisibility())
                    .questions(questionsID)
                    .tags(quizRequestDTO.getTags())
                    .build();

            Quiz quizSaved = quizRepository.save(quiz);

//          Set quiz id for questions
            Quiz finalQuizSaved = quizSaved;
            List<QuestionDTO> questionDTOListWithQuizId = quizRequestDTO.getNewQuestions()
                    .stream()
                    .peek(questionDTO -> questionDTO.setQuizId(finalQuizSaved.getId()))
                    .toList();

            log.info("questionDTOListWithQuizId", questionDTOListWithQuizId.toString());

            List<UUID> resultNewQuestions = Collections.emptyList();
            if (questionDTOListWithQuizId != null && !questionDTOListWithQuizId.isEmpty()) {

                ApiResponse<List<UUID>> response = webClient
                        .post()
                        .uri(CREATE_LIST_URI)
                        .header("X-username", username)
                        .header("X-roles", roles)
                        .header("X-userId", userId)
                        .bodyValue(questionDTOListWithQuizId)
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<ApiResponse<List<UUID>>>() {})
                        .block();

                if (response == null || !response.isSuccess()) {
                    throw new RuntimeException("Failed to create questions via API");
                }

                resultNewQuestions = response.getData();

            }

            assert resultNewQuestions != null;
            questionsID.addAll(resultNewQuestions);

            finalQuizSaved.setQuestions(questionsID);
            quizRepository.save(quiz);

            ApiResponse<Quiz> response = new ApiResponse<>(
                    true,
                    "Create Quiz Successfully!",
                    finalQuizSaved,
                    HttpStatus.OK
            );

            log.info("Create Quiz Successfully!");

            return new ResponseEntity<>(
                    response,
                    HttpStatus.OK
            );
        } catch (Exception e) {
            log.error("Failed to save quiz!", e);

            throw new RuntimeException("Failed to save quiz!", e);
        }
    }

    @Transactional
    public ResponseEntity<ApiResponse<Quiz>> update(QuizRequestDTO quizRequestDTO) {
        try {
            List<UUID> questionsID = new ArrayList<>(quizRequestDTO.getOldQuestions());

            List<UUID> newQuestions = Collections.emptyList();
            if (quizRequestDTO.getNewQuestions() != null && !quizRequestDTO.getNewQuestions().isEmpty()) {
                newQuestions = webClient
                        .post()
                        .uri(CREATE_LIST_URI)
                        .bodyValue(quizRequestDTO.getNewQuestions())
                        .retrieve()
                        .bodyToMono(new ParameterizedTypeReference<List<UUID>>() {})
                        .block();
            }

            assert newQuestions != null;
            questionsID.addAll(newQuestions);

            Quiz quiz = Quiz.builder()
                    .id(quizRequestDTO.getId())
                    .title(quizRequestDTO.getTitle())
                    .description(quizRequestDTO.getDescription())
                    .difficulty(quizRequestDTO.getDifficulty())
                    .questions(questionsID)
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
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();

            log.error("Failed to update quiz!", e);

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
            log.error("Failed to delete quiz!", e);

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

    public ResponseEntity<ApiResponse<Page<Quiz>>> searchQuizzes(String keyword, int limit, int pageNumber) {
        try {
            Pageable pageable = PageRequest.of(pageNumber, limit);
            Page<Quiz> quizzesFTS = quizRepository.searchByFTS(keyword, pageable);

            List<Quiz> combinedResults = new ArrayList<>(quizzesFTS.getContent());

            if(!quizzesFTS.hasContent()) {
                Pageable pageableFuzzy = PageRequest.of(0, limit);
                Page<Quiz> quizzesFuzzy = quizRepository.searchByFuzzyMatch(keyword, pageableFuzzy);
                combinedResults.addAll(quizzesFuzzy.getContent());
            }

            Page<Quiz> finalPage = new PageImpl<>(combinedResults, pageable, combinedResults.size());

            return new ResponseEntity<>(
                    new ApiResponse<>(
                            true,
                            "Search quizzes successfully!",
                            finalPage,
                            HttpStatus.OK
                    ),
                    HttpStatus.OK
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Failed to search quizzes!",
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ),
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
    public ResponseEntity<ApiResponse<Void>> importQuestions(MultipartFile multipartFile, UUID quizId, UUID userId) {
        try {
            Optional<Quiz> quiz = quizRepository.findById(quizId);

            if(quiz.isEmpty()) {
                log.error("Quiz id is empty!");
                return new ResponseEntity<>(
                        new ApiResponse<>(
                                false,
                                "Failed to import questions to quiz!",
                                null,
                                HttpStatus.INTERNAL_SERVER_ERROR
                        ),
                        HttpStatus.INTERNAL_SERVER_ERROR
                );
            }

            Quiz quizEntity = quiz.get();

            List<QuestionDTO> questionDTOList = scanListQuestionsCSV(multipartFile);

            Map<Boolean, List<QuestionDTO>> partitioned = questionDTOList.stream()
                            .collect(Collectors.partitioningBy(questionDTO -> questionDTO.getId() != null));

            List<QuestionDTO> nonNullQuestionList = partitioned.get(true);
            List<QuestionDTO> nullQuestionList = partitioned.get(false);


            List<UUID> listNewQuestionId;

            listNewQuestionId = webClient
                    .post()
                    .uri(CREATE_LIST_URI)
                    .header("X-userId", userId.toString())
                    .bodyValue(nullQuestionList)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<UUID>>() {})
                    .block();

            List<UUID> listOldQuestionId = nonNullQuestionList.stream()
                    .map(QuestionDTO::getId)
                    .toList();

            List<UUID> listFinalQuestionId = new ArrayList<>();

            if(listNewQuestionId != null) {
                listFinalQuestionId.addAll(listNewQuestionId);
            }

            listFinalQuestionId.addAll(listOldQuestionId);

            quizEntity.setQuestions(listFinalQuestionId);

            quizRepository.save(quizEntity);

            return new ResponseEntity<>(
                    new ApiResponse<>(
                            true,
                            "Import questions to quiz successfully!",
                            null,
                            HttpStatus.OK
                    ),
                    HttpStatus.OK
            );

        } catch (Exception e) {
            log.error("Failed to import questions to quiz!", e);
            return new ResponseEntity<>(
                    new ApiResponse<>(
                            false,
                            "Failed to import questions to quiz!",
                            null,
                            HttpStatus.INTERNAL_SERVER_ERROR
                    ),
                    HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }
}
