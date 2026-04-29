import * as React from "react";
import { cva, type VariantProps } from "class-variance-authority";
import { cn } from "@/lib/utils";

const inputVariants = cva(
    "w-full min-w-0 border bg-transparent text-base transition-[color,box-shadow] outline-none disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50",
    {
        variants: {
            variant: {
                default:
                    "border-input h-9 rounded-md px-3 py-1 shadow-xs md:text-sm focus-visible:ring-ring/50 focus-visible:ring-[3px]",
                form: "h-12 bg-gray-50 border-gray-200 text-black placeholder:text-gray-400 rounded-xl focus:bg-white focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20",
            },
        },
        defaultVariants: { variant: "default" },
    },
);

interface AuthInputProps
    extends React.ComponentProps<"input">,
        VariantProps<typeof inputVariants> {
    leftIcon?: boolean;
    rightIcon?: boolean;
}

function AuthInput({
    className,
    type,
    variant,
    leftIcon,
    rightIcon,
    ...props
}: AuthInputProps) {
    return (
        <input
            type={type}
            data-slot="input"
            className={cn(
                inputVariants({ variant }),
                leftIcon && "pl-12",
                rightIcon && "pr-12",
                className,
            )}
            {...props}
        />
    );
}

export { AuthInput, inputVariants };
