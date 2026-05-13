package com.tandem.interest_service.service;

import com.tandem.interest_service.service.model.response.GroupMatchingResponse;
import com.tandem.interest_service.service.model.response.UserMatchingResponse;

import java.util.List;
import java.util.UUID;

public interface MatchingService {
    List<UserMatchingResponse> getMatchingUsers(UUID userId, int limit, int minMatchCount); // Возвращает мэтчинг по пользователям
    List<GroupMatchingResponse> getMatchingGroups(UUID userId, int limit, int minMatchCount);
}
