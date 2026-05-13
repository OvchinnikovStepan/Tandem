package com.tandem.chat_service.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tandem.chat_service.api.model.dto.MessageMetadataJson;
import com.tandem.chat_service.dao.enums.MessageType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequestJson {

    @JsonProperty("content")
    private String content;

    @NotNull(message = "Message type is required")
    @JsonProperty("type")
    private MessageType type;

    @JsonProperty("metadata")
    private MessageMetadataJson metadata;
}