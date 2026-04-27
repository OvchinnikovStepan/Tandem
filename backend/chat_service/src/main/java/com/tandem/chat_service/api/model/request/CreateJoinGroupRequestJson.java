package com.tandem.chat_service.api.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CreateJoinGroupRequestJson {
    @JsonProperty("message")
    private String message;
}
