import type { CSSProperties } from "react";
import { cn } from "@/lib/utils";
import { getUserInitials } from "@/lib/userInitials";

type UserAvatarSize = "sm" | "md" | "lg" | "xl";

const sizeStyles: Record<UserAvatarSize, CSSProperties> = {
    sm: { width: "32px", height: "32px", fontSize: "11px" },
    md: { width: "40px", height: "40px", fontSize: "12px" },
    lg: { width: "44px", height: "44px", fontSize: "13px" },
    xl: { width: "150px", height: "150px", fontSize: "32px" },
};

type UserAvatarProps = {
    name: string;
    size?: UserAvatarSize;
    className?: string;
};

export function UserAvatar({ name, size = "md", className }: UserAvatarProps) {
    return (
        <div
            className={cn(
                "flex shrink-0 select-none items-center justify-center rounded-full bg-accent-gray font-roboto font-medium text-heading-black",
                className,
            )}
            style={sizeStyles[size]}
            aria-hidden
        >
            {getUserInitials(name)}
        </div>
    );
}
