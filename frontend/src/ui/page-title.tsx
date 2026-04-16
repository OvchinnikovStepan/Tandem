import * as React from "react";
import { cn } from "@/lib/utils";

export interface PageTitleProps extends React.HTMLAttributes<HTMLHeadingElement> {
  children: React.ReactNode;
}

const PageTitle = React.forwardRef<HTMLHeadingElement, PageTitleProps>(
  ({ className, children, ...props }, ref) => {
    return (
      <h1
        ref={ref}
        className={cn(
          "font-roboto font-bold text-heading-black flex items-center justify-center",
          className
        )}
        style={{
          fontSize: "43.5px",
          lineHeight: "48px",
        }}
        {...props}
      >
        {children}
      </h1>
    );
  }
);
PageTitle.displayName = "PageTitle";

export { PageTitle };
