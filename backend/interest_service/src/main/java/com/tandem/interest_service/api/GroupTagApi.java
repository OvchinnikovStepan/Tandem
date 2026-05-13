package com.tandem.interest_service.api;

import com.tandem.interest_service.api.model.request.GroupInterestCreateRequestJson;
import com.tandem.interest_service.api.model.response.GroupInterestResponseJson;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/interests")
public interface GroupTagApi {

    @Operation(summary = "Получить теги группы")
    @GetMapping("/groups/{groupId}")
    ResponseEntity<List<GroupInterestResponseJson>> getGroupTags(
            @PathVariable UUID groupId
    );

    @Operation(summary = "Добавить теги группе")
    @PostMapping("/groups/{groupId}")
    ResponseEntity<List<GroupInterestResponseJson>> addGroupTags(
            @PathVariable UUID groupId,
            @Valid @RequestBody GroupInterestCreateRequestJson request
    );

    @Operation(summary = "Удалить тег из группы")
    @DeleteMapping("/groups/{groupId}/tags/{tagId}")
    ResponseEntity<Void> deleteGroupTag(
            @PathVariable UUID groupId,
            @PathVariable UUID tagId
    );
}