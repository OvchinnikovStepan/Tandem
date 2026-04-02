import { useState } from "react";
import type { Interest, InterestSearchResult } from "@/types/interests.ts";
import { searchInterests } from "@/api/interests.ts";
import type { PrimitiveAtom } from "jotai";
import { useToggleInterest } from "@/hooks/useToggleInterest.ts";
import { useDebounce } from "@/hooks/useDebounce.ts";
import { useQuery } from "@tanstack/react-query";
import type { FocusEvent } from "react";

interface UseInterestSearchOptions {
    interestsAtom: PrimitiveAtom<Interest[]>;
    onCustomInterestAdd?: (name: string) => Promise<Interest | void>;
}

export function useInterestSearch({
    interestsAtom,
    onCustomInterestAdd,
}: UseInterestSearchOptions) {
    const { selectedInterests, toggleInterest } =
        useToggleInterest(interestsAtom);
    const [searchQuery, setSearchQuery] = useState("");
    const debouncedQuery = useDebounce(searchQuery.trim(), 350);
    const showSearchResults = searchQuery.trim().length > 0;

    const {
        data: searchResults = [],
        isLoading,
        error,
    } = useQuery({
        queryKey: ["interests", "search", debouncedQuery],
        queryFn: () => searchInterests(debouncedQuery),
        enabled: debouncedQuery.length > 0,
        staleTime: 1000 * 60 * 2,
    });

    const handleContainerBlur = (e: FocusEvent) => {
        if (e.currentTarget.contains(e.relatedTarget)) return;
        setSearchQuery("");
    };

    const handleSearchResultSelect = (interest: InterestSearchResult) => {
        toggleInterest(interest);
        setSearchQuery("");
    };

    const handleAddButtonClick = async () => {
        const trimmed = searchQuery.trim();
        if (!trimmed) return;

        const isAlreadySelected = selectedInterests.some(
            (i) => i.name.toLowerCase() === trimmed.toLowerCase(),
        );

        if (isAlreadySelected) {
            setSearchQuery("");
            return;
        }

        if (searchResults[0]?.name.toLowerCase() === trimmed.toLowerCase()) {
            handleSearchResultSelect(searchResults[0]);
            return;
        }

        // Создание кастомного интереса — только если передан обработчик
        // Это будет окончательно реализовано в редактировании профиля
        if (onCustomInterestAdd) {
            const newInterest = await onCustomInterestAdd(trimmed);
            if (newInterest) toggleInterest(newInterest);
            setSearchQuery("");
        }
    };

    return {
        selectedInterests,
        searchQuery,
        setSearchQuery,
        showSearchResults,
        searchResults,
        isLoading,
        error,
        handleContainerBlur,
        handleSearchResultSelect,
        handleAddButtonClick,
    };
}
