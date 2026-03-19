package com.tandem.interest_service.api.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class TagUpdateRequestJson {

    @Size(min = 1, max = 100, message = "Name must be between 1 and 100 characters")
    @JsonProperty("name")
    private String name;

    @JsonProperty("imageUrl")
    private String imageUrl;
}
