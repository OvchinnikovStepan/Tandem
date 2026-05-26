import { useAtomValue } from "jotai";
import { Navigate, Outlet, useLocation } from "react-router";

import { authAtom } from "@/modules/Auth/atoms/authAtom";

export function ProtectedRoute() {
    const auth = useAtomValue(authAtom);
    const location = useLocation();

    if (auth.status === "loading") {
        return null;
    }

    if (!auth.isAuthenticated) {
        return <Navigate to="/" replace />;
    }

    const isQuestionnaireRoute = location.pathname === "/questionnaire";
    const isOnboardingCompleted = auth.user?.onboardingCompleted ?? false;

    if (!isOnboardingCompleted && !isQuestionnaireRoute) {
        return <Navigate to="/questionnaire" replace />;
    }

    if (isOnboardingCompleted && isQuestionnaireRoute) {
        return <Navigate to="/feed" replace />;
    }

    return <Outlet />;
}
