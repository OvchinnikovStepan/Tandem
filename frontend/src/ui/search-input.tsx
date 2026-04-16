import * as React from "react";
import { Search } from "lucide-react";
import { cn } from "@/lib/utils";

export interface SearchInputProps extends React.InputHTMLAttributes<HTMLInputElement> {}

const SearchInput = React.forwardRef<HTMLInputElement, SearchInputProps>(
  ({ className, ...props }, ref) => {
    return (
      <div
        className={cn(
          "flex items-center bg-accent-white border border-accent-gray rounded-3xl",
          className
        )}
        style={{ width: "369px", height: "40px", padding: "8px 16px", gap: "12px" }}
      >
        <input
          type="text"
          className="flex-1 font-roboto font-medium text-heading-black/75 outline-none bg-transparent placeholder:text-heading-black/50"
          style={{
            fontSize: "16px",
            lineHeight: "22px",
            letterSpacing: "-0.007em",
          }}
          ref={ref}
          {...props}
        />
        <Search className="w-5 h-5 text-icon-gray shrink-0" />
      </div>
    );
  }
);
SearchInput.displayName = "SearchInput";

export { SearchInput };
