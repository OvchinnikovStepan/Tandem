package com.tandem.interest_service.api;

import com.tandem.interest_service.api.model.TagCreateRequestJson;
import com.tandem.interest_service.api.model.TagResponseJson;
import com.tandem.interest_service.api.model.TagUpdateRequestJson;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("/api/interests")
public interface TagApi {

    @Operation(
            summary = "Получить все теги"
    )
    @GetMapping("/tags")
    ResponseEntity<List<TagResponseJson>> getAllTags();

    @Operation(
            summary = "Получить дефолтные теги"
    )
    @GetMapping("/tags/default")
    ResponseEntity<List<TagResponseJson>> getDefaultTags();


    @Operation(
            summary = "Создать новый тег"
    )
    @PostMapping("/tag")
    ResponseEntity<TagResponseJson> createTag(
            @Valid @RequestBody TagCreateRequestJson request
    );

    @Operation(
            summary = "Обновить тег"
    )
    @PutMapping("/tag/{id}")
    ResponseEntity<TagResponseJson> updateTag(
            @PathVariable UUID id,

            @Valid @RequestBody TagUpdateRequestJson request
    );

    @Operation(
            summary = "Удалить тег"
    )
    @DeleteMapping("/tag/{id}")
    ResponseEntity<String> deleteTag(
            @PathVariable UUID id
    );

    @Operation(
            summary = "Получить тег по ID"
    )
    @GetMapping("/tag/{id}")
    ResponseEntity<TagResponseJson> getTag(
            @PathVariable UUID id
    );

    @Operation(
            summary = "Поиск тегов по началу названия"
    )
    @GetMapping("/tags/search")
    ResponseEntity<List<TagResponseJson>> searchTags(
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "10") int limit
    );
}