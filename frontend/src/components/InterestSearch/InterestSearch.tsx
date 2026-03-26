import { Plus, Search } from "lucide-react";
import { Input } from "@/ui/Input.tsx";
import type { Interest } from "@/types/interests.ts";
import Button from "@/ui/Button";
import { useTranslation } from "react-i18next";
import { InterestSearchResults } from "@/components/InterestSearch/InterestSearchResults.tsx";
import { useInterestSearch } from "@/hooks/useInterestSearch.ts";
import type { PrimitiveAtom } from "jotai";
import { cn } from "@/lib/utils.ts";

interface InterestSearchProps {
    interestsAtom: PrimitiveAtom<Interest[]>;
    onCustomInterestAdd?: (name: string) => Promise<Interest | void>;
}

function InterestSearch({
    interestsAtom,
    onCustomInterestAdd,
}: InterestSearchProps) {
    const { t } = useTranslation();
    const {
        searchQuery,
        setSearchQuery,
        showSearchResults,
        searchResults,
        isLoading,
        handleContainerBlur,
        handleSearchResultSelect,
        handleAddButtonClick,
    } = useInterestSearch({ interestsAtom, onCustomInterestAdd });

    const isSearching = searchQuery && isLoading;
    const hasResults = searchQuery && !isLoading && searchResults.length > 0;

    return (
        <div
            className="flex justify-center items-center gap-5"
            onBlur={handleContainerBlur}
        >
            <div className="relative">
                <div
                    className={cn(
                        showSearchResults && "shadow-side-lines rounded-t-2xl",
                    )}
                >
                    <Search
                        className={cn(
                            "absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none size-5 text-icon-gray",
                            isSearching && "animate-color-cycle",
                            hasResults && "text-heading-black",
                        )}
                    />
                    <Input
                        name="interest-search"
                        type="text"
                        value={searchQuery}
                        onChange={(e) => setSearchQuery(e.target.value)}
                        placeholder={t("questionnaire.edit.search-placeholder")}
                        autoComplete="off"
                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                e.preventDefault();
                                handleAddButtonClick();
                            }
                        }}
                        className="pl-10 h-10 w-93 z-0"
                    />
                </div>
                {showSearchResults && (
                    <InterestSearchResults
                        searchResults={searchResults}
                        onSelect={handleSearchResultSelect}
                        interestsAtom={interestsAtom}
                        isLoading={isLoading}
                    />
                )}
            </div>
            <Button
                onClick={() => handleAddButtonClick()}
                className="h-10 font-bold text-base has-[>svg]:px-4.5"
            >
                {t("questionnaire.edit.add-button")}
                <Plus className="size-5" />
            </Button>
        </div>
    );
}

export { InterestSearch };
