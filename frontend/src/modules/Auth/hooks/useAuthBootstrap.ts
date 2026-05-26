import { useSetAtom } from "jotai";
import { useCallback, useEffect } from "react";
import { useQuery } from "@tanstack/react-query";

import { setAccessTokenGetter, setAuthRefreshHandler } from "@/api/httpClient";
import { meRequest, refreshRequest } from "@/modules/Auth/api/authApi";
import { authAtom } from "@/modules/Auth/atoms/authAtom";
import {
    clearAccessToken,
    getAccessToken,
    setAccessToken,
} from "@/modules/Auth/lib/tokenStorage";

export function useAuthBootstrap() {
    const setAuth = useSetAtom(authAtom);

    const refreshSession = useCallback(async () => {
        try {
            const refreshResponse = await refreshRequest();
            setAccessToken(refreshResponse.accessToken);
            setAuth((prev) => ({
                ...prev,
                status: "ready",
                accessToken: refreshResponse.accessToken,
                isAuthenticated: true,
            }));
            return true;
        } catch {
            clearAccessToken();
            setAuth({
                status: "ready",
                accessToken: null,
                isAuthenticated: false,
                user: null,
            });
            return false;
        }
    }, [setAuth]);

    useEffect(() => {
        setAccessTokenGetter(getAccessToken);
        setAuthRefreshHandler(refreshSession);

        return () => {
            setAuthRefreshHandler(null);
            setAccessTokenGetter(null);
        };
    }, [refreshSession]);

    const bootstrapQuery = useQuery({
        queryKey: ["auth", "bootstrap"],
        queryFn: async () => {
            const refreshed = await refreshSession();
            if (!refreshed) return null;
            return meRequest();
        },
        staleTime: 0,
        retry: false,
        refetchOnWindowFocus: false,
    });

    useEffect(() => {
        if (bootstrapQuery.isLoading) {
            setAuth((prev) => ({ ...prev, status: "loading" }));
            return;
        }

        if (bootstrapQuery.isError || !bootstrapQuery.data) {
            setAuth({
                status: "ready",
                accessToken: null,
                isAuthenticated: false,
                user: null,
            });
            return;
        }

        setAuth((prev) => ({
            ...prev,
            status: "ready",
            isAuthenticated: true,
            user: bootstrapQuery.data,
        }));
    }, [
        bootstrapQuery.data,
        bootstrapQuery.isError,
        bootstrapQuery.isLoading,
        setAuth,
    ]);
}
