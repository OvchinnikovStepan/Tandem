import Button from "@/ui/Button.tsx";
import { useSetAtom } from "jotai";
import { questionnaireStepperAtom } from "@/modules/Questionnaire/atoms/questionnaireStepperAtom";
import { EditInterestCard } from "@/components/EditInterestCard/EditInterestCard.tsx";
import { useToggleInterest } from "@/hooks/useToggleInterest.ts";
import { selectedInterestsAtom } from "@/modules/Questionnaire/atoms/interestsAtoms.ts";
import { useTranslation } from "react-i18next";
import { InterestSearch } from "@/components/InterestSearch";
import { type CSSProperties } from "react";

function EditInterestsForm() {
    const { t } = useTranslation();
    const setSelectedForm = useSetAtom(questionnaireStepperAtom);
    const { selectedInterests, toggleInterest } = useToggleInterest(
        selectedInterestsAtom,
    );

    return (
        <div className="flex flex-col justify-between h-full gap-4">
            <div className="w-full">
                <div className="relative">
                    <InterestSearch interestsAtom={selectedInterestsAtom} />
                </div>
                <div className="flex justify-center mt-9">
                    <div
                        className="grid border border-accent-gray rounded-3xl px-15 py-10 shadow-default
                        2xl:max-h-105 2lg:max-h-91 max-h-76 min-w-135 overflow-y-auto justify-center"
                    >
                        {selectedInterests.length > 0 ? (
                            <div
                                className={`grid grid-cols-[repeat(var(--cols),minmax(0,1fr))] gap-2.5`}
                                style={
                                    {
                                        "--cols": Math.min(
                                            selectedInterests.length,
                                            3,
                                        ),
                                    } as CSSProperties
                                }
                            >
                                {selectedInterests.map((interest) => (
                                    <EditInterestCard
                                        key={interest.id}
                                        interest={interest}
                                        toggleInterest={toggleInterest}
                                    />
                                ))}
                            </div>
                        ) : (
                            <div className="min-w-105 leading-5.5 font-roboto font-medium text-heading-black text-center">
                                {t("questionnaire.edit.no-interests")}
                            </div>
                        )}
                    </div>
                </div>
            </div>
            <div className="flex justify-end gap-4 mr-16 mb-8 2xl:mr-32 2xl:mb-19.5">
                <Button
                    onClick={() => setSelectedForm(0)}
                    variant="action"
                    className="h-11.25 w-25 font-medium text-md leading-5"
                >
                    {t("questionnaire.buttons.back")}
                </Button>
                <Button
                    onClick={() => setSelectedForm(2)}
                    disabled={selectedInterests.length === 0}
                    className="h-11.25 w-25"
                >
                    {t("questionnaire.buttons.next")}
                </Button>
            </div>
        </div>
    );
}

export default EditInterestsForm;
