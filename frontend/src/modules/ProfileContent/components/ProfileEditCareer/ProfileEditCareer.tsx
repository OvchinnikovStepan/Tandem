import { useEffect } from "react";
import { Input, Select, PageTitle, Divider, Button } from "@/ui";
import { FormField } from "@/components";
import { Controller, FormProvider, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";

interface CareerData {
  workplace: string;
  startYear: string;
  endYear: string;
  position: string;
}

interface ProfileEditCareerProps {
  career?: CareerData;
  onSave: (career: CareerData) => void;
  onBack: () => void;
}

const defaultCareer: CareerData = {
  workplace: "",
  startYear: "",
  endYear: "",
  position: "",
};

const years = Array.from({ length: 76 }, (_, i) => String(2026 - i));

export default function ProfileEditCareer({
  career = defaultCareer,
  onSave,
  onBack: _onBack,
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
      className="bg-[#FEFEFE] overflow-hidden w-full"
      style={{
        boxShadow: "0px 2px 8px rgba(0, 0, 0, 0.25)",
        maxWidth: "975px",
        borderRadius: "24px",
        height: "504px",
        position: "relative",
      }}
    >

      <div
        className="absolute"
        style={{ left: "50%", transform: "translateX(-50%)", top: "26px" }}
      >
        <PageTitle>{t("profile.career.title")}</PageTitle>
      </div>

      <div className="absolute" style={{ width: "935px", left: "20px", top: "113px" }}>
        <Divider />
      </div>

      <div
        className="absolute flex flex-col justify-center items-center"
        style={{
          width: "935px",
          height: "292px",
          left: "20px",
          top: "114px",
          padding: "39px 0",
        }}
      >
        <div
          className="flex flex-col justify-center items-end gap-[18px]"
          style={{ width: "935px", paddingRight: "263px" }}
        >

          <FormProvider {...methods}>
            <FormField label={t("profile.career.workplace")} name="workplace">
              <Controller
                name="workplace"
                control={methods.control}
                render={({ field }) => (
                  <Input
                    variant="profile"
                    type="text"
                    placeholder={t("profile.career.workplacePlaceholder")}
                    className="w-[326px]"
                    {...field}
                  />
                )}
              />
            </FormField>

            <FormField label={t("profile.career.startYear")} name="startYear">
              <Controller
                name="startYear"
                control={methods.control}
                render={({ field }) => (
                  <Select variant="profile" className="w-[326px]" {...field}>
                    <option value="">{t("profile.career.notSelected")}</option>
                    {years.map((year) => (
                      <option key={year} value={year}>
                        {year}
                      </option>
                    ))}
                  </Select>
                )}
              />
            </FormField>

            <FormField label={t("profile.career.endYear")} name="endYear">
              <Controller
                name="endYear"
                control={methods.control}
                render={({ field }) => (
                  <Select variant="profile" className="w-[326px]" {...field}>
                    <option value="">{t("profile.career.notSelected")}</option>
                    {years.map((year) => (
                      <option key={year} value={year}>
                        {year}
                      </option>
                    ))}
                  </Select>
                )}
              />
            </FormField>

            <FormField label={t("profile.career.position")} name="position">
              <Controller
                name="position"
                control={methods.control}
                render={({ field }) => (
                  <Input variant="profile" type="text" className="w-[326px]" {...field} />
                )}
              />
            </FormField>
          </FormProvider>
        </div>
      </div>

      <div className="absolute" style={{ width: "935px", left: "20px", top: "406px" }}>
        <Divider />
      </div>

      <div
        className="absolute"
        style={{ left: "50%", transform: "translateX(-50%)", top: "433px" }}
      >
        <Button
          variant="primary"
          onClick={handleSave}
          className="w-[100px] h-[45px] rounded-lg text-[15px]"
        >
          {t("profile.career.save")}
        </Button>
      </div>
    </div>
  );
}
