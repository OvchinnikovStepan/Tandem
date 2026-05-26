import { atom } from "jotai";
import type { AuthUser } from "@/modules/Auth/api/authApi";

export type AuthStatus = "loading" | "ready";

export type AuthState = {
    status: AuthStatus;
    accessToken: string | null;
    isAuthenticated: boolean;
    user: AuthUser | null;
};

export const authAtom = atom<AuthState>(
    {
    status: "loading",
    accessToken: null,
    isAuthenticated: false,
    user: null,
});
