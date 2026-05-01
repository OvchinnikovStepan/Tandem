package com.tandem.chat_service.dao.impl;

import com.tandem.chat_service.dao.ChatParticipantDao;
import com.tandem.chat_service.dao.mapper.ChatParticipantDaoRowMapper;
import com.tandem.chat_service.dao.mapper.ChatParticipantJdbcMapper;
import com.tandem.chat_service.dao.model.ChatParticipantEntity;
import com.tandem.chat_service.dao.queries.ChatParticipantQueries;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Repository
public class ChatParticipantDaoImpl implements ChatParticipantDao {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final ChatParticipantDaoRowMapper rowMapper;

    public ChatParticipantDaoImpl(NamedParameterJdbcTemplate jdbcTemplate, ChatParticipantDaoRowMapper rowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = rowMapper;
    }

    @Override
    public void insert(ChatParticipantEntity entity) {
        jdbcTemplate.update(ChatParticipantQueries.INSERT, ChatParticipantJdbcMapper.mapInsertParams(entity));
    }

    @Override
    public void update(ChatParticipantEntity entity) {
        jdbcTemplate.update(ChatParticipantQueries.UPDATE, ChatParticipantJdbcMapper.mapUpdateParams(entity));
    }

    @Override
    public void delete(UUID id) {
        jdbcTemplate.update(ChatParticipantQueries.DELETE, Map.of("id", id));
    }

    @Override
    public void deleteByChatIdAndUserId(UUID chatId, UUID userId) {
        jdbcTemplate.update(
                ChatParticipantQueries.DELETE_BY_CHAT_ID_AND_USER_ID,
                Map.of("chatId", chatId, "userId", userId)
        );
    }


    @Override
    public List<ChatParticipantEntity> findByChatId(UUID chatId) {
        return jdbcTemplate.query(
                ChatParticipantQueries.SELECT_BY_CHAT_ID,
                Map.of("chatId", chatId),
                rowMapper.rowMapper
        );
    }


    @Override
    public boolean isParticipant(UUID chatId, UUID userId) {
        Boolean result = jdbcTemplate.queryForObject(
                ChatParticipantQueries.CHECK_IS_PARTICIPANT,
                Map.of("chatId", chatId, "userId", userId),
                Boolean.class
        );
        return result != null && result;
    }


    @Override
    public void updateMutedStatus(UUID chatId, UUID userId, boolean isMuted) {
        jdbcTemplate.update(
                ChatParticipantQueries.UPDATE_MUTED_STATUS,
                Map.of("chatId", chatId, "userId", userId, "isMuted", isMuted)
        );
    }

    @Override
    public void updateBannedStatus(UUID chatId, UUID userId, boolean isBanned) {
        jdbcTemplate.update(
                ChatParticipantQueries.UPDATE_BANNED_STATUS,
                Map.of("chatId", chatId, "userId", userId, "isBanned", isBanned)
        );
    }
}