import * as React from "react";
import { Search } from "lucide-react";
import { cn } from "@/lib/utils";

export interface SearchInputProps extends React.InputHTMLAttributes<HTMLInputElement> {}

const SearchInput = React.forwardRef<HTMLInputElement, SearchInputProps>(
    ({ className, ...props }, ref) => {
        return (
            <div
                className={cn(
                    "flex items-center bg-[#FEFEFE] border border-[#EAECEE] rounded-3xl",
                    className,
                )}
                style={{
                    width: "369px",
                    height: "40px",
                    padding: "8px 16px",
                    gap: "12px",
                }}
            >
                <input
                    type="text"
                    className="flex-1 font-roboto font-medium text-[#333333]/75 outline-none bg-transparent placeholder:text-[#333333]/50"
                    style={{
                        fontSize: "16px",
                        lineHeight: "22px",
                        letterSpacing: "-0.007em",
                    }}
                    ref={ref}
                    {...props}
                />
                <Search className="w-5 h-5 text-[#C4C4C4] shrink-0" />
            </div>
        );
    },
);
SearchInput.displayName = "SearchInput";

export { SearchInput };
