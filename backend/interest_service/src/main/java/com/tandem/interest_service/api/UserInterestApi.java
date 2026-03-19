package com.tandem.interest_service.api;

import com.tandem.interest_service.api.model.request.UserInterestCreateRequestJson;
import com.tandem.interest_service.api.model.request.UserInterestDeleteRequestJson;
import com.tandem.interest_service.api.model.response.UserInterestResponseJson;
import com.tandem.interest_service.api.model.response.UserMatchingResponseJson;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/interests")
public interface UserInterestApi {

    @Operation(
            summary = "Получить свои теги"
    )
    @GetMapping("/me")
    ResponseEntity<List<UserInterestResponseJson>> getMyTags(
    );

    @Operation(
            summary = "Добавить себе тег"
    )
    @PostMapping("/me")
    ResponseEntity<List<UserInterestResponseJson>> addMyTag(
            @Valid @RequestBody UserInterestCreateRequestJson request
    );

    @Operation(
            summary = "Удалить у себя тег"
    )
    @DeleteMapping("/me")
    ResponseEntity<String> deleteMyTag(
            @Valid @RequestBody UserInterestDeleteRequestJson request
    );

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
