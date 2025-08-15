package com.example.questionservice.document;

import jakarta.persistence.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@Document(indexName = "questions")
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

    @Field(type = FieldType.Nested)
    private List<String> options; // Save options, nested to objects

    private String createdBy;     // UUID -> String
}
