import { useAtom, type PrimitiveAtom } from "jotai";
import type { Interest } from "@/types/interests.ts";

export function useToggleInterest(interestsAtom: PrimitiveAtom<Interest[]>) {
    const [selectedInterests, setSelectedInterests] = useAtom(interestsAtom);

    const toggleInterest = (interest: Interest) => {
        setSelectedInterests((prev) => {
            const exists = prev.some(
                (i) => i.id.toLowerCase() === interest.id.toLowerCase(),
            );
            return exists
                ? prev.filter(
                      (i) => i.id.toLowerCase() !== interest.id.toLowerCase(),
                  )
                : [...prev, interest];
        });
    };

    const isSelected = (interest: Interest) =>
        selectedInterests.some(
            (i) => i.id.toLowerCase() === interest.id.toLowerCase(),
        );

    const isAllSelected = (interests: Interest[]) =>
        interests.every((i) => isSelected(i));

    return {
        selectedInterests,
        toggleInterest,
        isSelected,
        isAllSelected,
    };
}
