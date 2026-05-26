import { apiFetch } from "@/api/httpClient";

type LoginPayload = {
    email: string;
    password: string;
};

type RegisterPayload = {
    email: string;
    password: string;
};

type ForgotPasswordPayload = {
    email: string;
};

type ResetPasswordPayload = {
    email: string;
    code: string;
    password: string;
};

type AuthResponse = {
    accessToken: string;
};

export type AuthUser = {
    id: string;
    email: string;
    name?: string;
    onboardingCompleted: boolean;
    firstName?: string;
    lastName?: string;
    city?: string;
    birthDate?: string;
    gender?: string;
};

export type MockAuthMode = "authorized" | "unauthorized";

const AUTH_API_BASE = "/api/auth";
const USE_MOCK_AUTH = true;
const MOCK_AUTH_STORAGE_KEY = "tandem_mock_auth_state";
const MOCK_AUTH_USER_STORAGE_KEY = "tandem_mock_auth_user";
const MOCK_AUTH_DEFAULT_STATE: MockAuthMode = "authorized";
let MOCK_AUTH_STATE: MockAuthMode = MOCK_AUTH_DEFAULT_STATE;
const MOCK_AUTH_DEFAULT_USER: AuthUser = {
    id: "1",
    email: "azunyan@tandem.dev",
    name: "Azunyan U. Wu",
    onboardingCompleted: true,
    firstName: "U. Wu",
    lastName: "Azunyan",
    city: "Moscow",
    gender: "male",
};
let MOCK_AUTH_USER: AuthUser = MOCK_AUTH_DEFAULT_USER;

if (USE_MOCK_AUTH) {
    const persistedState = localStorage.getItem(MOCK_AUTH_STORAGE_KEY);
    if (persistedState === "authorized" || persistedState === "unauthorized") {
        MOCK_AUTH_STATE = persistedState;
    } else {
        localStorage.setItem(MOCK_AUTH_STORAGE_KEY, MOCK_AUTH_DEFAULT_STATE);
    }

    const persistedUser = localStorage.getItem(MOCK_AUTH_USER_STORAGE_KEY);
    if (persistedUser) {
        try {
            const parsedUser = JSON.parse(persistedUser) as Partial<AuthUser>;
            if (parsedUser.id && parsedUser.email) {
                MOCK_AUTH_USER = {
                    ...MOCK_AUTH_DEFAULT_USER,
                    ...parsedUser,
                };
            }
        } catch {
            localStorage.removeItem(MOCK_AUTH_USER_STORAGE_KEY);
        }
    } else {
        localStorage.setItem(
            MOCK_AUTH_USER_STORAGE_KEY,
            JSON.stringify(MOCK_AUTH_DEFAULT_USER),
        );
    }
}

function wait(ms = 200) {
    return new Promise((resolve) => {
        setTimeout(resolve, ms);
    });
}

export function isMockAuthEnabled() {
    return USE_MOCK_AUTH;
}

export function getMockAuthMode(): MockAuthMode {
    if (!USE_MOCK_AUTH) return "unauthorized";
    return MOCK_AUTH_STATE;
}

export function setMockAuthMode(mode: MockAuthMode) {
    MOCK_AUTH_STATE = mode;
    if (USE_MOCK_AUTH) {
        localStorage.setItem(MOCK_AUTH_STORAGE_KEY, mode);
    }
}

function persistMockUser(user: AuthUser) {
    MOCK_AUTH_USER = user;
    if (USE_MOCK_AUTH) {
        localStorage.setItem(MOCK_AUTH_USER_STORAGE_KEY, JSON.stringify(user));
    }
}

export function completeMockOnboarding() {
    if (!USE_MOCK_AUTH) return;

    persistMockUser({
        ...MOCK_AUTH_USER,
        onboardingCompleted: true,
    });
}

type MockProfileData = {
    firstName: string;
    lastName: string;
    city?: string;
    birthDate?: string;
    gender?: string;
};

export function updateMockAuthProfile(profile: MockProfileData) {
    if (!USE_MOCK_AUTH) return;

    persistMockUser({
        ...MOCK_AUTH_USER,
        ...profile,
        name: `${profile.lastName} ${profile.firstName}`.trim(),
    });
}

async function postJson<TResponse>(
    path: string,
    body: Record<string, unknown>,
): Promise<TResponse> {
    const response = await apiFetch(`${AUTH_API_BASE}${path}`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(body),
        retryOnUnauthorized: false,
    });

    if (!response.ok) {
        throw new Error("Ошибка авторизации");
    }

    return (await response.json()) as TResponse;
}

export async function loginRequest(
    payload: LoginPayload,
): Promise<AuthResponse> {
    if (USE_MOCK_AUTH) {
        await wait();
        if (!payload.email || !payload.password) {
            throw new Error("Ошибка авторизации");
        }
        setMockAuthMode("authorized");
        persistMockUser({
            ...MOCK_AUTH_USER,
            email: payload.email,
        });
        return { accessToken: "mock-access-token" };
    }

    return postJson<AuthResponse>("/login", payload);
}

export async function registerRequest(
    payload: RegisterPayload,
): Promise<AuthResponse> {
    if (USE_MOCK_AUTH) {
        await wait();
        if (!payload.email || !payload.password) {
            throw new Error("Ошибка регистрации");
        }
        setMockAuthMode("authorized");
        persistMockUser({
            id: "1",
            email: payload.email,
            name: payload.email.split("@")[0],
            onboardingCompleted: false,
            firstName: "",
            lastName: "",
            city: "",
            birthDate: "",
            gender: "",
        });
        return { accessToken: "mock-access-token" };
    }

    return postJson<AuthResponse>("/register", payload);
}

export async function forgotPasswordRequest(payload: ForgotPasswordPayload) {
    if (USE_MOCK_AUTH) {
        await wait();
        if (!payload.email) throw new Error("Ошибка отправки");
        return { ok: true };
    }

    return postJson<{ ok: true }>("/forgot-password", payload);
}

export async function resetPasswordRequest(payload: ResetPasswordPayload) {
    if (USE_MOCK_AUTH) {
        await wait();
        if (!payload.email || !payload.code || !payload.password) {
            throw new Error("Ошибка сброса");
        }
        return { ok: true };
    }

    return postJson<{ ok: true }>("/reset-password", payload);
}

export async function refreshRequest(): Promise<AuthResponse> {
    if (USE_MOCK_AUTH) {
        await wait();
        if (getMockAuthMode() !== "authorized") {
            throw new Error("Mock: пользователь не авторизован");
        }
        return { accessToken: "mock-access-token" };
    }

    // REAL IMPLEMENTATION (временно отключено до запуска бэкенда)
    // const response = await apiFetch(`${AUTH_API_BASE}/refresh`, {
    //     method: "POST",
    //     retryOnUnauthorized: false,
    // });
    // if (!response.ok) {
    //     throw new Error("Не удалось обновить сессию");
    // }
    // return (await response.json()) as AuthResponse;
    throw new Error("Refresh API disabled");
}

export async function logoutRequest() {
    if (USE_MOCK_AUTH) {
        await wait();
        setMockAuthMode("unauthorized");
        persistMockUser(MOCK_AUTH_DEFAULT_USER);
        return { ok: true };
    }

    // REAL IMPLEMENTATION (временно отключено до запуска бэкенда)
    // const response = await apiFetch(`${AUTH_API_BASE}/logout`, {
    //     method: "POST",
    //     retryOnUnauthorized: false,
    // });
    // if (!response.ok) {
    //     throw new Error("Не удалось завершить сессию");
    // }
    // return (await response.json()) as { ok: true };
    throw new Error("Logout API disabled");
}

export async function meRequest(): Promise<AuthUser> {
    if (USE_MOCK_AUTH) {
        await wait();
        if (getMockAuthMode() !== "authorized") {
            throw new Error("Mock: профиль недоступен");
        }
        return MOCK_AUTH_USER;
    }

    // REAL IMPLEMENTATION (временно отключено до запуска бэкенда)
    // const response = await apiFetch(`${AUTH_API_BASE}/me`, {
    //     method: "GET",
    //     auth: true,
    // });
    // if (!response.ok) {
    //     throw new Error("Не удалось получить профиль");
    // }
    // return (await response.json()) as AuthUser;
    throw new Error("Me API disabled");
}
