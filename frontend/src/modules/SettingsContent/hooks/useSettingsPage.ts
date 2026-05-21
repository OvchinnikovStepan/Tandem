import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";

import {
    getPrivacySettings,
    updatePrivacySettings,
    getProfileMe,
    type PrivacySettings,
} from "@/api/profile";
import {
    getAuthMe,
    getSessions,
    terminateSession,
    changePassword,
    changeEmail,
    changePhone,
    deleteAccount,
} from "@/api/authAccount";
import {
    getNotificationPreferences,
    updateNotificationPreferences,
} from "@/api/notificationPreferences";
import type { SettingsData, Session } from "../components/SettingsForm/SettingsForm";

const DEFAULT_PRIVACY: PrivacySettings = {
    showPhoneNumber: false,
    showEmail: false,
    showBirthday: false,
    showCity: false,
    showPlaceOfWork: false,
    showJobTitle: false,
    showPersonalInterests: false,
};

export function useSettingsPage() {
    const queryClient = useQueryClient();

    const privacyQuery = useQuery({
        queryKey: ["settings", "privacy"],
        queryFn: getPrivacySettings,
    });

    const notifQuery = useQuery({
        queryKey: ["settings", "notifications"],
        queryFn: getNotificationPreferences,
    });

    const authMeQuery = useQuery({
        queryKey: ["settings", "authMe"],
        queryFn: getAuthMe,
    });

    const profileMeQuery = useQuery({
        queryKey: ["settings", "profileMe"],
        queryFn: getProfileMe,
    });

    const sessionsQuery = useQuery({
        queryKey: ["settings", "sessions"],
        queryFn: getSessions,
    });

    const savePrivacyMutation = useMutation({
        mutationFn: updatePrivacySettings,
        onSuccess: () =>
            queryClient.invalidateQueries({ queryKey: ["settings", "privacy"] }),
    });

    const saveNotifMutation = useMutation({
        mutationFn: updateNotificationPreferences,
        onSuccess: () =>
            queryClient.invalidateQueries({ queryKey: ["settings", "notifications"] }),
    });

    const terminateSessionMutation = useMutation({
        mutationFn: terminateSession,
        onSuccess: () =>
            queryClient.invalidateQueries({ queryKey: ["settings", "sessions"] }),
    });

    const settings: SettingsData = {
        privacy: privacyQuery.data?.privacySettings ?? DEFAULT_PRIVACY,
        notifications: {
            push: notifQuery.data?.preferences?.channels?.push ?? false,
            email: notifQuery.data?.preferences?.channels?.email ?? false,
        },
        account: {
            email: authMeQuery.data?.user?.email ?? "",
            phone: profileMeQuery.data?.phoneNumber ?? "",
        },
    };

    const sessions: Session[] = sessionsQuery.data?.sessions ?? [];

    const isLoading =
        privacyQuery.isLoading ||
        notifQuery.isLoading ||
        authMeQuery.isLoading ||
        sessionsQuery.isLoading;

    async function handleSave(updated: SettingsData) {
        await Promise.all([
            savePrivacyMutation.mutateAsync(updated.privacy),
            saveNotifMutation.mutateAsync(updated.notifications),
        ]);
    }

    async function handleTerminateAllSessions() {
        const nonCurrent = sessions.filter((s) => !s.isCurrent);
        await Promise.all(nonCurrent.map((s) => terminateSession(s.sessionId)));
        queryClient.invalidateQueries({ queryKey: ["settings", "sessions"] });
    }

    return {
        settings,
        sessions,
        isLoading,
        onSave: handleSave,
        onChangePassword: (oldPassword: string, newPassword: string) =>
            changePassword(oldPassword, newPassword),
        onChangeEmail: (newEmail: string, password: string) =>
            changeEmail(newEmail, password),
        onChangePhone: (newPhone: string) => changePhone(newPhone),
        onTerminateSession: (sessionId: string) =>
            terminateSessionMutation.mutate(sessionId),
        onTerminateAllSessions: handleTerminateAllSessions,
        onDeleteAccount: () => deleteAccount(),
    };
}
