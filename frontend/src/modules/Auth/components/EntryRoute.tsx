import { useAtomValue } from "jotai";
import { Navigate } from "react-router";
import type { ReactNode } from "react";

import { authAtom } from "@/modules/Auth/atoms/authAtom";

type EntryRouteProps = {
    unauthenticatedElement: ReactNode;
};

export function EntryRoute({ unauthenticatedElement }: EntryRouteProps) {
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

    return <>{unauthenticatedElement}</>;
}
