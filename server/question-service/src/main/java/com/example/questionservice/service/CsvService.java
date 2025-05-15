package com.example.questionservice.service;

import com.example.questionservice.model.Question;
import com.example.questionservice.model.Tag;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Log4j2
public class CsvService {

    public ByteArrayInputStream writeToCSV(List<Question> questionList) {
        // Định nghĩa header đúng theo yêu cầu
        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("id", "content", "questionType", "visibility", "options", "tags")
                .setDelimiter(',') // dùng dấu phẩy
                .build();
        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format)) {

            for (Question q : questionList) {
                // Gộp options thành chuỗi "text:isCorrect|text2:isCorrect2"
                String options = q.getOptions().stream()
                        .map(opt -> opt.getText() + ":" + opt.getIsCorrect())
                        .collect(Collectors.joining("|"));

                // Gộp tags thành chuỗi "tag1|tag2"
                String tags = q.getTags().stream()
                        .map(Tag::getName)
                        .collect(Collectors.joining("|"));

                csvPrinter.printRecord(
                        // q.getId() có thể null, để CSV để trống
                        q.getId() != null ? q.getId().toString() : "",
                        q.getContent(),
                        q.getQuestionType().name(),
                        q.getVisibility().name(),
                        options,
                        tags
                );
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Failed to write CSV: " + e.getMessage(), e);
        }
    }
}
