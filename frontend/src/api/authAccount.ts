import { apiRequest } from "./client";

export interface AuthUser {
    userId: string;
    email: string;
}

export interface ApiSession {
    sessionId: string;
    device: string;
    browser: string;
    ip: string;
    lastActivity: string;
    isCurrent: boolean;
}

export function getAuthMe() {
    return apiRequest<{ user: AuthUser }>("/auth/me", { auth: true });
}

export function getSessions() {
    return apiRequest<{ sessions: ApiSession[] }>("/auth/sessions", { auth: true });
}

export function terminateSession(sessionId: string) {
    return apiRequest<{ success: boolean }>(`/auth/sessions/${sessionId}`, {
        method: "DELETE",
        auth: true,
    });
}

// TODO: implement change password endpoint when documented in auth-service
export function changePassword(oldPassword: string, newPassword: string) {
    return apiRequest<{ success: boolean }>("/auth/password/change", {
        method: "POST",
        body: { oldPassword, newPassword },
        auth: true,
    });
}

// TODO: implement change email endpoint when documented in auth-service
export function changeEmail(newEmail: string, password: string) {
    return apiRequest<{ success: boolean }>("/auth/email/change", {
        method: "POST",
        body: { newEmail, password },
        auth: true,
    });
}

// TODO: implement change phone endpoint when documented in auth-service
export function changePhone(newPhone: string) {
    return apiRequest<{ verificationId: string }>("/auth/phone/change", {
        method: "POST",
        body: { phoneNumber: newPhone },
        auth: true,
    });
}

// TODO: implement delete account endpoint when documented in auth-service
export function deleteAccount() {
    return apiRequest<{ success: boolean }>("/auth/account", {
        method: "DELETE",
        auth: true,
    });
}
