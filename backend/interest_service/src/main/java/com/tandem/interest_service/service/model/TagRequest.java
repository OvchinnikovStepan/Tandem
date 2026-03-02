package com.tandem.interest_service.service.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TagRequest {
    private String name;
    private String imageUrl;
}