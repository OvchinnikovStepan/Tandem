import type { Interest, InterestSearchResult } from "@/types/interests.ts";
import { UserRound } from "lucide-react";
import { type PrimitiveAtom } from "jotai";
import { useToggleInterest } from "@/hooks/useToggleInterest.ts";
import { useTranslation } from "react-i18next";
import Button from "@/ui/Button.tsx";

interface InterestSearchResultsProps {
    searchResults: InterestSearchResult[];
    onSelect: (interest: InterestSearchResult) => void;
    interestsAtom: PrimitiveAtom<Interest[]>;
    isLoading: boolean;
}

export const InterestSearchResults = ({
    searchResults,
    onSelect,
    interestsAtom,
    isLoading,
}: InterestSearchResultsProps) => {
    const { isSelected, isAllSelected } = useToggleInterest(interestsAtom);
    const { t } = useTranslation();
    const isNotFound =
        (searchResults.length === 0 && !isLoading) ||
        isAllSelected(searchResults);

    return (
        <div className="absolute w-full left-0 border-x border-b border-accent-gray rounded-b-default bg-accent-white shadow-lg max-h-64 max-w-93 z-10 overflow-y-auto">
            {isLoading ? null : isNotFound ? (
                <div className="w-full flex text-center justify-center px-4 py-3 font-roboto font-normal text-heading-black text-sm whitespace-pre-line">
                    {t("questionnaire.edit.search-not-found")}
                </div>
            ) : (
                searchResults.map((interest) => {
                    if (!isSelected(interest)) {
                        return (
                            <Button
                                key={interest.id}
                                variant="ghost"
                                onClick={() => onSelect(interest)}
                                className="w-full h-fit px-4 py-3 flex items-center justify-between text-heading-black"
                            >
                                <span className="font-roboto font-normal text-md">
                                    {interest.name}
                                </span>
                                <span className="flex ml-2 items-center text-sm">
                                    {interest.userCount}
                                    <UserRound className="size-6 text-icon-gray" />
                                </span>
                            </Button>
                        );
                    }
                })
            )}
        </div>
    );
};
