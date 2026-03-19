package com.tandem.interest_service.service;

import com.tandem.interest_service.service.model.request.UserInterestRequest;
import com.tandem.interest_service.service.model.response.UserInterestResponse;

import java.util.List;
import java.util.UUID;

public interface UserInterestService {
    List<UserInterestResponse> addUserInterest(List<UserInterestRequest> request); // Добавить интерес пользователю
    void removeUserInterest(UserInterestRequest request); // Удалить интерес по ID
    List<UserInterestResponse> getUserInterests(UUID userId); // Получить все интересы пользователя
}