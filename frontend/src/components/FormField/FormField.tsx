import type { HTMLAttributes, ReactNode } from "react";
import { cn } from "@/lib/utils";
import { useFormContext } from "react-hook-form";

export interface FormFieldProps extends HTMLAttributes<HTMLDivElement> {
  label: string;
  htmlFor?: string;
  required?: boolean;
  error?: string;
  name?: string;
  children: ReactNode;
  labelAlign?: "center" | "start";
  labelWidth?: string;
}

function getNestedErrorMessage(
  errors: Record<string, unknown> | undefined,
  fieldName: string | undefined
) {
  if (!errors || !fieldName) {
    return undefined;
  }

  const parts = fieldName.split(".");
  let current: unknown = errors;

  for (const part of parts) {
    if (typeof current !== "object" || current === null || !(part in current)) {
      return undefined;
    }

    current = (current as Record<string, unknown>)[part];
  }

  if (typeof current === "object" && current !== null && "message" in current) {
    const message = (current as { message?: unknown }).message;
    if (typeof message === "string") {
      return message;
    }
  }

  return undefined;
}

const FormField = ({
  className,
  label,
  htmlFor,
  required,
  error,
  name,
  children,
  labelAlign = "center",
  labelWidth,
  ...props
}: FormFieldProps) => {
  const formContext = useFormContext();
  const formError = getNestedErrorMessage(
    formContext?.formState?.errors as Record<string, unknown> | undefined,
    name
  );
  const resolvedError = error ?? formError;

  return (
    <div
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
          "font-roboto font-medium text-heading-black flex items-center shrink-0",
          labelAlign === "start" && "pt-2"
        )}
        style={{
          fontSize: "16px",
          lineHeight: "22px",
          letterSpacing: "-0.007em",
          ...(labelWidth ? { width: labelWidth } : {}),
        }}
      >
        {label}
        {required && <span className="text-red-500 ml-0.5">*</span>}
      </label>
      <div className="flex flex-col">
        {children}
        {resolvedError && <span className="text-sm text-red-500 mt-1">{resolvedError}</span>}
      </div>
    </div>
  );
};

export { FormField };
