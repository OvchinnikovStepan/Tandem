import * as React from "react";
import { cn } from "@/lib/utils";

interface InterestCardProps extends React.ComponentProps<"button"> {
    interestName: string;
    imgPath?: string;
    gradient: string;
    isSelected?: boolean;
}

function DefaultInterestCard({
    className,
    interestName,
    imgPath,
    gradient,
    isSelected = false,
    ...props
}: InterestCardProps) {
    return (
        <button
            type="button"
            className={cn(
                "relative 2xl:size-35 2lg:size-32 size-26 rounded-default overflow-hidden flex flex-col",
                "transition-all ease-out duration-300",
                "after:absolute after:inset-0 after:rounded-default after:pointer-events-none after:opacity-0",
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
            <span className="text-sm 2lg:text-base 2xl:text-lg leading-4.5 font-roboto font-normal text-heading-black text-left pt-2.5 2xl:pl-3.75 pl-2">
                {interestName}
            </span>
            <div className="size-full flex relative">
                <img
                    src={imgPath || "interestsIcons/default.svg"}
                    alt="Icon"
                    draggable="false"
                    className="2lg:size-22.5 size-16 absolute bottom-0"
                />
            </div>
        </button>
    );
}

export { DefaultInterestCard };
