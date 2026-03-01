import * as React from "react";
import { cn } from "@/lib/utils";

interface InterestCardProps extends React.ComponentProps<"button"> {
    interestName: string;
    iconPath?: string;
    gradient: string;
    isSelected?: boolean;
}

function DefaultInterestCard({
    className,
    interestName,
    iconPath,
    gradient,
    isSelected,
    ...props
}: InterestCardProps) {
    return (
        <button
            type="button"
            className={cn(
                "relative size-34.75 rounded-[0.75rem] overflow-hidden flex flex-col",
                "transition-all ease-out duration-300",
                "after:absolute after:inset-0 after:rounded-[0.75rem] after:pointer-events-none after:opacity-0",
                "after:transition-opacity after:duration-300 after:ease-out",
                "after:shadow-[inset_0_0_0_2px_rgba(0,0,0,0.5)] [--tw-shadow-opacity:0.5] hover:shadow-default",
                gradient,
                isSelected
                    ? "after:opacity-100 shadow-default after:shadow-[inset_0_0_0_2px_rgba(0,0,0,0.7)]"
                    : "hover:after:opacity-100",
                className,
            )}
            {...props}
        >
            <span className="text-lg leading-4.5 font-roboto font-normal text-heading-black text-left pt-2.5 pl-3.75">
                {interestName}
            </span>
            <div className="size-full flex relative">
                <img
                    src={iconPath}
                    alt="Icon"
                    draggable="false"
                    className="size-22.5 absolute bottom-0"
                />
            </div>
        </button>
    );
}

export { DefaultInterestCard };
