import Button from "@/ui/Button.tsx";
import { useAtom } from "jotai";
import { questionnaireStepperAtom } from "@/modules/Questionnaire/atoms/questionnaireStepperAtom";
import { EditInterestCard } from "@/components/EditInterestCard/EditInterestCard.tsx";
import { useUserInterests } from "@/hooks/useUserInterests";
import { selectedInterestsAtom } from "@/modules/Questionnaire/atoms/interestsAtoms.ts";
import { useTranslation } from "react-i18next";
import { InterestSearch } from "@/components/InterestSearch";

function EditInterestsForm() {
    const { t } = useTranslation();
    const [, setSelectedForm] = useAtom(questionnaireStepperAtom);
    const [selectedInterests] = useAtom(selectedInterestsAtom);
    const { toggleInterest, createInterest } = useUserInterests();

    const handleNext = () => {
        if (selectedInterests.length === 0) {
            alert("Пожалуйста, выберите хотя бы один интерес");
            return;
        }
        setSelectedForm(2);
    };

    return (
        <div className="flex flex-col justify-between h-full">
            <div className="w-full">
                <div className="relative">
                    <InterestSearch
                        selectedInterests={selectedInterests}
                        toggleInterest={toggleInterest}
                        createInterest={createInterest}
                    />
                </div>
                <div className="flex justify-center mt-9">
                    <div className="border border-accent-gray rounded-[1.5rem] px-15 py-10 shadow-default max-h-105 overflow-y-auto">
                        {selectedInterests.length > 0 ? (
                            <div
                                className={`grid grid-cols-[repeat(var(--cols),_minmax(0,_1fr))] gap-2.5`}
                                style={
                                    {
                                        "--cols": Math.min(
                                            selectedInterests.length,
                                            3,
                                        ),
                                    } as React.CSSProperties
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
            <div className="flex justify-end gap-4 mb-19.5 mr-32">
                <Button
                    onClick={() => setSelectedForm(0)}
                    variant="action"
                    className="h-[2.8125rem] w-25 font-medium text-[0.9375rem] leading-5"
                >
                    {t("questionnaire.buttons.back")}
                </Button>
                <Button
                    onClick={handleNext}
                    disabled={selectedInterests.length === 0}
                    className="h-[2.8125rem] w-25"
                >
                    {t("questionnaire.buttons.next")}
                </Button>
            </div>
        </div>
    );
}

export default EditInterestsForm;
