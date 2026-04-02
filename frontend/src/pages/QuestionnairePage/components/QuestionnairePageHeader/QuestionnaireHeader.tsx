import { useQuestionnaireStepper } from "@/modules/Questionnaire";
import { useTranslation } from "react-i18next";

export default function QuestionnaireHeader() {
    const { selectedForm } = useQuestionnaireStepper();
    const { t } = useTranslation();

    return (
        <div className="2xl:mb-11 2lg:mb-7 mb-5 text-center w-full 2xl:pt-10 2lg:pt-7 pt-4">
            <div className="2xl:mb-7.25 mb-3.625 flex relative items-center">
                <img
                    src="/logo/Black_Logo.svg"
                    alt="Tandem"
                    draggable="false"
                    className="size-16 ml-12.5"
                />
                <h1 className="text-heading leading-12 font-roboto font-bold content-center text-heading-black absolute left-1/2 -translate-x-1/2">
                    {t("questionnaire.header.title")}
                </h1>
            </div>
            <p className="text-base leading-6 font-roboto font-normal text-base-black whitespace-pre-line">
                {selectedForm === 0
                    ? t("questionnaire.header.description.default")
                    : selectedForm === 1
                      ? t("questionnaire.header.description.edit")
                      : t("questionnaire.header.description.profile")}
            </p>
        </div>
    );
}
