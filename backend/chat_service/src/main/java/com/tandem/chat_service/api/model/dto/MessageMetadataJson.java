package com.tandem.chat_service.api.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageMetadataJson {

    // Для типа FILE
    @JsonProperty("fileId")
    private String fileId;

    @JsonProperty("fileUrl")
    private String fileUrl;

    @JsonProperty("fileName")
    private String fileName;

    @JsonProperty("fileType")
    private String fileType;

    // Для типа LINK
    @JsonProperty("linkUrl")
    private String linkUrl;

    // Для типа CODE
    @JsonProperty("code")
    private String code;

    @JsonProperty("language")
    private String language;

    @JsonProperty("sourceLink")
    private String sourceLink;

    // Для типа EMOJI
    @JsonProperty("emoji")
    private String emoji;

    // Для типа STICKER
    @JsonProperty("stickerId")
    private String stickerId;

    @JsonProperty("stickerUrl")
    private String stickerUrl;
}