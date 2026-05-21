package com.tandem.interest_service.api;

import com.tandem.interest_service.api.model.response.GroupSearchResponseJson;
import com.tandem.interest_service.api.model.response.UserSearchResponseJson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/api/interests")
public interface DirectorySearchApi {

    @Operation(summary = "Поиск пользователей по фрагменту отображаемого имени")
    @GetMapping("/users/search")
    ResponseEntity<List<UserSearchResponseJson>> searchUsers(
            @Parameter(description = "Подстрока имени (без учёта регистра)", example = "ann")
            @RequestParam(required = false) String search,

            @Parameter(description = "Максимум результатов", example = "10")
            @RequestParam(defaultValue = "10") int limit
    );

    @Operation(summary = "Поиск групп по фрагменту названия")
    @GetMapping("/groups/search")
    ResponseEntity<List<GroupSearchResponseJson>> searchGroups(
            @Parameter(description = "Подстрока названия группы (без учёта регистра)", example = "book")
            @RequestParam(required = false) String search,

            @Parameter(description = "Максимум результатов", example = "10")
            @RequestParam(defaultValue = "10") int limit
    );
}
