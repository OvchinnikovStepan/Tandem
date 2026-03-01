import { useEffect, useRef, useState } from "react";
import type { Interest, InterestSearchResult } from "@/types/interests.ts";
import { searchInterests } from "@/api/interests.ts";

export function useInterestSearch(
    selectedInterests: Interest[],
    toggleInterest: (interest: Interest) => void,
    createInterest: (name: string) => Promise<Interest>,
    delay: number = 300,
) {
    const [searchResults, setSearchResults] = useState<InterestSearchResult[]>(
        [],
    );
    const [isLoading, setIsLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const [searchQuery, setSearchQuery] = useState("");
    const searchInputRef = useRef<HTMLInputElement>(null);
    const searchResultsRef = useRef<HTMLDivElement>(null);

    const [showSearchResults, setShowSearchResults] = useState(false);

    useEffect(() => {
        setShowSearchResults(
            searchQuery.trim().length > 0 && searchResults.length > 0,
        );
    }, [searchQuery, searchResults]);

    useEffect(() => {
        function handleClickOutside(event: MouseEvent) {
            if (
                searchResultsRef.current &&
                !searchResultsRef.current.contains(event.target as Node) &&
                searchInputRef.current &&
                !searchInputRef.current.contains(event.target as Node)
            ) {
                setShowSearchResults(false);
            }
        }

        document.addEventListener("mousedown", handleClickOutside);
        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    useEffect(() => {
        if (!searchQuery.trim()) {
            setSearchResults([]);
            return;
        }

        const timeoutId = setTimeout(async () => {
            setIsLoading(true);
            setError(null);
            try {
                const searchResults = await searchInterests(searchQuery);
                setSearchResults(searchResults);
            } catch (err) {
                setError(err instanceof Error ? err.message : "Ошибка поиска");
                setSearchResults([]);
            } finally {
                setIsLoading(false);
            }
        }, delay);

        return () => clearTimeout(timeoutId);
    }, [searchQuery, delay]);

    const handleSearchResultSelect = async (interest: InterestSearchResult) => {
        toggleInterest(interest);
        setSearchQuery("");
        setShowSearchResults(false);
    };

    const handleCustomInterestAdd = async () => {
        const customInterestName = searchQuery.trim();
        const isAlreadySelected = selectedInterests.some(
            (item) =>
                item.name.toLowerCase() === customInterestName.toLowerCase(),
        );

        if (!customInterestName || isAlreadySelected) {
            setSearchQuery("");
            return;
        }

        if (
            searchResults.length > 0 &&
            searchResults[0].name.toLowerCase() ===
                customInterestName.toLowerCase()
        ) {
            handleSearchResultSelect(searchResults[0]);
            return;
        }

        try {
            const newInterest = await createInterest(customInterestName);
            toggleInterest(newInterest);
            setSearchQuery("");
            setShowSearchResults(false);
        } catch (error) {
            console.error("Failed to create custom interest:", error);
        }
    };

    return {
        searchInputRef,
        searchResultsRef,
        searchQuery,
        setSearchQuery,
        showSearchResults,
        setShowSearchResults,
        searchResults,
        isLoading,
        error,
        handleSearchResultSelect,
        handleCustomInterestAdd,
    };
}
