import * as React from "react";
import { cn } from "@/lib/utils";

export interface FormFieldProps extends React.HTMLAttributes<HTMLDivElement> {
  label: string;
  htmlFor?: string;
  required?: boolean;
  error?: string;
  children: React.ReactNode;
  labelAlign?: "center" | "start";
}

const FormField = React.forwardRef<HTMLDivElement, FormFieldProps>(
  (
    { className, label, htmlFor, required, error, children, labelAlign = "center", ...props },
    ref
  ) => {
    return (
      <div
        ref={ref}
        className={cn(
          "flex flex-row gap-[10px]",
          labelAlign === "center" ? "items-center justify-end" : "items-start justify-end",
          className
        )}
        {...props}
      >
        <label
          htmlFor={htmlFor}
          className={cn(
            "font-roboto font-medium text-[#333333] flex items-center shrink-0",
            labelAlign === "start" && "pt-2"
          )}
          style={{
            fontSize: "16px",
            lineHeight: "22px",
            letterSpacing: "-0.007em",
          }}
        >
          {label}
          {required && <span className="text-red-500 ml-0.5">*</span>}
        </label>
        <div className="flex flex-col">
          {children}
          {error && (
            <span className="text-sm text-red-500 mt-1">{error}</span>
          )}
        </div>
      </div>
    );
  }
);
FormField.displayName = "FormField";

export { FormField };
