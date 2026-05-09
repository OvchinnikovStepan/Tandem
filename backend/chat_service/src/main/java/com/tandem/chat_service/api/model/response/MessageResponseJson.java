package com.tandem.chat_service.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.tandem.chat_service.api.model.dto.MessageMetadataJson;
import com.tandem.chat_service.dao.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponseJson {

    @JsonProperty("messageId")
    private UUID messageId;

    @JsonProperty("chatId")
    private UUID chatId;

    @JsonProperty("senderId")
    private UUID senderId;

    @JsonProperty("senderName")
    private String senderName;

    @JsonProperty("content")
    private String content;

    @JsonProperty("type")
    private MessageType type;

    @JsonProperty("metadata")
    private MessageMetadataJson metadata;

    @JsonProperty("sentAt")
    private LocalDateTime sentAt;

    @JsonProperty("editedAt")
    private LocalDateTime editedAt;
}