import * as React from "react";
import { cn } from "@/lib/utils";
import { cva, type VariantProps } from "class-variance-authority";

const textareaVariants = cva(
  "w-full min-w-0 border bg-transparent text-base transition-[color,box-shadow] outline-none resize-none disabled:pointer-events-none disabled:cursor-not-allowed disabled:opacity-50",
  {
    variants: {
      variant: {
        default:
          "placeholder:text-muted-foreground border-input rounded-md px-3 py-2 shadow-xs md:text-sm focus-visible:border-ring focus-visible:ring-ring/50 focus-visible:ring-[3px]",
        form:
          "bg-gray-50 border-gray-200 text-black placeholder:text-gray-400 rounded-xl px-4 py-3 focus:bg-white focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20",
        profile:
          "bg-[#FEFEFE] border-[#EAECEE] text-[#333333] placeholder:text-[#666666] rounded-xl px-4 py-3 font-roboto font-medium focus:border-[#126DF7] focus:ring-0",
      },
    },
    defaultVariants: {
      variant: "default",
    },
  }
);

export interface TextareaProps
  extends React.TextareaHTMLAttributes<HTMLTextAreaElement>,
    VariantProps<typeof textareaVariants> {}

const Textarea = React.forwardRef<HTMLTextAreaElement, TextareaProps>(
  ({ className, variant, style, ...props }, ref) => {
    const profilePaddingStyle =
      variant === "profile"
        ? {
            paddingLeft: "1rem",
            paddingRight: "1rem",
          }
        : undefined;
    const mergedStyle = profilePaddingStyle ? { ...profilePaddingStyle, ...style } : style;

    return (
      <textarea
        className={cn(textareaVariants({ variant }), className)}
        ref={ref}
        style={mergedStyle}
        {...props}
      />
    );
  }
);
Textarea.displayName = "Textarea";

export { Textarea, textareaVariants };
