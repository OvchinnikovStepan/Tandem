package com.tandem.chat_service.service.model.response;

import lombok.Builder;
import lombok.Getter;
import java.util.List;

@Getter
@Builder
public class PaginatedMessagesResponse {
    private List<MessageResponse> messages;
    private boolean hasMore;
}