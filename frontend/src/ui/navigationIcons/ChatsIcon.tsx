import type { SVGProps } from "react";
import { cn } from "@/lib/utils";

interface IconProps extends SVGProps<SVGSVGElement> {
    isActive?: boolean;
}

function ChatsIcon({ isActive = false, className, ...props }: IconProps) {
    return (
        <svg
            viewBox="0 0 24 24"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
            className={cn("size-6 text-heading-black", className)}
            {...props}
        >
            <path
                d="M12 22C17.5228 22 22 17.5228 22 12C22 6.47715 17.5228 2 12 2C6.47715 2 2 6.47715 2 12C2 13.8214 2.48697 15.5291 3.33782 17L2.5 21.5L7 20.6622C8.47087 21.513 10.1786 22 12 22Z"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className={cn(
                    "transition-all duration-200",
                    isActive ? "fill-current" : "fill-transparent",
                )}
            />
            <g
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className={cn(
                    "transition-colors duration-200",
                    isActive ? "text-accent-white" : "text-current",
                )}
            >
                <path d="M8 10H16" stroke="currentColor" />
                <path d="M8 14H12" stroke="currentColor" />
            </g>
        </svg>
    );
}

export default ChatsIcon;
