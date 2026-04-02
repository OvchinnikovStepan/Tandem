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
            <div className="bg-accent-white 2xl:my-8 2xl:mx-6 2lg:my-4 2lg:mx-3 w-full 2lg:rounded-3xl shadow-default flex flex-col">
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
