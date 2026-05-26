import { useAtomValue } from "jotai";
import { Navigate, Outlet } from "react-router";

import { authAtom } from "@/modules/Auth/atoms/authAtom";

export function PublicOnlyRoute() {
    const auth = useAtomValue(authAtom);

    if (auth.status === "loading") {
        return null;
    }

    if (auth.isAuthenticated) {
        return (
            <Navigate
                to={
                    auth.user?.onboardingCompleted ? "/feed" : "/questionnaire"
                }
                replace
            />
        );
    }

    return <Outlet />;
}
