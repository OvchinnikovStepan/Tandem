import type { ComponentProps } from "react";
import { cn } from "@/lib/utils";

function Input({ className, type, ...props }: ComponentProps<"input">) {
    return (
        <input
            type={type}
            data-slot="input"
            className={cn(
                "h-10 w-full min-w-0 border-2 border-accent-gray bg-accent-white px-2.5 text-base text-heading-black",
                "placeholder:text-base font-roboto font-medium rounded-default",
                "transition-colors ease-out duration-300 file:inline-flex file:h-7 file:border-0 file:text-sm file:font-medium",
                "disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50 md:text-sm",
                "aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive",
                "placeholder-heading-black/75 focus:outline-3 focus:outline-icon-gray focus:-outline-offset-3",
                className,
            )}
            {...props}
        />
    );
}

export { Input };
