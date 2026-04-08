package com.tandem.interest_service.api;

import com.tandem.interest_service.api.model.response.UserMatchingResponseJson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/api/interests")
public interface MatchingApi {
    @Operation(
            summary = "Получить пользователей по интересам"
    )
    @GetMapping("/matching-users")
    ResponseEntity<List<UserMatchingResponseJson>> getMatchUsers(
            @Parameter(description = "Максимальное количество пользователей в результате", example = "10")
            @RequestParam(value = "limit", required = false) Integer limit,

            @Parameter(description = "Минимальное количество общих интересов", example = "2")
            @RequestParam(value = "minMatchCount", required = false) Integer minMatchCount
    );
}
