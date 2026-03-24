import type { Interest } from "@/types/interests.ts";
import { atomWithStorage } from "jotai/utils";

export const selectedInterestsAtom = atomWithStorage<Interest[]>(
    "selectedInterests",
    [],
);
