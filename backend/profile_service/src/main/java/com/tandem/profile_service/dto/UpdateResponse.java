package com.tandem.profile_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateResponse {
    private boolean updated;
    private ProfileResponse profile;

    public static UpdateResponse success(ProfileResponse profile) {
        return new UpdateResponse(true, profile);
    }

    public static UpdateResponse failure() {
        return new UpdateResponse(false, null);
    }
}