package com.tandem.chat_service.dao.impl;

import com.tandem.chat_service.dao.ChatDao;
import com.tandem.chat_service.dao.mapper.ChatDaoRowMapper;
import com.tandem.chat_service.dao.mapper.ChatJdbcMapper;
import com.tandem.chat_service.dao.model.ChatEntity;
import com.tandem.chat_service.dao.queries.ChatQueries;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;


@Repository
public class ChatDaoImpl implements ChatDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ChatDaoRowMapper rowMapper;

    public ChatDaoImpl(NamedParameterJdbcTemplate jdbcTemplate, ChatDaoRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public void insert(ChatEntity entity) {
        jdbcTemplate.update(ChatQueries.INSERT, ChatJdbcMapper.mapInsertParams(entity));
    }

    @Override
    public void update(ChatEntity entity) {
        jdbcTemplate.update(ChatQueries.UPDATE, ChatJdbcMapper.mapUpdateParams(entity));
    }

    @Override
    public void delete(UUID id) {
        jdbcTemplate.update(ChatQueries.DELETE, Map.of("id", id));
    }

    @Override
    public Optional<ChatEntity> findById(UUID id) {
        List<ChatEntity> results = jdbcTemplate.query(
                ChatQueries.SELECT_BY_ID,
                Map.of("id", id),
                rowMapper.rowMapper
        );
        return results.stream().findFirst();
    }

    @Override
    public Optional<ChatEntity> findByGroupId(UUID groupId) {
        List<ChatEntity> results = jdbcTemplate.query(
                ChatQueries.SELECT_BY_GROUP_ID,
                Map.of("groupId", groupId),
                rowMapper.rowMapper
        );
        return results.stream().findFirst();
    }

    @Override
    public List<ChatEntity> findPersonalChatsByUser(UUID userId) {
        return jdbcTemplate.query(
                ChatQueries.SELECT_PERSONAL_CHATS_BY_USER,
                Map.of("userId", userId),
                rowMapper.rowMapper
        );
    }

    @Override
    public List<ChatEntity> findGroupChatsByUser(UUID userId) {
        return jdbcTemplate.query(
                ChatQueries.SELECT_GROUP_CHATS_BY_USER,
                Map.of("userId", userId),
                rowMapper.rowMapper
        );
    }

    @Override
    public void updateLastMessageAt(UUID chatId, LocalDateTime lastMessageAt) {
        jdbcTemplate.update(
                ChatQueries.UPDATE_LAST_MESSAGE_AT,
                Map.of("id", chatId, "lastMessageAt", lastMessageAt)
        );
    }
}