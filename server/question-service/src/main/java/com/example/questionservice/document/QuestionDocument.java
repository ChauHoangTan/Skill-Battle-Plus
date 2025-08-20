package com.example.questionservice.document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.WriteTypeHint;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "questions", writeTypeHint = WriteTypeHint.FALSE)
public class QuestionDocument {
    @Id
    private String id;  // convert UUID -> String

    private String quizId;  // convert UUID -> String

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword)
    private String questionType;  // Enum as String

    @Field(type = FieldType.Keyword)
    private String visibility;    // Enum as String

    @Field(type = FieldType.Keyword)
    private List<String> tags;    // Save tags name

    @Field(type = FieldType.Nested, includeInParent = true)
    private List<AnswerOptionDocument> options; // Save options, nested to objects
}
