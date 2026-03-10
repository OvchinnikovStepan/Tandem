import * as React from "react";
import { cn } from "@/lib/utils";
import { cva, type VariantProps } from "class-variance-authority";

const badgeVariants = cva(
  "inline-flex items-center justify-center rounded-full font-semibold",
  {
    variants: {
      variant: {
        default: "bg-[rgba(18,109,247,0.1)] text-[#126DF7] border border-[rgba(18,109,247,0.5)]",
        secondary: "bg-gray-100 text-gray-700",
        success: "bg-green-100 text-green-700",
        warning: "bg-yellow-100 text-yellow-700",
        danger: "bg-red-100 text-red-700",
      },
      size: {
        sm: "px-1.5 py-0.5 text-xs h-5",
        md: "px-2.5 py-1 text-sm h-7",
        lg: "px-3 py-1.5 text-base h-8",
      },
    },
    defaultVariants: {
      variant: "default",
      size: "md",
    },
  }
);

export interface BadgeProps
  extends React.HTMLAttributes<HTMLSpanElement>,
    VariantProps<typeof badgeVariants> {}

const Badge = React.forwardRef<HTMLSpanElement, BadgeProps>(
  ({ className, variant, size, ...props }, ref) => {
    return (
      <span
        ref={ref}
        className={cn(badgeVariants({ variant, size }), className)}
        {...props}
      />
    );
  }
);
Badge.displayName = "Badge";

export { Badge, badgeVariants };
