import { Trash2 } from "lucide-react";
import * as React from "react";
import type { Interest } from "@/types/interests.ts";

interface EditInterestCardProps extends React.ComponentProps<"div"> {
    interest: Interest;
    toggleInterest?: (interest: Interest) => void;
}

function EditInterestCard({
    interest,
    toggleInterest,
    ...props
}: EditInterestCardProps) {
    return (
        <div
            className="flex items-center gap-1.25 border border-accent-gray px-3 transition-all ease-out duration-300 w-70 h-12 rounded-[1.5rem] hover:shadow-default"
            {...props}
        >
            <span className="leading-5.5 font-roboto font-bold text-heading-black text-[1rem] w-full">
                {interest.name}
            </span>
            <button
                onClick={() => toggleInterest(interest)}
                className="border-l border-accent-gray pl-2.5"
            >
                <Trash2 className="size-5 text-icon-gray transition-all ease-out duration-300 hover:text-heading-black" />
            </button>
        </div>
    );
}

export { EditInterestCard };
