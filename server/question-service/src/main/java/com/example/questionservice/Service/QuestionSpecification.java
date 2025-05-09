package com.example.questionservice.Service;

import com.example.questionservice.Enum.QuestionType;
import com.example.questionservice.Enum.Visibility;
import com.example.questionservice.Model.Question;
import com.example.questionservice.Model.Tag;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class QuestionSpecification {
    public static Specification<Question> filterOptions(
            UUID userId,
            Visibility visibility,
            List<String> tags,
            QuestionType questionType,
            LocalDate fromDate,
            LocalDate toDate
    ) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // if have userId -> filter follow user
            if (userId != null) {
                predicates.add(cb.equal(root.get("createdBy"), userId));
            } else {
                // if do not have userId → just get PUBLIC question
                predicates.add(cb.equal(root.get("visibility"), Visibility.PUBLIC));
            }

            // if have visibility -> filter follow it
            if (visibility != null) {
                predicates.add(cb.equal(root.get("visibility"), visibility));
            }

            // filter follow time
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdAt"), fromDate.atStartOfDay()));
            }

            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdAt"), toDate.atTime(23, 59, 59)));
            }

            // follow question type
            if (questionType != null) {
                predicates.add(cb.equal(root.get("questionType"), questionType));
            }

            // follow tags
            if (tags != null && !tags.isEmpty()) {
                Join<Question, Tag> tagJoin = root.join("tags");
                predicates.add(tagJoin.get("name").in(tags));
                query.distinct(true);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
