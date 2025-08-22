package com.example.quizservice.document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Document(indexName = "quizzes")
public class QuizDocument {
    @Id
    private String id;  // convert UUID -> String

    @Field(type = FieldType.Text)
    private String title;

    @Field(type = FieldType.Text)
    private String description;

    @Field(type = FieldType.Keyword)
    private String difficulty;  // Enum as String

    @Field(type = FieldType.Keyword)
    private String visibility;  // Enum as String

    @Field(type = FieldType.Keyword)
    private List<String> tags;
}
