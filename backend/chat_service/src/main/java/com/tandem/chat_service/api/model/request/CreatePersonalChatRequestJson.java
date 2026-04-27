package com.tandem.chat_service.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreatePersonalChatRequestJson {

    @NotNull(message = "Target user ID cannot be null")
    @JsonProperty("targetUserId")
    private UUID targetUserId;
}
