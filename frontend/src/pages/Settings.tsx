import { SettingsForm } from "@/modules/SettingsContent";
import { useSettingsPage } from "@/modules/SettingsContent/hooks/useSettingsPage";

export default function Settings() {
    const {
        settings,
        sessions,
        isLoading,
        onSave,
        onChangePassword,
        onChangeEmail,
        onChangePhone,
        onTerminateSession,
        onTerminateAllSessions,
        onDeleteAccount,
    } = useSettingsPage();

    if (isLoading) {
        return (
            <div className="min-h-screen w-screen bg-landing-bg flex items-center justify-center">
                <span className="text-heading-black font-roboto">Загрузка...</span>
            </div>
        );
    }

    return (
        <div className="min-h-screen w-screen bg-landing-bg flex justify-center overflow-y-auto">
            <main
                className="flex justify-center w-full"
                style={{ padding: "32px 16px" }}
            >
                <SettingsForm
                    settings={settings}
                    sessions={sessions}
                    onSave={onSave}
                    onChangePassword={onChangePassword}
                    onChangeEmail={onChangeEmail}
                    onChangePhone={onChangePhone}
                    onTerminateSession={onTerminateSession}
                    onTerminateAllSessions={onTerminateAllSessions}
                    onDeleteAccount={onDeleteAccount}
                />
            </main>
        </div>
    );
}
