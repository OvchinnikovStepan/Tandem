import { useQuestionnaireStepper } from "@/modules/Questionnaire";
import { useTranslation } from "react-i18next";

export default function QuestionnaireHeader() {
    const { selectedForm } = useQuestionnaireStepper();
    const { t } = useTranslation();

    return (
        <div className="mb-11 text-center w-full pt-10">
            <div className="mb-7.25 flex relative items-center">
                <img
                    src="/logo/Black_Logo.svg"
                    alt="Tandem"
                    draggable="false"
                    className="size-16 ml-12.5"
                />
                <h1 className="text-[2.75rem] leading-12 font-roboto font-bold content-center text-heading-black absolute left-1/2 -translate-x-1/2">
                    {t("questionnaire.header.title")}
                </h1>
            </div>
            <p className="text-[1rem] leading-6 font-roboto font-normal text-base-black whitespace-pre-line">
                {selectedForm === 0
                    ? t("questionnaire.header.description.default")
                    : selectedForm === 1
                      ? t("questionnaire.header.description.edit")
                      : t("questionnaire.header.description.profile")}
            </p>
        </div>
    );
}
