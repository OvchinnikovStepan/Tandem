import { useState } from "react";

import { SettingsForm, type SettingsData, type Session } from "@/modules/SettingsContent";

const initialSettings: SettingsData = {
    privacy: {
        showPhoneNumber: false,
        showEmail: false,
        showBirthday: false,
        showCity: true,
        showPlaceOfWork: true,
        showJobTitle: true,
        showPersonalInterests: true,
    },
    notifications: {
        push: true,
        email: false,
    },
    account: {
        email: "user@example.com",
        phone: "+7 999 123 45 67",
    },
};

const mockSessions: Session[] = [
    {
        sessionId: "sess-1",
        device: "Windows 11",
        browser: "Chrome 124",
        ip: "95.165.12.44",
        lastActivity: "Сейчас",
        isCurrent: true,
    },
    {
        sessionId: "sess-2",
        device: "iPhone 15",
        browser: "Safari 17",
        ip: "95.165.12.44",
        lastActivity: "2 часа назад",
        isCurrent: false,
    },
    {
        sessionId: "sess-3",
        device: "macOS Sonoma",
        browser: "Firefox 125",
        ip: "188.43.7.211",
        lastActivity: "Вчера в 21:34",
        isCurrent: false,
    },
];

export default function Settings() {
    const [settings, setSettings] = useState<SettingsData>(initialSettings);
    const [sessions, setSessions] = useState<Session[]>(mockSessions);

    return (
        <div className="min-h-screen w-screen bg-landing-bg flex justify-center overflow-y-auto">
            <main
                className="flex justify-center w-full"
                style={{ padding: "32px 16px" }}
            >
                <SettingsForm
                    settings={settings}
                    sessions={sessions}
                    onSave={(updated) => {
                        setSettings(updated);
                        console.log("Saved settings:", updated);
                    }}
                    onChangePassword={(oldPassword, newPassword) =>
                        console.log("Change password:", { oldPassword, newPassword })
                    }
                    onChangeEmail={(newEmail, password) =>
                        console.log("Change email:", { newEmail, password })
                    }
                    onChangePhone={(newPhone) =>
                        console.log("Change phone:", newPhone)
                    }
                    onTerminateSession={(sessionId) => {
                        setSessions((prev) => prev.filter((s) => s.sessionId !== sessionId));
                        console.log("Terminate session:", sessionId);
                    }}
                    onTerminateAllSessions={() => {
                        setSessions((prev) => prev.filter((s) => s.isCurrent));
                        console.log("Terminate all sessions");
                    }}
                    onDeleteAccount={() => console.log("Delete account")}
                />
            </main>
        </div>
    );
}
