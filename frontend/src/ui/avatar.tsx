import * as React from "react";
import { cn } from "@/lib/utils";

export interface AvatarProps extends React.HTMLAttributes<HTMLDivElement> {
    src?: string;
    alt?: string;
    size?: "xs" | "sm" | "md" | "lg" | "xl";
    showOnlineIndicator?: boolean;
    isOnline?: boolean;
    fallback?: React.ReactNode;
}

const sizeStyles = {
    xs: { width: "24px", height: "24px" },
    sm: { width: "32px", height: "32px" },
    md: { width: "48px", height: "48px" },
    lg: { width: "64px", height: "64px" },
    xl: { width: "100px", height: "100px" },
};

const indicatorSizes = {
    xs: { width: "6px", height: "6px" },
    sm: { width: "8px", height: "8px" },
    md: { width: "12px", height: "12px" },
    lg: { width: "14px", height: "14px" },
    xl: { width: "16px", height: "16px" },
};

const Avatar = React.forwardRef<HTMLDivElement, AvatarProps>(
    (
        {
            className,
            src,
            alt = "Avatar",
            size = "md",
            showOnlineIndicator = false,
            isOnline = false,
            fallback,
            ...props
        },
        ref,
    ) => {
        const [imageError, setImageError] = React.useState(false);

        return (
            <div
                ref={ref}
                className={cn("relative", className)}
                style={sizeStyles[size]}
                {...props}
            >
                <div className="w-full h-full rounded-full bg-gray-200 overflow-hidden">
                    {src && !imageError ? (
                        <img
                            src={src}
                            alt={alt}
                            className="w-full h-full object-cover"
                            onError={() => setImageError(true)}
                        />
                    ) : fallback ? (
                        <div className="w-full h-full flex items-center justify-center">
                            {fallback}
                        </div>
                    ) : (
                        <div className="w-full h-full bg-gradient-to-br from-gray-300 to-gray-400" />
                    )}
                </div>
                {showOnlineIndicator && (
                    <div
                        className={cn(
                            "absolute bottom-0 right-0 rounded-full border-[1.5px] border-[#FEFEFE]",
                            isOnline ? "bg-[#22C55E]" : "bg-gray-400",
                        )}
                        style={indicatorSizes[size]}
                    />
                )}
            </div>
        );
    },
);
Avatar.displayName = "Avatar";

export { Avatar };
