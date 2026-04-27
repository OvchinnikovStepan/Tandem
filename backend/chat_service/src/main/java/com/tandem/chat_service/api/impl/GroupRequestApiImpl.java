package com.tandem.chat_service.api.impl;

import com.tandem.chat_service.api.GroupRequestApi;
import com.tandem.chat_service.api.mapper.GroupRequestApiMapper;
import com.tandem.chat_service.api.model.request.CreateJoinGroupRequestJson;
import com.tandem.chat_service.api.model.response.JoinGroupResponseJson;
import com.tandem.chat_service.service.GroupRequestService;
import com.tandem.chat_service.service.model.response.GroupRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class GroupRequestApiImpl implements GroupRequestApi {

    private final GroupRequestService requestService;

    @Override
    public ResponseEntity<JoinGroupResponseJson> requestToJoin(UUID groupId, CreateJoinGroupRequestJson request) {
        UUID currentUserId = getCurrentUserId();
        String message = request != null ? request.getMessage() : null;

        GroupRequestDto created = requestService.createRequest(groupId, currentUserId, message);
        return ResponseEntity.status(HttpStatus.CREATED).body(GroupRequestApiMapper.toJson(created));
    }

    @Override
    public ResponseEntity<String> approveRequest(UUID requestId) {
        UUID currentUserId = getCurrentUserId();
        requestService.approveRequest(requestId, currentUserId);
        return ResponseEntity.ok("Заявка одобрена, пользователь добавлен в чат");
    }

    @Override
    public ResponseEntity<String> rejectRequest(UUID requestId) {
        UUID currentUserId = getCurrentUserId();
        requestService.rejectRequest(requestId, currentUserId);
        return ResponseEntity.ok("Заявка отклонена");
    }

    @Override
    public ResponseEntity<String> cancelRequest(UUID requestId) {
        UUID currentUserId = getCurrentUserId();
        requestService.cancelRequest(requestId, currentUserId);
        return ResponseEntity.ok("Заявка отменена");
    }

    @Override
    public ResponseEntity<List<JoinGroupResponseJson>> getPendingRequests(UUID groupId) {
        UUID currentUserId = getCurrentUserId();
        List<GroupRequestDto> requests = requestService.getPendingRequestsForGroup(groupId, currentUserId);

        List<JoinGroupResponseJson> response = requests.stream()
                .map(GroupRequestApiMapper::toJson)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<List<JoinGroupResponseJson>> getMyRequests() {
        UUID currentUserId = getCurrentUserId();
        List<GroupRequestDto> requests = requestService.getMyRequests(currentUserId);

        List<JoinGroupResponseJson> response = requests.stream()
                .map(GroupRequestApiMapper::toJson)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    private UUID getCurrentUserId() {
        // Для тестирования (будет заменено на извлечение токена через Security)
        return UUID.fromString("123e4567-e89b-12d3-a456-426614174002");
    }
}