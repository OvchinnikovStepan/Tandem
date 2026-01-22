import * as React from "react";
import { cn } from "@/lib/utils";
import { ArrowLeft, X } from "lucide-react";

interface IconButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  icon: "back" | "close";
}

function IconButton({ icon, className, ...props }: IconButtonProps) {
  const IconComponent = icon === "back" ? ArrowLeft : X;

  return (
    <button
      type="button"
      className={cn(
        "w-10 h-10 rounded-full border border-gray-200 flex items-center justify-center hover:bg-gray-50 transition-colors",
        className
      )}
      {...props}
    >
      <IconComponent className="w-5 h-5 text-gray-600" />
    </button>
  );
}

export { IconButton };
