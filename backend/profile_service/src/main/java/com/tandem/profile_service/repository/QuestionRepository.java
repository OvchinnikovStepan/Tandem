package com.tandem.profile_service.repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.profile_service.model.Question;
import com.tandem.profile_service.model.Question.QuestionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

@Repository
public class QuestionRepository extends GeneralRepository<Question> {

    private final ObjectMapper objectMapper;

    @Autowired
    public QuestionRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        super(jdbcTemplate, "questions", "id");
        this.objectMapper = objectMapper;
    }

    @Override
    protected RowMapper<Question> getRowMapper() {
        return (rs, rowNum) -> {
            UUID id = UUID.fromString(rs.getString("id"));
            UUID pollId = rs.getString("poll_id") != null
                    ? UUID.fromString(rs.getString("poll_id"))
                    : null;

            Map<String, Object> validationRules = null;
            String validationJson = rs.getString("validation_rules");
            if (validationJson != null) {
                try {
                    validationRules = objectMapper.readValue(
                            validationJson,
                            new TypeReference<Map<String, Object>>() {});
                } catch (Exception e) {
                    validationRules = new HashMap<>();
                }
            }

            List<String> options = null;
            String optionsJson = rs.getString("options");
            if (optionsJson != null) {
                try {
                    options = objectMapper.readValue(
                            optionsJson,
                            new TypeReference<List<String>>() {});
                } catch (Exception e) {
                    options = new ArrayList<>();
                }
            }

            return Question.builder()
                    .id(id)
                    .pollId(pollId)
                    .questionOrder(rs.getInt("question_order"))
                    .questionType(QuestionType.fromString(rs.getString("question_type")))
                    .label(rs.getString("label"))
                    .description(rs.getString("description"))
                    .isRequired(rs.getBoolean("is_required"))
                    .validationRules(validationRules)
                    .options(options)
                    .profileField(rs.getString("profile_field"))   // <‑‑ новое поле
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .build();
        };
    }

    public Question save(Question question) {
        LocalDateTime now = LocalDateTime.now();
        if (question.getId() == null) {
            question.setId(UUID.randomUUID());
            question.setCreatedAt(now);

            String sql = """
                INSERT INTO questions (
                    id, poll_id, question_order, question_type, label,
                    description, is_required, validation_rules, options,
                    profile_field, created_at
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?::jsonb, ?::jsonb, ?, ?)
                """;

            String validationJson = toJsonOrNull(question.getValidationRules());
            String optionsJson = toJsonOrNull(question.getOptions());

            jdbcTemplate.update(sql,
                    question.getId(),
                    question.getPollId(),
                    question.getQuestionOrder(),
                    question.getQuestionType().getValue(),
                    question.getLabel(),
                    question.getDescription(),
                    question.isRequired(),
                    validationJson,
                    optionsJson,
                    question.getProfileField(),
                    Timestamp.valueOf(question.getCreatedAt()));
        } else {
            String sql = """
                UPDATE questions SET
                    poll_id = ?,
                    question_order = ?,
                    question_type = ?,
                    label = ?,
                    description = ?,
                    is_required = ?,
                    validation_rules = ?::jsonb,
                    options = ?::jsonb,
                    profile_field = ?
                WHERE id = ?
                """;

            String validationJson = toJsonOrNull(question.getValidationRules());
            String optionsJson = toJsonOrNull(question.getOptions());

            jdbcTemplate.update(sql,
                    question.getPollId(),
                    question.getQuestionOrder(),
                    question.getQuestionType().getValue(),
                    question.getLabel(),
                    question.getDescription(),
                    question.isRequired(),
                    validationJson,
                    optionsJson,
                    question.getProfileField(),
                    question.getId());
        }
        return question;
    }

    public List<Question> findByPollId(UUID pollId) {
        String sql = "SELECT * FROM questions WHERE poll_id = ? ORDER BY question_order";
        return jdbcTemplate.query(sql, getRowMapper(), pollId);
    }

    private String toJsonOrNull(Object value) {
        if (value == null) return null;
        try {
            return objectMapper.writeValueAsString(value);
        } catch (Exception e) {
            return null;
        }
    }
}

