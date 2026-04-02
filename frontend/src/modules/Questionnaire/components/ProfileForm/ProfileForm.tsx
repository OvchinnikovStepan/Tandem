import { useForm, FormProvider } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { useSetAtom } from "jotai";
import { useTranslation } from "react-i18next";
import Button from "@/ui/Button";
import { ProfileFormFields } from "./ProfileFormFields";
import { useSubmitQuestionnaire } from "@/modules/Questionnaire/hooks/useSubmitQuestionnaire";
import {
    profileSchema,
    type ProfileFormValues,
} from "@/modules/Questionnaire/schemas/profileSchema";
import { questionnaireStepperAtom } from "@/modules/Questionnaire/atoms/questionnaireStepperAtom";

const DEFAULT_VALUES: ProfileFormValues = {
    firstName: "",
    lastName: "",
    city: "",
    birthDate: "",
    gender: "no-select",
};

function ProfileForm() {
    const { t } = useTranslation();
    const setSelectedForm = useSetAtom(questionnaireStepperAtom);
    const { saveQuestionnaire, isPending, error } = useSubmitQuestionnaire();

    const methods = useForm<ProfileFormValues>({
        resolver: zodResolver(profileSchema),
        defaultValues: DEFAULT_VALUES,
    });

    const onSubmit = (data: ProfileFormValues) => saveQuestionnaire(data);

    return (
        <div className="flex flex-col justify-between h-full gap-4 2lg:gap-0">
            <div className="flex flex-col items-center relative">
                <FormProvider {...methods}>
                    <form
                        id="profile-form"
                        onSubmit={methods.handleSubmit(onSubmit)}
                        className="w-lg"
                        autoComplete="off"
                    >
                        <ProfileFormFields disabled={isPending} />
                    </form>
                </FormProvider>
                {error && (
                    <div className="absolute -top-5.75 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm font-medium font-roboto">
                        {error instanceof Error
                            ? error.message
                            : t("questionnaire.profile.errors.save-error")}
                    </div>
                )}
            </div>
            <div className="flex justify-end gap-4 mr-16 mb-8 2xl:mr-32 2xl:mb-19.5">
                <Button
                    onClick={() => setSelectedForm(1)}
                    disabled={isPending}
                    variant="action"
                    className="h-11.25 w-25 font-medium text-md leading-5"
                >
                    {t("questionnaire.buttons.back")}
                </Button>
                <Button
                    type="submit"
                    form="profile-form"
                    disabled={isPending}
                    className="h-11.25 w-25"
                >
                    {isPending
                        ? t("questionnaire.buttons.saving")
                        : t("questionnaire.buttons.finish")}
                </Button>
            </div>
        </div>
    );
}

export default ProfileForm;
