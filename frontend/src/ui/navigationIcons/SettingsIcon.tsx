import type { SVGProps } from "react";
import { cn } from "@/lib/utils";

interface IconProps extends SVGProps<SVGSVGElement> {
    isActive?: boolean;
}

function SettingsIcon({ isActive = false, className, ...props }: IconProps) {
    return (
        <svg
            viewBox="0 0 24 24"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
            className={cn(
                "size-6 text-heading-black transition-colors duration-200",
                className,
            )}
            {...props}
        >
            <path
                fillRule="evenodd"
                clipRule="evenodd"
                d="M12.4883 4.36719L15.1289 2.8916C15.2295 2.88379 15.2314 2.88379 15.2314 2.88379C16.415 3.28127 17.5126 3.89964 18.4658 4.70605C18.5107 4.78367 18.5107 4.80176 18.5107 4.80176L18.5225 8.31543L19.0254 8.60254L21.6338 10.0898C21.6943 10.1738 21.6943 10.1738 21.6943 10.1738C21.9041 11.2283 21.9309 12.3097 21.7744 13.3711L21.6357 13.9102L19.0273 15.3926L18.5244 15.6787L18.5088 19.1953C18.5087 19.2134 18.4639 19.29 18.4639 19.29C17.5109 20.0983 16.4136 20.719 15.2295 21.1182L15.1279 21.1094L12.4893 19.6328L12.001 19.3604L11.5127 19.6328L8.875 21.1074L8.77344 21.1162C7.58966 20.7187 6.49237 20.0995 5.53906 19.293C5.49414 19.1982 5.49414 19.1982 5.49414 19.1982L5.48242 15.6846L4.97949 15.3975L2.36914 13.9102C2.30859 13.8262 2.30859 13.8262 2.30859 13.8262C2.06942 11.3809 2.30859 10.1758 2.30859 10.1758L2.37109 10.0889L4.97266 8.60742L5.47754 7.74219L5.49023 4.80469C5.53516 4.70996 5.53516 4.70996 5.53516 4.70996C6.48822 3.90158 7.58626 3.28104 8.77051 2.88184L8.87109 2.89062L11.5137 4.36719L12.001 4.63965L12.4883 4.36719ZM12 8.5C10.067 8.5 8.5 10.067 8.5 12C8.5 13.933 10.067 15.5 12 15.5C13.933 15.5 15.5 13.933 15.5 12C15.5 10.067 13.933 8.5 12 8.5Z"
                stroke="currentColor"
                strokeWidth="2"
                strokeLinejoin="round"
                className={cn(
                    "transition-all duration-200 ease-in-out",
                    isActive ? "fill-current" : "fill-transparent",
                )}
            />
        </svg>
    );
}

export default SettingsIcon;
