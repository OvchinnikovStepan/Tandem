import type { SVGProps } from "react";
import { cn } from "@/lib/utils";

interface IconProps extends SVGProps<SVGSVGElement> {
    isActive?: boolean;
}

function ProfileIcon({ isActive = false, className, ...props }: IconProps) {
    return (
        <svg
            viewBox="0 0 24 24"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
            className={cn("size-6 transition-colors duration-200", className)}
            {...props}
        >
            <circle
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                strokeWidth="2"
            />
            <g
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className={cn(
                    "transition-all duration-200 ease-in-out",
                    isActive ? "fill-current" : "fill-transparent",
                )}
            >
                <path d="M12 12C13.6569 12 15 10.6569 15 9C15 7.34315 13.6569 6 12 6C10.3431 6 9 7.34315 9 9C9 10.6569 10.3431 12 12 12Z" />
                <path d="M4.26953 18.3457C4.26953 18.3457 6.49855 15.5 11.9985 15.5C17.4985 15.5 19.7276 18.3457 19.7276 18.3457C17.9622 20.5939 15.1481 22 11.9985 22C8.84897 22 6.03487 20.5939 4.26953 18.3457Z" />
            </g>
        </svg>
    );
}

export default ProfileIcon;
