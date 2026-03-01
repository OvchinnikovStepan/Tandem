import {
    DefaultInterestsForm,
    EditInterestsForm,
    ProfileForm,
    useQuestionnaireStepper,
} from "@/modules/Questionnaire";
import QuestionnaireHeader from "@/pages/QuestionnairePage/components/QuestionnairePageHeader/QuestionnaireHeader.tsx";

export default function Questionnaire() {
    const { selectedForm } = useQuestionnaireStepper();

    return (
        <div className="min-h-screen bg-landing-bg flex">
            <div className="bg-accent-white my-8 mx-6 w-full rounded-3xl shadow-default flex flex-col">
                <QuestionnaireHeader />
                {selectedForm === 0 ? (
                    <DefaultInterestsForm />
                ) : selectedForm === 1 ? (
                    <EditInterestsForm />
                ) : (
                    <ProfileForm />
                )}
            </div>
        </div>
    );
}
