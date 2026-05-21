import * as React from "react";
import { Switch as SwitchPrimitive } from "radix-ui";

import { cn } from "@/lib/utils";

function Switch({
    className,
    ...props
}: React.ComponentProps<typeof SwitchPrimitive.Root>) {
    return (
        <SwitchPrimitive.Root
            data-slot="switch"
            className={cn(
                "peer inline-flex h-6 w-11 shrink-0 items-center rounded-full border-2 border-transparent",
                "transition-colors ease-out duration-300 cursor-pointer",
                "focus-visible:outline-none focus-visible:ring-3 focus-visible:ring-icon-gray",
                "disabled:cursor-not-allowed disabled:opacity-50",
                "data-[state=checked]:bg-button-yellow data-[state=unchecked]:bg-icon-gray",
                className,
            )}
            {...props}
        >
            <SwitchPrimitive.Thumb
                data-slot="switch-thumb"
                className={cn(
                    "pointer-events-none block h-5 w-5 rounded-full bg-accent-white shadow-md ring-0",
                    "transition-transform ease-out duration-200",
                    "data-[state=checked]:translate-x-5 data-[state=unchecked]:translate-x-0",
                )}
            />
        </SwitchPrimitive.Root>
    );
}

export { Switch };
