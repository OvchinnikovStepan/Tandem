import * as React from "react";
import { cn } from "@/lib/utils";

interface LinkButtonProps
    extends React.ButtonHTMLAttributes<HTMLButtonElement> {
    variant?: "primary" | "secondary";
}

function LinkButton({
    variant = "primary",
    className,
    children,
    ...props
}: LinkButtonProps) {
    return (
        <button
            type="button"
            className={cn(
                "font-medium transition-colors",
                variant === "primary" && "text-blue-600 hover:text-blue-700",
                variant === "secondary" && "text-gray-600 hover:text-gray-700",
                className,
            )}
            {...props}
        >
            {children}
        </button>
    );
}

export { LinkButton };
