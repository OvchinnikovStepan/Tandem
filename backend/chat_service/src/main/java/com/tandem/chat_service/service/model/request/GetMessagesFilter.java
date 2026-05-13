package com.tandem.chat_service.service.model.request;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetMessagesFilter {
    private final UUID chatId;
    private final UUID requesterId;
    private final LocalDateTime before;
    private final LocalDateTime after;
    private final int limit;
}
