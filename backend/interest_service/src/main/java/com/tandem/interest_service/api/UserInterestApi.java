package com.tandem.interest_service.api;

import com.tandem.interest_service.api.model.request.UserInterestCreateRequestJson;
import com.tandem.interest_service.api.model.request.UserInterestDeleteRequestJson;
import com.tandem.interest_service.api.model.response.UserInterestResponseJson;
import io.swagger.v3.oas.annotations.Operation;
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
}
