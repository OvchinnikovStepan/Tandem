package com.tandem.chat_service.api.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaginatedMessagesResponseJson {

    @JsonProperty("messages")
    private List<MessageResponseJson> messages;

    @JsonProperty("hasMore")
    private boolean hasMore;
}
