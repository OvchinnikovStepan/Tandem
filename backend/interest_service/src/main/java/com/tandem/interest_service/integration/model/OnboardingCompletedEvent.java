package com.tandem.interest_service.integration.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OnboardingCompletedEvent {

    @JsonProperty("userId")
    private String userId;

    @JsonProperty("data")
    private Map<String, Object> data;

    @SuppressWarnings("unchecked")
    public List<String> getInterests() {
        if (data != null && data.containsKey("interests")) {
            Object interests = data.get("interests");
            if (interests instanceof List) {
                return (List<String>) interests;
            }
        }
        return List.of();
    }

    public UUID getUserIdAsUUID() {
        return UUID.fromString(userId);
    }

    /**
     * Username из {@code data.username} (единственное поддерживаемое поле для имени в каталоге).
     */
    public Optional<String> resolveUsername() {
        if (data == null) {
            return Optional.empty();
        }
        Object value = data.get("username");
        if (value == null) {
            return Optional.empty();
        }
        String s = String.valueOf(value).trim();
        return s.isEmpty() ? Optional.empty() : Optional.of(s);
    }
}
