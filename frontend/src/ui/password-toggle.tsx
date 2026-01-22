import * as React from "react";
import { cn } from "@/lib/utils";
import { Eye, EyeOff } from "lucide-react";

interface PasswordToggleProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  showPassword: boolean;
}

function PasswordToggle({ showPassword, className, ...props }: PasswordToggleProps) {
  return (
    <button
      type="button"
      className={cn(
        "absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors",
        className
      )}
      {...props}
    >
      {showPassword ? (
        <EyeOff className="w-5 h-5" />
      ) : (
        <Eye className="w-5 h-5" />
      )}
    </button>
  );
}

export { PasswordToggle };
