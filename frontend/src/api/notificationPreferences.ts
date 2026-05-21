import { apiRequest } from "./client";

export interface NotificationChannels {
    push: boolean;
    email: boolean;
}

export function getNotificationPreferences() {
    return apiRequest<{ preferences: { channels: NotificationChannels } }>(
        "/notifications/preferences",
        { auth: true },
    );
}

export function updateNotificationPreferences(channels: NotificationChannels) {
    return apiRequest<{ updated: boolean }>("/notifications/preferences", {
        method: "PUT",
        body: { channels },
        auth: true,
    });
}
