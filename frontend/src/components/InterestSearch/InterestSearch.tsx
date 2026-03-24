import { Plus, Search } from "lucide-react";
import { Input } from "@/ui/Input.tsx";
import type { Interest } from "@/types/interests.ts";
import Button from "@/ui/Button";
import { useTranslation } from "react-i18next";
import { InterestSearchResults } from "@/components/InterestSearch/InterestSearchResults.tsx";
import { useInterestSearch } from "@/hooks/useInterestSearch.ts";
import type { PrimitiveAtom } from "jotai";

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
        searchInputRef,
        searchResultsRef,
        addButtonRef,
        searchQuery,
        setSearchQuery,
        showSearchResults,
        searchResults,
        isLoading,
        handleSearchResultSelect,
        handleAddButtonClick,
    } = useInterestSearch({ interestsAtom, onCustomInterestAdd });

    return (
        <div className="flex justify-center items-center gap-5">
            <div className="relative">
                <div
                    className={`${showSearchResults && "shadow-side-lines rounded-t-2xl"}`}
                >
                    <Search
                        className={`absolute left-3 top-1/2 -translate-y-1/2 pointer-events-none size-5
                        ${
                            searchQuery
                                ? isLoading
                                    ? "animate-color-cycle"
                                    : searchResults.length === 0
                                      ? "text-icon-gray"
                                      : "text-heading-black"
                                : "text-icon-gray"
                        }`}
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
                        ref={searchInputRef}
                        className="pl-10 h-10 w-93 z-0"
                    />
                </div>
                {showSearchResults && (
                    <InterestSearchResults
                        ref={searchResultsRef}
                        searchResults={searchResults}
                        onSelect={handleSearchResultSelect}
                        interestsAtom={interestsAtom}
                        isLoading={isLoading}
                    />
                )}
            </div>
            <div ref={addButtonRef}>
                <Button
                    onClick={() => handleAddButtonClick()}
                    className="h-10 w-34.5 font-bold text-base"
                >
                    {t("questionnaire.edit.add-button")}
                    <Plus className="size-5" />
                </Button>
            </div>
        </div>
    );
}

export { InterestSearch };
