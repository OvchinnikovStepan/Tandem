import type { SVGProps } from "react";
import { cn } from "@/lib/utils";

interface IconProps extends SVGProps<SVGSVGElement> {
    isActive?: boolean;
}

function HelpIcon({ isActive = false, className, ...props }: IconProps) {
    return (
        <svg
            viewBox="0 0 24 24"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
            className={cn(
                "size-6 transition-colors duration-200 text-heading-black",
                className,
            )}
            {...props}
        >
            <circle
                cx="12"
                cy="12"
                r="10"
                stroke="currentColor"
                strokeWidth="2"
                className={cn(
                    "transition-all duration-200",
                    isActive ? "fill-current" : "fill-transparent",
                )}
            />
            <path
                d="M9 9.00001C9 5.49998 14.5 5.50001 14.5 9.00001C14.5 11.5 12 10.9999 12 13.9999M12 18.0091L12.01 17.998"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinecap="round"
                strokeLinejoin="round"
                className={cn(
                    "transition-colors duration-200",
                    isActive ? "text-accent-white" : "text-current",
                )}
            />
        </svg>
    );
}

export default HelpIcon;
