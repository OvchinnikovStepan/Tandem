import { apiRequest } from "./client";

export interface PrivacySettings {
    showPhoneNumber: boolean;
    showEmail: boolean;
    showBirthday: boolean;
    showCity: boolean;
    showPlaceOfWork: boolean;
    showJobTitle: boolean;
    showPersonalInterests: boolean;
}

export interface ProfileMe {
    userId: string;
    name: string;
    surname: string;
    phoneNumber?: string;
}

export function getPrivacySettings() {
    return apiRequest<{ privacySettings: PrivacySettings }>("/profile/me/privacy", {
        auth: true,
    });
}

export function updatePrivacySettings(settings: PrivacySettings) {
    return apiRequest<{ updated: boolean }>("/profile/me/privacy", {
        method: "PUT",
        body: settings,
        auth: true,
    });
}

export function getProfileMe() {
    return apiRequest<ProfileMe>("/profile/me", { auth: true });
}
