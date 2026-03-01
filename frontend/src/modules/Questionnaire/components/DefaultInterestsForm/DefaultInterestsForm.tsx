import Button from "@/ui/Button.tsx";
import DefaultInterestsList from "@/modules/Questionnaire/components/DefaultInterestsList/DefaultInterestsList.tsx";
import { useAtom } from "jotai";
import { questionnaireStepperAtom } from "@/modules/Questionnaire/atoms/questionnaireStepperAtom";
import { useTranslation } from "react-i18next";

function DefaultInterestsForm() {
    const [, setSelectedForm] = useAtom(questionnaireStepperAtom);
    const { t } = useTranslation();

    return (
        <div className="flex flex-col h-full justify-between">
            <div className="w-full items-center justify-center">
                <DefaultInterestsList />
            </div>
            <div className="flex justify-end mr-32 mb-19.5">
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
