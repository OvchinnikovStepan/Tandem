import * as React from "react";
import { Slot } from "@radix-ui/react-slot";
import { cva, type VariantProps } from "class-variance-authority";
import { cn } from "@/lib/utils.ts";

const buttonVariants = cva(
    "inline-flex w-fit items-center justify-center gap-2 whitespace-nowrap text-md font-roboto font-medium transition-all ease-out duration-300 disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none shrink-0 outline-none focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-destructive/20 dark:aria-invalid:ring-destructive/40 aria-invalid:border-destructive",
    {
        variants: {
            variant: {
                default:
                    "bg-button-yellow hover:bg-button-yellow-hover text-heading-black leading-5 transform",
                destructive:
                    "bg-destructive text-white hover:bg-destructive/90 focus-visible:ring-destructive/20 dark:focus-visible:ring-destructive/40 dark:bg-destructive/60",
                outline:
                    "border bg-background shadow-xs hover:bg-accent hover:text-accent-foreground",
                secondary:
                    "bg-header-button hover:bg-header-button-hover font-[400] leading-4 transform text-[0.8125rem] text-action-button-text",
                ghost: "hover:bg-accent-gray duration-500 dark:hover:bg-accent/50",
                link: "text-action-button-text hover:text-blue-700 underline-offset-4 hover:underline",
                action: "bg-action-button hover:bg-action-button-hover text-action-button-text font-[400] leading-4 transform",
                custom: "",
            },
            size: {
                default: "h-14 px-6 rounded-default has-[>svg]:px-3",
                sm: "h-8.75 px-4.5 rounded-md gap-1.5 has-[>svg]:px-2.5",
                lg: "h-10 rounded-md px-6 has-[>svg]:px-4",
                icon: "size-9",
                "icon-sm": "size-8",
                "icon-lg": "size-10",
                custom: "",
            },
        },
        defaultVariants: {
            variant: "default",
            size: "default",
        },
    },
);

function Button({
    className,
    variant,
    size,
    asChild = false,
    ...props
}: React.ComponentProps<"button"> &
    VariantProps<typeof buttonVariants> & {
        asChild?: boolean;
    }) {
    const Comp = asChild ? Slot : "button";

    return (
        <Comp
            data-slot="button"
            className={cn(buttonVariants({ variant, size, className }))}
            {...props}
        />
    );
}

export default Button;
