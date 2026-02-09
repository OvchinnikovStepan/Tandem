import { cn } from "@/lib/utils";
import * as React from "react";
import { cva, type VariantProps } from "class-variance-authority";

const inputVariants = cva(
  "w-full min-w-0 border bg-transparent text-base transition-[color,box-shadow] outline-none disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50",
  {
    variants: {
      variant: {
        default:
          "file:text-foreground placeholder:text-muted-foreground selection:bg-primary selection:text-primary-foreground border-input h-9 rounded-md px-3 py-1 shadow-xs md:text-sm file:inline-flex file:h-7 file:border-0 file:bg-transparent file:text-sm file:font-medium focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-destructive/20 aria-invalid:border-destructive",
        form:
          "h-12 bg-gray-50 border-gray-200 text-black placeholder:text-gray-400 rounded-xl focus:bg-white focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20",
      },
    },
    defaultVariants: {
      variant: "default",
    },
  }
);

interface InputProps
  extends React.ComponentProps<"input">,
    VariantProps<typeof inputVariants> {
  leftIcon?: boolean;
  rightIcon?: boolean;
}

function Input({
  className,
  type,
  variant,
  leftIcon,
  rightIcon,
  ...props
}: InputProps) {
  return (
    <input
      type={type}
      data-slot="input"
      className={cn(
        inputVariants({ variant }),
        leftIcon && "pl-12",
        rightIcon && "pr-12",
        className
      )}
      {...props}
    />
  );
}

export { Input, inputVariants };
