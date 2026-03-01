import { Suspense } from "react";
import { useAtom, useAtomValue } from "jotai";
import { INTERESTS_GRADIENTS } from "@/modules/Questionnaire/constants/constants.ts";
import { DefaultInterestCard } from "@/modules/Questionnaire/components/DefaultInterestCard/DefaultInterestCard.tsx";
import {
    defaultInterestsAtom,
    selectedInterestsAtom,
} from "@/modules/Questionnaire/atoms/interestsAtoms.ts";
import { useTranslation } from "react-i18next";
import { useUserInterests } from "@/hooks/useUserInterests.ts";
import type { Interest } from "@/types/interests.ts";

function InterestsGrid() {
    const [selectedInterests] = useAtom(selectedInterestsAtom);
    const defaultInterests = useAtomValue(defaultInterestsAtom);
    const { t } = useTranslation();
    const { toggleInterest } = useUserInterests();

    const isCardSelected = (interest: Interest) => {
        return selectedInterests.some(
            (someInterest) =>
                someInterest.id.toLowerCase() === interest.id.toLowerCase(),
        );
    };

    return (
        <div className="grid grid-cols-[repeat(8,8.6875rem)] gap-2.5">
            {defaultInterests.map((interest, index) => (
                <DefaultInterestCard
                    key={interest.id}
                    onClick={() => {
                        toggleInterest(interest);
                        console.log(`${selectedInterests}`);
                    }}
                    interestName={t(
                        `questionnaire.default.interests.${interest.id}`,
                    )}
                    gradient={INTERESTS_GRADIENTS[index]}
                    isSelected={isCardSelected(interest)}
                    iconPath={`interestsIcons/${interest.id}.svg`}
                />
            ))}
        </div>
    );
}

function DefaultInterestsList() {
    const { t } = useTranslation();

    return (
        <div className="flex justify-center">
            <Suspense
                fallback={
                    <div className="flex text-center text-base-black justify-center">
                        {t("questionnaire.default.loading")}
                    </div>
                }
            >
                <InterestsGrid />
            </Suspense>
        </div>
    );
}

export default DefaultInterestsList;
