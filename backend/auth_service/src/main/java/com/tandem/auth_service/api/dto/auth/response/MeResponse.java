package com.tandem.auth_service.api.dto.auth.response;

import com.tandem.auth_service.api.dto.UserDto;

public record MeResponse(
        UserDto user
) {}

