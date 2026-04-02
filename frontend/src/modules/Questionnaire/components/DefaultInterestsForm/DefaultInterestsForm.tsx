import Button from "@/ui/Button.tsx";
import DefaultInterestsList from "@/modules/Questionnaire/components/DefaultInterestsList/DefaultInterestsList.tsx";
import { useSetAtom } from "jotai";
import { questionnaireStepperAtom } from "@/modules/Questionnaire/atoms/questionnaireStepperAtom";
import { useTranslation } from "react-i18next";

function DefaultInterestsForm() {
    const setSelectedForm = useSetAtom(questionnaireStepperAtom);
    const { t } = useTranslation();

    return (
        <div className="flex flex-col h-full justify-between gap-4">
            <div className="w-full items-center justify-center">
                <DefaultInterestsList />
            </div>
            <div className="flex justify-end mr-16 mb-8 2xl:mr-32 2xl:mb-19.5">
                <Button
                    onClick={() => setSelectedForm(1)}
                    className="h-11.25 w-25"
                >
                    {t("questionnaire.buttons.next")}
                </Button>
            </div>
        </div>
    );
}

export default DefaultInterestsForm;
