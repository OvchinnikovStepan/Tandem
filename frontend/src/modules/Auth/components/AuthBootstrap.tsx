import type { ReactNode } from "react";
import { useAtomValue } from "jotai";

import { authAtom } from "@/modules/Auth/atoms/authAtom";
import { useAuthBootstrap } from "@/modules/Auth/hooks/useAuthBootstrap";

type AuthBootstrapProps = {
    children: ReactNode;
};

export function AuthBootstrap({ children }: AuthBootstrapProps) {
    useAuthBootstrap();
    const auth = useAtomValue(authAtom);

    if (auth.status === "loading") {
        return null;
    }

    return <>{children}</>;
}
