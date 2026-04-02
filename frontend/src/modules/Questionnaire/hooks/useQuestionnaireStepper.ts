import { useAtom } from "jotai";
import { questionnaireStepperAtom } from "@/modules/Questionnaire/atoms/questionnaireStepperAtom";

export function useQuestionnaireStepper() {
    const [selectedForm] = useAtom(questionnaireStepperAtom);
    return { selectedForm };
}
