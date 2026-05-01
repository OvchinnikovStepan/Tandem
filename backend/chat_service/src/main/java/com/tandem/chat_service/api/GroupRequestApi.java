package com.tandem.chat_service.api;

import com.tandem.chat_service.api.model.request.CreateJoinGroupRequestJson;
import com.tandem.chat_service.api.model.response.JoinGroupResponseJson;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/group-requests")
public interface GroupRequestApi {

    @Operation(summary = "Подать заявку на вступление в приватную группу")
    @PostMapping("/groups/{groupId}")
    ResponseEntity<JoinGroupResponseJson> requestToJoin(
            @PathVariable UUID groupId,
            @RequestBody(required = false) CreateJoinGroupRequestJson request
    );

    @Operation(summary = "Принять заявку (для владельцев)")
    @PutMapping("/{requestId}/approve")
    ResponseEntity<String> approveRequest(@PathVariable UUID requestId);

    @Operation(summary = "Отклонить заявку (для владельцев)")
    @PutMapping("/{requestId}/reject")
    ResponseEntity<String> rejectRequest(@PathVariable UUID requestId);

    @Operation(summary = "Отменить заявку (пользователь)")
    @PutMapping("/{requestId}/cancel")
    ResponseEntity<String> cancelRequest(@PathVariable UUID requestId);

    @Operation(summary = "Получить заявки на вступление в группу (для владельцев)")
    @GetMapping("/groups/{groupId}/pending")
    ResponseEntity<List<JoinGroupResponseJson>> getPendingRequests(@PathVariable UUID groupId);

    @Operation(summary = "Получить отправленные заявки (пользователь)")
    @GetMapping("/me")
    ResponseEntity<List<JoinGroupResponseJson>> getMyRequests();
}
