import type { Interest, InterestSearchResult } from "@/types/interests.ts";
import { UserRound } from "lucide-react";

interface InterestSearchResultsProps {
    results: InterestSearchResult[];
    isLoading: boolean;
    onSelect: (interest: InterestSearchResult) => void;
    selectedInterests: Interest[];
}

export function InterestSearchResults({
    results,
    isLoading,
    onSelect,
    selectedInterests,
}: InterestSearchResultsProps) {
    if (isLoading) {
        return (
            <div className="mt-2 p-4 text-center text-gray-500">Поиск...</div>
        );
    }

    if (results.length === 0) {
        return null;
    }

    return (
        <div className="absolute left-0 mt-2 border border-gray-200 rounded-lg bg-white shadow-lg max-h-64 max-w-93 overflow-y-auto">
            {results.map((interest) => {
                const isSelected = selectedInterests.some(
                    (someInterest) =>
                        someInterest.id.toLowerCase() ===
                        interest.id.toLowerCase(),
                );
                return (
                    <button
                        key={interest.id}
                        type="button"
                        onClick={() => !isSelected && onSelect(interest)}
                        disabled={isSelected}
                        className={`w-full text-left px-4 py-3 hover:bg-gray-50 transition-colors ${
                            isSelected
                                ? "bg-yellow-100 cursor-not-allowed opacity-60"
                                : ""
                        }`}
                    >
                        <div className="flex items-center justify-between">
                            <span className="font-medium text-black">
                                {interest.name}
                            </span>
                            <span className="text-sm text-gray-500 ml-2 flex items-center ">
                                {interest.userCount}
                                <UserRound className="text-icon-gray" />
                            </span>
                        </div>
                    </button>
                );
            })}
        </div>
    );
}
