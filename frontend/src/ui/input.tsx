import * as React from "react";
import { cn } from "@/lib/utils";
import { cva, type VariantProps } from "class-variance-authority";

const inputVariants = cva(
  "w-full min-w-0 border bg-transparent text-base transition-[color,box-shadow] outline-none disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50",
  {
    variants: {
      variant: {
        default:
          "file:text-foreground placeholder:text-muted-foreground selection:bg-primary selection:text-primary-foreground border-input h-9 rounded-md px-3 py-1 shadow-xs md:text-sm file:inline-flex file:h-7 file:border-0 file:bg-transparent file:text-sm file:font-medium focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px] aria-invalid:ring-destructive/20 aria-invalid:border-destructive",
        form:
          "h-12 bg-gray-50 border-gray-200 text-black placeholder:text-gray-400 rounded-xl px-4 focus:bg-white focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20",
        profile:
          "h-10 bg-[#FEFEFE] border-[#EAECEE] text-[#333333] placeholder:text-[#666666] rounded-xl px-4 font-roboto font-medium focus:border-[#126DF7] focus:ring-0",
      },
    },
    defaultVariants: {
      variant: "default",
    },
  }
);

export interface InputProps
  extends Omit<React.InputHTMLAttributes<HTMLInputElement>, 'size'>,
    VariantProps<typeof inputVariants> {
  icon?: React.ReactNode;
  iconPosition?: "left" | "right";
}

const Input = React.forwardRef<HTMLInputElement, InputProps>(
  ({ className, type, variant, icon, iconPosition = "right", style, ...props }, ref) => {
    const profilePaddingStyle =
      variant === "profile"
        ? {
            paddingLeft: icon && iconPosition === "left" ? "2.5rem" : "1rem",
            paddingRight: icon && iconPosition === "right" ? "2.5rem" : "1rem",
          }
        : undefined;

    const mergedStyle = profilePaddingStyle ? { ...profilePaddingStyle, ...style } : style;

    if (icon) {
      return (
        <div className="relative">
          <input
            type={type}
            data-slot="input"
            className={cn(
              inputVariants({ variant }),
              iconPosition === "right" ? "pr-12" : "pl-12",
              className
            )}
            ref={ref}
            style={mergedStyle}
            {...props}
          />
          <div
            className={cn(
              "absolute top-1/2 -translate-y-1/2 pointer-events-none",
              iconPosition === "right" ? "right-4" : "left-4"
            )}
          >
            {icon}
          </div>
        </div>
      );
    }

    return (
      <input
        type={type}
        data-slot="input"
        className={cn(inputVariants({ variant }), className)}
        ref={ref}
        style={mergedStyle}
        {...props}
      />
    );
  }
);
Input.displayName = "Input";

export { Input, inputVariants };
