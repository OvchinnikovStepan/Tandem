package com.tandem.profile_service.dto;

import lombok.Data;

@Data
public class PrivacySettingsDto {
    private boolean showPhoneNumber;
    private boolean showEmail;
    private boolean showCity;
    private boolean showPlaceOfWork;
    private boolean showJobTitle;
    private boolean showBirthday;
    private boolean showPersonalInterests;
}