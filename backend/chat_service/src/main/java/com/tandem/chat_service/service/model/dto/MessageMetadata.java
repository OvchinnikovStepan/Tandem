package com.tandem.chat_service.service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageMetadata {
    // Поля для FILE
    private String fileId;
    private String fileUrl;
    private String fileName;
    private String fileType;

    // Поля для LINK
    private String linkUrl;

    // Поля для CODE
    private String code;
    private String language;
    private String sourceLink;

    // Поля для EMOJI
    private String emoji;

    // Поля для STICKER
    private String stickerId;
    private String stickerUrl;
}