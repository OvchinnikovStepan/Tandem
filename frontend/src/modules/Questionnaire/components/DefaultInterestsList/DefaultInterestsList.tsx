import { Suspense } from "react";
import { INTERESTS_GRADIENTS } from "@/modules/Questionnaire/constants/constants.ts";
import { DefaultInterestCard } from "@/modules/Questionnaire/components/DefaultInterestCard/DefaultInterestCard.tsx";
import { selectedInterestsAtom } from "@/modules/Questionnaire/atoms/interestsAtoms.ts";
import { useTranslation } from "react-i18next";
import { useToggleInterest } from "@/hooks/useToggleInterest.ts";
import { useDefaultInterests } from "@/modules/Questionnaire/hooks/useDefaultInterests.ts";

function InterestsGrid() {
    const { i18n } = useTranslation();
    const { defaultInterests } = useDefaultInterests(i18n.language);
    const { toggleInterest, isSelected } = useToggleInterest(
        selectedInterestsAtom,
    );

    return (
        <div className="grid grid-cols-8 2lg:gap-2.5 gap-2">
            {defaultInterests.map((interest, index) => (
                <DefaultInterestCard
                    key={interest.id}
                    onClick={() => toggleInterest(interest)}
                    interestName={interest.name}
                    gradient={INTERESTS_GRADIENTS[index]}
                    isSelected={isSelected(interest)}
                    imgPath={interest.img}
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
                    <div className="flex text-center text-base-black justify-center text-lg font-roboto font-normal">
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
