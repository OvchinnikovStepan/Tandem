package com.tandem.interest_service.service.model.request;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class TagRequest {
    private String name;
    private String imageUrl;
}
