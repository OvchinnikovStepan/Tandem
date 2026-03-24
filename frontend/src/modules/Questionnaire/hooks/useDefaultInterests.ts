import { getDefaultInterests } from "../api/getDefaultInterests";
import type { DefaultInterest } from "@/types/interests.ts";
import { useSuspenseQuery } from "@tanstack/react-query";

export function useDefaultInterests(loc: string = "en") {
    const { data: defaultInterests } = useSuspenseQuery<DefaultInterest[]>({
        queryKey: ["defaultInterests", loc],
        queryFn: () => getDefaultInterests(loc),
        staleTime: Infinity,
        gcTime: Infinity,
    });

    return {
        defaultInterests,
    };
}
