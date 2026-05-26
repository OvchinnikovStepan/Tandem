import { useSetAtom } from "jotai";
import { useMutation } from "@tanstack/react-query";

import {
    forgotPasswordRequest,
    loginRequest,
    logoutRequest,
    meRequest,
    registerRequest,
    resetPasswordRequest,
} from "@/modules/Auth/api/authApi";
import { authAtom } from "@/modules/Auth/atoms/authAtom";
import {
    clearAccessToken,
    setAccessToken,
} from "@/modules/Auth/lib/tokenStorage";

export function useAuthActions() {
    const setAuth = useSetAtom(authAtom);

    const loginMutation = useMutation({
        mutationFn: async ({
            email,
            password,
        }: {
            email: string;
            password: string;
        }) => {
            const response = await loginRequest({ email, password });
            setAccessToken(response.accessToken);
            const user = await meRequest();
            return { response, user };
        },
    });

    const registerMutation = useMutation({
        mutationFn: async ({
            email,
            password,
        }: {
            email: string;
            password: string;
        }) => {
            const response = await registerRequest({ email, password });
            setAccessToken(response.accessToken);
            const user = await meRequest();
            return { response, user };
        },
    });

    const forgotPasswordMutation = useMutation({
        mutationFn: async (email: string) => forgotPasswordRequest({ email }),
    });

    const resetPasswordMutation = useMutation({
        mutationFn: async ({
            email,
            code,
            password,
        }: {
            email: string;
            code: string;
            password: string;
        }) => resetPasswordRequest({ email, code, password }),
    });

    const logoutMutation = useMutation({
        mutationFn: logoutRequest,
    });

    const login = async (email: string, password: string) => {
        const { response, user } = await loginMutation.mutateAsync({
            email,
            password,
        });
        setAuth({
            status: "ready",
            accessToken: response.accessToken,
            isAuthenticated: true,
            user,
        });
    };

    const register = async (email: string, password: string) => {
        const { response, user } = await registerMutation.mutateAsync({
            email,
            password,
        });
        setAuth({
            status: "ready",
            accessToken: response.accessToken,
            isAuthenticated: true,
            user,
        });
    };

    const logout = async () => {
        try {
            await logoutMutation.mutateAsync();
        } catch {
            // Best-effort: local session must be cleaned regardless of API result.
        }
        clearAccessToken();
        setAuth({
            status: "ready",
            accessToken: null,
            isAuthenticated: false,
            user: null,
        });
    };

    const forgotPassword = async (email: string) => {
        await forgotPasswordMutation.mutateAsync(email);
    };

    const resetPassword = async (
        email: string,
        code: string,
        password: string,
    ) => {
        await resetPasswordMutation.mutateAsync({ email, code, password });
    };

    return {
        login,
        register,
        logout,
        forgotPassword,
        resetPassword,
    };
}
