import { useEffect } from "react";
import { FormField } from "@/components/FormField";
import { Controller, FormProvider, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { PageTitle } from "@/ui/page-title";
import { Input } from "@/ui/Input.tsx";
import { Divider } from "@/ui/divider.tsx";
import Button from "@/ui/Button.tsx";

interface CareerData {
    workplace: string;
    startYear: string;
    endYear: string;
    position: string;
}

interface ProfileEditCareerProps {
    career?: CareerData;
    onSave: (career: CareerData) => void;
    onBack?: () => void;
}

const defaultCareer: CareerData = {
    workplace: "",
    startYear: "",
    endYear: "",
    position: "",
};

const currentYear = new Date().getFullYear();
const years = Array.from({ length: 76 }, (_, i) => String(currentYear - i));

export default function ProfileEditCareer({
    career = defaultCareer,
    onSave,
}: ProfileEditCareerProps) {
    const { t } = useTranslation();
    const methods = useForm<CareerData>({ defaultValues: career });

    useEffect(() => {
        methods.reset(career);
    }, [career, methods]);

    const handleSave = methods.handleSubmit((data) => {
        onSave(data);
    });

    return (
        <div
            className="bg-accent-white overflow-hidden w-full flex flex-col"
            style={{
                boxShadow: "0px 2px 8px rgba(0, 0, 0, 0.25)",
                maxWidth: "975px",
                borderRadius: "24px",
            }}
        >
            <div
                className="flex justify-center"
                style={{ paddingTop: "26px", paddingBottom: "24px" }}
            >
                <PageTitle>{t("profile.career.title")}</PageTitle>
            </div>

            <div className="px-5">
                <Divider />
            </div>

            <div
                className="flex flex-col justify-center items-center"
                style={{
                    padding: "39px 20px",
                }}
            >
                <div
                    className="flex flex-col justify-center items-end gap-[18px]"
                    style={{ width: "100%", maxWidth: "400px" }}
                >
                    <FormProvider {...methods}>
                        <FormField
                            label={t("profile.career.workplace")}
                            name="workplace"
                        >
                            <Controller
                                name="workplace"
                                control={methods.control}
                                render={({ field }) => (
                                    <Input
                                        type="text"
                                        placeholder={t(
                                            "profile.career.workplacePlaceholder",
                                        )}
                                        className="w-[326px]"
                                        {...field}
                                    />
                                )}
                            />
                        </FormField>

                        <FormField
                            label={t("profile.career.startYear")}
                            name="startYear"
                        >
                            <Controller
                                name="startYear"
                                control={methods.control}
                                render={({ field }) => (
                                    <select className="w-[326px]" {...field}>
                                        <option value="">
                                            {t("profile.career.notSelected")}
                                        </option>
                                        {years.map((year) => (
                                            <option key={year} value={year}>
                                                {year}
                                            </option>
                                        ))}
                                    </select>
                                )}
                            />
                        </FormField>

                        <FormField
                            label={t("profile.career.endYear")}
                            name="endYear"
                        >
                            <Controller
                                name="endYear"
                                control={methods.control}
                                render={({ field }) => (
                                    <select className="w-[326px]" {...field}>
                                        <option value="">
                                            {t("profile.career.notSelected")}
                                        </option>
                                        {years.map((year) => (
                                            <option key={year} value={year}>
                                                {year}
                                            </option>
                                        ))}
                                    </select>
                                )}
                            />
                        </FormField>

                        <FormField
                            label={t("profile.career.position")}
                            name="position"
                        >
                            <Controller
                                name="position"
                                control={methods.control}
                                render={({ field }) => (
                                    <Input
                                        type="text"
                                        className="w-[326px]"
                                        {...field}
                                    />
                                )}
                            />
                        </FormField>
                    </FormProvider>
                </div>
            </div>

            <div className="px-5">
                <Divider />
            </div>

            <div className="flex justify-center" style={{ padding: "24px 0" }}>
                <Button
                    variant="action"
                    onClick={handleSave}
                    className="w-[100px] h-[45px] rounded-lg text-[15px] font-medium"
                >
                    {t("profile.career.save")}
                </Button>
            </div>
        </div>
    );
}
