import type { ButtonHTMLAttributes, ReactNode } from "react";
import { cn } from "@/lib/utils";

export interface IconButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  children: ReactNode;
  size?: "sm" | "md" | "lg";
}

const sizeStyles = {
  sm: { width: "32px", height: "32px", padding: "8px" },
  md: { width: "48px", height: "48px", padding: "12px" },
  lg: { width: "56px", height: "56px", padding: "16px" },
};

const IconButton = ({ className, children, size = "md", ...props }: IconButtonProps) => {
  return (
    <button
      className={cn(
        "flex items-center justify-center rounded-full hover:bg-gray-100 transition-colors",
        className
      )}
      style={sizeStyles[size]}
      {...props}
    >
      {children}
    </button>
  );
};

export { IconButton };
