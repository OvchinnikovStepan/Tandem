import { useEffect, useState } from "react";
import { Controller, FormProvider, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";

import Button from "@/ui/Button";
import { Input } from "@/ui/Input";
import { Label } from "@/ui/Label";
import { Separator } from "@/ui/Separator";
import { Switch } from "@/ui/Switch";

export interface Session {
    sessionId: string;
    device: string;
    browser: string;
    ip: string;
    lastActivity: string;
    isCurrent: boolean;
}

export interface SettingsData {
    privacy: {
        showPhoneNumber: boolean;
        showEmail: boolean;
        showBirthday: boolean;
        showCity: boolean;
        showPlaceOfWork: boolean;
        showJobTitle: boolean;
        showPersonalInterests: boolean;
    };
    notifications: {
        push: boolean;
        email: boolean;
    };
    account: {
        email: string;
        phone: string;
    };
}

interface SettingsFormProps {
    settings: SettingsData;
    sessions?: Session[];
    onSave: (settings: SettingsData) => void;
    onChangePassword?: (oldPassword: string, newPassword: string) => void;
    onChangeEmail?: (newEmail: string, password: string) => void;
    onChangePhone?: (newPhone: string) => void;
    onTerminateSession?: (sessionId: string) => void;
    onTerminateAllSessions?: () => void;
    onDeleteAccount?: () => void;
}

export default function SettingsForm({
    settings,
    sessions = [],
    onSave,
    onChangePassword,
    onChangeEmail,
    onChangePhone,
    onTerminateSession,
    onTerminateAllSessions,
    onDeleteAccount,
}: SettingsFormProps) {
    const { t } = useTranslation();
    const methods = useForm<SettingsData>({ defaultValues: settings });

    useEffect(() => {
        methods.reset(settings);
    }, [methods, settings]);

    const handleSubmit = methods.handleSubmit((data) => onSave(data));

    return (
        <FormProvider {...methods}>
            <div
                className="bg-white overflow-hidden w-full flex flex-col"
                style={{
                    boxShadow: "0px 2px 8px rgba(0, 0, 0, 0.25)",
                    maxWidth: "975px",
                    borderRadius: "24px",
                }}
            >
                {/* Title */}
                <div
                    className="flex justify-center"
                    style={{ paddingTop: "32px", paddingBottom: "24px" }}
                >
                    <h1 className="text-4xl font-bold text-black">
                        {t("settings.title")}
                    </h1>
                </div>

                <div className="px-5">
                    <Separator className="bg-accent-gray" />
                </div>

                {/* Privacy section */}
                <SettingsSection title={t("settings.privacy.title")}>
                    <SwitchRow
                        label={t("settings.privacy.showPhoneNumber")}
                        name="privacy.showPhoneNumber"
                    />
                    <SwitchRow
                        label={t("settings.privacy.showEmail")}
                        name="privacy.showEmail"
                    />
                    <SwitchRow
                        label={t("settings.privacy.showBirthday")}
                        name="privacy.showBirthday"
                    />
                    <SwitchRow
                        label={t("settings.privacy.showCity")}
                        name="privacy.showCity"
                    />
                    <SwitchRow
                        label={t("settings.privacy.showPlaceOfWork")}
                        name="privacy.showPlaceOfWork"
                    />
                    <SwitchRow
                        label={t("settings.privacy.showJobTitle")}
                        name="privacy.showJobTitle"
                    />
                    <SwitchRow
                        label={t("settings.privacy.showPersonalInterests")}
                        name="privacy.showPersonalInterests"
                    />
                </SettingsSection>

                <div className="px-5">
                    <Separator className="bg-accent-gray" />
                </div>

                {/* Notifications section */}
                <SettingsSection title={t("settings.notifications.title")}>
                    <SwitchRow
                        label={t("settings.notifications.push")}
                        name="notifications.push"
                    />
                    <SwitchRow
                        label={t("settings.notifications.email")}
                        name="notifications.email"
                    />
                </SettingsSection>

                <div className="px-5">
                    <Separator className="bg-accent-gray" />
                </div>

                {/* Account section */}
                <SettingsSection title={t("settings.account.title")}>
                    <ChangeEmailBlock
                        currentEmail={settings.account.email}
                        onSubmit={onChangeEmail}
                    />
                    <ChangePhoneBlock
                        currentPhone={settings.account.phone}
                        onSubmit={onChangePhone}
                    />
                    <ChangePasswordBlock onSubmit={onChangePassword} />
                    <DeleteAccountBlock onConfirm={onDeleteAccount} />
                </SettingsSection>

                <div className="px-5">
                    <Separator className="bg-accent-gray" />
                </div>

                {/* Sessions section */}
                <SettingsSection title={t("settings.sessions.title")}>
                    <SessionsBlock
                        sessions={sessions}
                        onTerminate={onTerminateSession}
                        onTerminateAll={onTerminateAllSessions}
                    />
                </SettingsSection>

                <div className="px-5">
                    <Separator className="bg-accent-gray" />
                </div>

                {/* Save button */}
                <div
                    className="flex justify-center"
                    style={{ padding: "24px 0" }}
                >
                    <Button
                        variant="action"
                        size="custom"
                        onClick={handleSubmit}
                        className="w-[140px] h-[45px] rounded-lg text-[15px] font-medium"
                    >
                        {t("settings.save")}
                    </Button>
                </div>
            </div>
        </FormProvider>
    );
}

function SettingsSection({
    title,
    children,
}: {
    title: string;
    children: React.ReactNode;
}) {
    return (
        <div
            className="flex flex-col items-center"
            style={{ padding: "32px 20px", gap: "20px" }}
        >
            <h2 className="text-2xl font-bold text-black w-full max-w-[560px] text-left">
                {title}
            </h2>
            <div
                className="flex flex-col w-full"
                style={{ maxWidth: "560px", gap: "18px" }}
            >
                {children}
            </div>
        </div>
    );
}

function Row({
    label,
    children,
}: {
    label: string;
    children: React.ReactNode;
}) {
    return (
        <div className="flex items-center justify-between gap-4 w-full">
            <Label className="flex-1">{label}</Label>
            <div className="shrink-0">{children}</div>
        </div>
    );
}

function SwitchRow({
    label,
    name,
}: {
    label: string;
    name:
        | `privacy.${"showPhoneNumber" | "showEmail" | "showBirthday" | "showCity" | "showPlaceOfWork" | "showJobTitle" | "showPersonalInterests"}`
        | `notifications.${"push" | "email"}`;
}) {
    return (
        <Row label={label}>
            <Controller
                name={name}
                render={({ field }) => (
                    <Switch
                        checked={field.value as boolean}
                        onCheckedChange={field.onChange}
                    />
                )}
            />
        </Row>
    );
}

function ChangeEmailBlock({
    currentEmail,
    onSubmit,
}: {
    currentEmail: string;
    onSubmit?: (newEmail: string, password: string) => void;
}) {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [newEmail, setNewEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState<string | null>(null);

    const handleConfirm = () => {
        if (!newEmail || !password) {
            setError(t("settings.account.errors.fieldRequired"));
            return;
        }
        setError(null);
        onSubmit?.(newEmail, password);
        setNewEmail("");
        setPassword("");
        setOpen(false);
    };

    return (
        <div className="flex flex-col gap-3 w-full">
            <Row label={t("settings.account.email")}>
                <div className="flex items-center gap-2 w-[326px] justify-between">
                    <span className="text-sm text-gray-600 truncate">{currentEmail}</span>
                    <Button
                        type="button"
                        variant="secondary"
                        size="sm"
                        onClick={() => setOpen((v) => !v)}
                    >
                        {open ? t("settings.account.cancel") : t("settings.account.change")}
                    </Button>
                </div>
            </Row>
            {open && (
                <div className="flex flex-col gap-3 w-full bg-landing-bg rounded-default p-4">
                    <p className="text-xs text-gray-500">{t("settings.account.changeEmailNote")}</p>
                    <PasswordRow
                        label={t("settings.account.newEmail")}
                        value={newEmail}
                        onChange={setNewEmail}
                        type="email"
                    />
                    <PasswordRow
                        label={t("settings.account.currentPassword")}
                        value={password}
                        onChange={setPassword}
                        type="password"
                    />
                    {error && <p className="text-xs text-red-600">{error}</p>}
                    <div className="flex justify-end">
                        <Button type="button" variant="action" size="sm" onClick={handleConfirm}>
                            {t("settings.account.confirmChange")}
                        </Button>
                    </div>
                </div>
            )}
        </div>
    );
}

function ChangePhoneBlock({
    currentPhone,
    onSubmit,
}: {
    currentPhone: string;
    onSubmit?: (newPhone: string) => void;
}) {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [newPhone, setNewPhone] = useState("");
    const [error, setError] = useState<string | null>(null);

    const handleConfirm = () => {
        if (!newPhone) {
            setError(t("settings.account.errors.fieldRequired"));
            return;
        }
        setError(null);
        onSubmit?.(newPhone);
        setNewPhone("");
        setOpen(false);
    };

    return (
        <div className="flex flex-col gap-3 w-full">
            <Row label={t("settings.account.phone")}>
                <div className="flex items-center gap-2 w-[326px] justify-between">
                    <span className="text-sm text-gray-600 truncate">{currentPhone}</span>
                    <Button
                        type="button"
                        variant="secondary"
                        size="sm"
                        onClick={() => setOpen((v) => !v)}
                    >
                        {open ? t("settings.account.cancel") : t("settings.account.change")}
                    </Button>
                </div>
            </Row>
            {open && (
                <div className="flex flex-col gap-3 w-full bg-landing-bg rounded-default p-4">
                    <p className="text-xs text-gray-500">{t("settings.account.changePhoneNote")}</p>
                    <PasswordRow
                        label={t("settings.account.newPhone")}
                        value={newPhone}
                        onChange={setNewPhone}
                        type="tel"
                    />
                    {error && <p className="text-xs text-red-600">{error}</p>}
                    <div className="flex justify-end">
                        <Button type="button" variant="action" size="sm" onClick={handleConfirm}>
                            {t("settings.account.sendCode")}
                        </Button>
                    </div>
                </div>
            )}
        </div>
    );
}

function SessionsBlock({
    sessions,
    onTerminate,
    onTerminateAll,
}: {
    sessions: Session[];
    onTerminate?: (sessionId: string) => void;
    onTerminateAll?: () => void;
}) {
    const { t } = useTranslation();

    if (sessions.length === 0) {
        return <p className="text-sm text-gray-500">{t("settings.sessions.noSessions")}</p>;
    }

    return (
        <div className="flex flex-col gap-4 w-full">
            {sessions.map((session) => (
                <div
                    key={session.sessionId}
                    className="flex items-start justify-between gap-4 p-3 rounded-xl border border-accent-gray bg-landing-bg"
                >
                    <div className="flex flex-col gap-0.5 text-sm">
                        <span className="font-medium text-black">
                            {session.device} · {session.browser}
                            {session.isCurrent && (
                                <span className="ml-2 text-xs text-action-button-text font-normal">
                                    {t("settings.sessions.current")}
                                </span>
                            )}
                        </span>
                        <span className="text-gray-500">{session.ip}</span>
                        <span className="text-gray-400 text-xs">{session.lastActivity}</span>
                    </div>
                    {!session.isCurrent && (
                        <Button
                            type="button"
                            variant="outline"
                            size="sm"
                            onClick={() => onTerminate?.(session.sessionId)}
                        >
                            {t("settings.sessions.terminate")}
                        </Button>
                    )}
                </div>
            ))}
            <div className="flex justify-end">
                <Button
                    type="button"
                    variant="destructive"
                    size="sm"
                    onClick={onTerminateAll}
                >
                    {t("settings.sessions.terminateAll")}
                </Button>
            </div>
        </div>
    );
}

function ChangePasswordBlock({
    onSubmit,
}: {
    onSubmit?: (oldPassword: string, newPassword: string) => void;
}) {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);
    const [oldPassword, setOldPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [repeat, setRepeat] = useState("");
    const [error, setError] = useState<string | null>(null);

    const handleConfirm = () => {
        if (!oldPassword || !newPassword) {
            setError(t("settings.account.errors.passwordRequired"));
            return;
        }
        if (newPassword !== repeat) {
            setError(t("settings.account.errors.passwordMismatch"));
            return;
        }
        setError(null);
        onSubmit?.(oldPassword, newPassword);
        setOldPassword("");
        setNewPassword("");
        setRepeat("");
        setOpen(false);
    };

    return (
        <div className="flex flex-col gap-3 w-full">
            <Row label={t("settings.account.password")}>
                <Button
                    type="button"
                    variant="secondary"
                    size="sm"
                    className="w-[326px]"
                    onClick={() => setOpen((v) => !v)}
                >
                    {open
                        ? t("settings.account.cancel")
                        : t("settings.account.changePassword")}
                </Button>
            </Row>
            {open && (
                <div className="flex flex-col gap-3 w-full bg-landing-bg rounded-default p-4">
                    <PasswordRow
                        label={t("settings.account.oldPassword")}
                        value={oldPassword}
                        onChange={setOldPassword}
                    />
                    <PasswordRow
                        label={t("settings.account.newPassword")}
                        value={newPassword}
                        onChange={setNewPassword}
                    />
                    <PasswordRow
                        label={t("settings.account.repeatPassword")}
                        value={repeat}
                        onChange={setRepeat}
                    />
                    {error && (
                        <p className="text-xs text-red-600">{error}</p>
                    )}
                    <div className="flex justify-end">
                        <Button
                            type="button"
                            variant="action"
                            size="sm"
                            onClick={handleConfirm}
                        >
                            {t("settings.account.confirmChange")}
                        </Button>
                    </div>
                </div>
            )}
        </div>
    );
}

function PasswordRow({
    label,
    value,
    onChange,
    type = "password",
}: {
    label: string;
    value: string;
    onChange: (v: string) => void;
    type?: string;
}) {
    return (
        <div className="flex items-center justify-between gap-4 w-full">
            <Label className="flex-1">{label}</Label>
            <Input
                type={type}
                className="w-[326px]"
                value={value}
                onChange={(e) => onChange(e.target.value)}
            />
        </div>
    );
}

function DeleteAccountBlock({ onConfirm }: { onConfirm?: () => void }) {
    const { t } = useTranslation();
    const [open, setOpen] = useState(false);

    return (
        <div className="flex flex-col gap-3 w-full">
            <Row label={t("settings.account.danger")}>
                <Button
                    type="button"
                    variant="destructive"
                    size="sm"
                    className="w-[326px]"
                    onClick={() => setOpen((v) => !v)}
                >
                    {open
                        ? t("settings.account.cancel")
                        : t("settings.account.deleteAccount")}
                </Button>
            </Row>
            {open && (
                <div className="flex flex-col gap-3 w-full bg-red-50 border border-red-200 rounded-default p-4">
                    <p className="text-sm text-red-700">
                        {t("settings.account.deleteWarning")}
                    </p>
                    <div className="flex justify-end gap-2">
                        <Button
                            type="button"
                            variant="secondary"
                            size="sm"
                            onClick={() => setOpen(false)}
                        >
                            {t("settings.account.cancel")}
                        </Button>
                        <Button
                            type="button"
                            variant="destructive"
                            size="sm"
                            onClick={() => {
                                onConfirm?.();
                                setOpen(false);
                            }}
                        >
                            {t("settings.account.confirmDelete")}
                        </Button>
                    </div>
                </div>
            )}
        </div>
    );
}
