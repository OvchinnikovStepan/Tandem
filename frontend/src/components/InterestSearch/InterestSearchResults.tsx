import type { Interest, InterestSearchResult } from "@/types/interests.ts";
import { UserRound } from "lucide-react";
import { type PrimitiveAtom } from "jotai";
import { useToggleInterest } from "@/hooks/useToggleInterest.ts";
import { forwardRef } from "react";
import { useTranslation } from "react-i18next";

interface InterestSearchResultsProps {
    searchResults: InterestSearchResult[];
    onSelect: (interest: InterestSearchResult) => void;
    interestsAtom: PrimitiveAtom<Interest[]>;
    isLoading: boolean;
}

export const InterestSearchResults = forwardRef<
    HTMLDivElement,
    InterestSearchResultsProps
>(({ searchResults, onSelect, interestsAtom, isLoading }, ref) => {
    const { isSelected, isAllSelected } = useToggleInterest(interestsAtom);
    const { t } = useTranslation();
    const isNotFound =
        (searchResults.length === 0 && !isLoading) ||
        isAllSelected(searchResults);

    return (
        <div
            ref={ref}
            className="absolute w-full left-0 border-x border-b border-accent-gray rounded-b-default bg-accent-white shadow-lg max-h-64 max-w-93 z-10 overflow-y-auto"
        >
            <>
                {isLoading ? null : isNotFound ? (
                    <div className="w-full flex text-center justify-center px-4 py-3 font-roboto font-normal text-heading-black text-sm whitespace-pre-line">
                        {t("questionnaire.edit.search-not-found")}
                    </div>
                ) : (
                    searchResults.map((interest) => {
                        if (!isSelected(interest)) {
                            return (
                                <button
                                    key={interest.id}
                                    type="button"
                                    onClick={() => onSelect(interest)}
                                    className="w-full text-left px-4 py-3 hover:bg-accent-gray transition-all ease-out duration-500 rounded-default"
                                >
                                    <div className="flex items-center justify-between">
                                        <span className="font-roboto font-normal text-heading-black text-[0.9375rem]">
                                            {interest.name}
                                        </span>
                                        <span className="text-sm text-heading-black ml-2 flex items-center justify-between">
                                            {interest.userCount}
                                            <UserRound className="text-icon-gray" />
                                        </span>
                                    </div>
                                </button>
                            );
                        }
                    })
                )}
            </>
        </div>
    );
});
