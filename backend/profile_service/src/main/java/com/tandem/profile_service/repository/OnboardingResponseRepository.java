package com.tandem.profile_service.repository;

import com.tandem.profile_service.model.OnboardingResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Array;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;
import java.util.Arrays;

@Repository
public class OnboardingResponseRepository extends GeneralRepository<OnboardingResponse> {

    @Autowired
    public OnboardingResponseRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, "onboarding_responses", "id", "user_id");
    }

    @Override
    protected RowMapper<OnboardingResponse> getRowMapper() {
        return (rs, rowNum) -> {
            UUID id = UUID.fromString(rs.getString("id"));
            UUID userId = UUID.fromString(rs.getString("user_id"));
            UUID pollId = rs.getString("poll_id") != null
                    ? UUID.fromString(rs.getString("poll_id"))
                    : null;
            UUID questionId = rs.getString("question_id") != null
                    ? UUID.fromString(rs.getString("question_id"))
                    : null;
            String answerText = rs.getString("answer_text");

            List<String> answerArray = null;
            Array sqlArray = rs.getArray("answer_array");
            if (sqlArray != null) {
                String[] arr = (String[]) sqlArray.getArray();
                answerArray = Arrays.asList(arr);
            }

            return OnboardingResponse.builder()
                    .id(id)
                    .userId(userId)
                    .pollId(pollId)
                    .questionId(questionId)
                    .answerText(answerText)
                    .answerArray(answerArray)
                    .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                    .build();
        };
    }

    public OnboardingResponse save(OnboardingResponse response) {
        String sql = """
                UPDATE onboarding_responses SET
                    user_id = ?,
                    poll_id = ?,
                    question_id = ?,
                    answer_text = ?,
                    answer_array = ?
                WHERE id = ?
                """;

        jdbcTemplate.update(sql,
                response.getUserId(),
                response.getPollId(),
                response.getQuestionId(),
                response.getAnswerText(),
                toSqlStringArray(response.getAnswerArray()),
                response.getId());
        return response;
    }

    public OnboardingResponse createResponse(UUID userId, UUID pollId, UUID questionId, Object answer) {
        UUID responseId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        OnboardingResponse response = OnboardingResponse.builder()
                .id(responseId)
                .userId(userId)
                .pollId(pollId)
                .questionId(questionId)
                .createdAt(now)
                .build();

        response.setAnswer(answer);

        String sql = """
            INSERT INTO onboarding_responses (
                id, user_id, poll_id, question_id,
                answer_text, answer_array, created_at
            )
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.update(sql,
                responseId,
                userId,
                pollId,
                questionId,
                response.getAnswerText(),
                toSqlStringArray(response.getAnswerArray()),
                Timestamp.valueOf(now));

        return response;
    }

    private Object toSqlStringArray(List<String> list) {
        if (list == null) return null;
        return list.toArray(new String[0]);
    }
}