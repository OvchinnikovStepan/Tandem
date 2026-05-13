package com.tandem.chat_service.dao.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tandem.chat_service.dao.MessageDao;
import com.tandem.chat_service.dao.mapper.MessageDaoRowMapper;
import com.tandem.chat_service.dao.mapper.MessageJdbcMapper;
import com.tandem.chat_service.dao.model.MessageEntity;
import com.tandem.chat_service.dao.queries.MessageQueries;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class MessageDaoImpl implements MessageDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final MessageDaoRowMapper rowMapper;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void insert(MessageEntity entity) {
        jdbcTemplate.update(MessageQueries.INSERT, MessageJdbcMapper.mapInsertParams(entity, objectMapper));
    }

    @Override
    public void update(MessageEntity entity) {
        jdbcTemplate.update(MessageQueries.UPDATE, MessageJdbcMapper.mapUpdateParams(entity));
    }

    @Override
    public void softDelete(UUID id, LocalDateTime deletedAt) {
        jdbcTemplate.update(MessageQueries.SOFT_DELETE, Map.of("id", id, "deletedAt", deletedAt));
    }

    @Override
    public Optional<MessageEntity> findById(UUID id) {
        List<MessageEntity> results = jdbcTemplate.query(
                MessageQueries.SELECT_BY_ID,
                Map.of("id", id),
                rowMapper.rowMapper
        );
        return results.stream().findFirst();
    }

    @Override
    public List<MessageEntity> findMessagesByChatIdBefore(UUID chatId, LocalDateTime before, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("chatId", chatId);
        params.put("before", before);
        params.put("limit", limit);
        return jdbcTemplate.query(MessageQueries.SELECT_BY_CHAT_ID_PAGINATED_BEFORE, params, rowMapper.rowMapper);
    }

    @Override
    public List<MessageEntity> findMessagesByChatIdAfter(UUID chatId, LocalDateTime after, int limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("chatId", chatId);
        params.put("after", after);
        params.put("limit", limit);
        return jdbcTemplate.query(MessageQueries.SELECT_BY_CHAT_ID_PAGINATED_AFTER, params, rowMapper.rowMapper);
    }
}
