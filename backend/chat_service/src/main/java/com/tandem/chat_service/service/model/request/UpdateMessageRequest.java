package com.tandem.chat_service.service.model.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateMessageRequest {
    private String content;
}