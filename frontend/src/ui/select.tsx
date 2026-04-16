import * as React from "react";
import { ChevronDown } from "lucide-react";
import { cn } from "@/lib/utils";
import { cva, type VariantProps } from "class-variance-authority";

const selectVariants = cva(
  "appearance-none w-full min-w-0 border bg-transparent text-base transition-[color,box-shadow] outline-none cursor-pointer disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50",
  {
    variants: {
      variant: {
        default:
          "border-input h-9 rounded-md px-3 py-1 pr-10 shadow-xs md:text-sm focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px]",
        form:
          "h-12 bg-gray-50 border-gray-200 text-black rounded-xl px-4 pr-12 focus:bg-white focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20",
        profile:
          "h-10 bg-accent-white border-accent-gray text-heading-black rounded-xl px-4 pr-12 font-roboto font-medium focus:border-header-button-text focus:ring-0",
      },
    },
    defaultVariants: {
      variant: "default",
    },
  }
);

export interface SelectProps
  extends React.SelectHTMLAttributes<HTMLSelectElement>,
    VariantProps<typeof selectVariants> {}

const Select = React.forwardRef<HTMLSelectElement, SelectProps>(
  ({ className, variant, children, style, ...props }, ref) => {
    const profilePaddingStyle =
      variant === "profile"
        ? {
            paddingLeft: "1rem",
            paddingRight: "3rem",
          }
        : undefined;
    const mergedStyle = profilePaddingStyle ? { ...profilePaddingStyle, ...style } : style;

    return (
      <div className="relative">
        <select
          className={cn(selectVariants({ variant }), className)}
          ref={ref}
          style={mergedStyle}
          {...props}
        >
          {children}
        </select>
        <ChevronDown className="absolute right-4 top-1/2 -translate-y-1/2 w-5 h-5 text-heading-black pointer-events-none" />
      </div>
    );
  }
);
Select.displayName = "Select";

export { Select, selectVariants };
