import * as React from "react";
import { Trash2, X } from "lucide-react";
import { cn } from "@/lib/utils";

export interface TagProps extends React.HTMLAttributes<HTMLDivElement> {
    label: string;
    onRemove?: () => void;
    removeIcon?: "trash" | "x";
    variant?: "default" | "outline";
}

const Tag = React.forwardRef<HTMLDivElement, TagProps>(
    (
        {
            className,
            label,
            onRemove,
            removeIcon = "trash",
            variant = "outline",
            ...props
        },
        ref,
    ) => {
        const RemoveIcon = removeIcon === "trash" ? Trash2 : X;

        return (
            <div
                ref={ref}
                className={cn(
                    "flex items-center rounded-3xl",
                    variant === "outline" &&
                        "bg-[#FEFEFE] border border-[#EAECEE]",
                    variant === "default" && "bg-gray-100",
                    className,
                )}
                style={{
                    width: "280px",
                    height: "48px",
                    minHeight: "48px",
                    padding: "10px 12px",
                }}
                {...props}
            >
                <div
                    className="flex items-center flex-1 border-r border-[#EAECEE]"
                    style={{
                        height: "22px",
                        paddingLeft: "8px",
                        paddingRight: "12px",
                    }}
                >
                    <span
                        className="font-roboto font-bold text-[#333333]"
                        style={{
                            fontSize: "16px",
                            lineHeight: "22px",
                            letterSpacing: "-0.007em",
                        }}
                    >
                        {label}
                    </span>
                </div>
                {onRemove && (
                    <button
                        onClick={onRemove}
                        className="w-10 h-10 flex items-center justify-center hover:bg-gray-100 rounded-full transition-colors ml-2"
                        type="button"
                    >
                        <RemoveIcon className="w-5 h-5 text-[#C4C4C4]" />
                    </button>
                )}
            </div>
        );
    },
);
Tag.displayName = "Tag";

export { Tag };
