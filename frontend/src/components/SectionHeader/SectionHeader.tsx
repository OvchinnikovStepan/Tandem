import * as React from "react";
import { ArrowUpRight } from "lucide-react";
import { Link } from "react-router-dom";
import { cn } from "@/lib/utils";

export interface SectionHeaderProps extends React.HTMLAttributes<HTMLDivElement> {
    title: string;
    linkText?: string;
    linkHref?: string;
    showBorder?: boolean;
}

const SectionHeader = React.forwardRef<HTMLDivElement, SectionHeaderProps>(
    (
        { className, title, linkText, linkHref, showBorder = true, ...props },
        ref,
    ) => {
        return (
            <div
                ref={ref}
                className={cn(
                    "flex items-center",
                    showBorder && "border-b border-accent-gray",
                    className,
                )}
                style={{ padding: "24px 0px", gap: "16px", height: "96px" }}
                {...props}
            >
                <h3 className="flex-1 font-roboto font-bold text-heading-black text-section-title">
                    {title}
                </h3>
                {linkText && linkHref && (
                    <Link to={linkHref} className="flex items-center gap-0.5">
                        <span className="font-roboto font-bold text-header-button-text text-body-sm underline">
                            {linkText}
                        </span>
                        <ArrowUpRight className="w-5 h-5 text-header-button-text" />
                    </Link>
                )}
            </div>
        );
    },
);
SectionHeader.displayName = "SectionHeader";

export { SectionHeader };
