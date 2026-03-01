import { atom } from "jotai";
import type { Interest } from "@/types/interests.ts";
import { atomWithStorage } from "jotai/utils";
import { getDefaultInterests } from "@/modules/Questionnaire/api/getDefaultInterests.ts";

export const selectedInterestsAtom = atomWithStorage<Interest[]>(
    "selectedInterests",
    [],
);
export const defaultInterestsCacheAtom = atom<Interest[]>([]);

export const defaultInterestsAtom = atom<Promise<Interest[]>>(async (get) => {
    const cached = get(defaultInterestsCacheAtom);

    if (cached.length > 0) {
        return cached;
    }

    return await getDefaultInterests();
});
