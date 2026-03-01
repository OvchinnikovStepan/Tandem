import { Plus, Search } from "lucide-react";
import { Input } from "@/ui/Input.tsx";
import type { Interest } from "@/types/interests.ts";
import Button from "@/ui/Button.tsx";
import { useTranslation } from "react-i18next";
import { InterestSearchResults } from "@/components/InterestSearch/InterestSearchResults.tsx";
import { useInterestSearch } from "@/hooks/useInterestSearch.ts";

interface InterestSearchProps {
    selectedInterests: Interest[];
    toggleInterest: (interest: Interest) => void;
    createInterest: (name: string) => Promise<Interest>;
}

function InterestSearch({
    selectedInterests,
    toggleInterest,
    createInterest,
}: InterestSearchProps) {
    const { t } = useTranslation();
    const {
        searchInputRef,
        searchResultsRef,
        searchQuery,
        setSearchQuery,
        showSearchResults,
        setShowSearchResults,
        searchResults,
        isLoading,
        handleSearchResultSelect,
        handleCustomInterestAdd,
    } = useInterestSearch(selectedInterests, toggleInterest, createInterest);

    return (
        <div
            ref={searchResultsRef}
            className="flex justify-center items-center gap-5"
        >
            <div className="relative">
                <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-icon-gray pointer-events-none size-5" />
                <Input
                    id="search"
                    ref={searchInputRef}
                    type="text"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    onFocus={() => {
                        if (
                            searchQuery.trim().length > 0 &&
                            searchResults.length > 0
                        ) {
                            setShowSearchResults(true);
                        }
                    }}
                    placeholder={t("questionnaire.edit.search-placeholder")}
                    onKeyDown={(e) => {
                        if (e.key === "Enter") {
                            e.preventDefault();
                            handleCustomInterestAdd();
                        }
                    }}
                    className="pl-10 border-accent-gray rounded-[0.75rem] h-10 w-93 placeholder-heading-black/75
                placeholder:text-[1rem] leading-5.5 font-roboto font-medium"
                />
                {showSearchResults && (
                    <InterestSearchResults
                        results={searchResults}
                        isLoading={isLoading}
                        onSelect={handleSearchResultSelect}
                        selectedInterests={selectedInterests}
                    />
                )}
            </div>
            <Button
                onClick={() => handleCustomInterestAdd()}
                className="h-10 w-34.5 font-bold text-[1rem]"
            >
                {t("questionnaire.edit.add-button")}
                <Plus className="size-5" />
            </Button>
        </div>
    );
}

export { InterestSearch };
