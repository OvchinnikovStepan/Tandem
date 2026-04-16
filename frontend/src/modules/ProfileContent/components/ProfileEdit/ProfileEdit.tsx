import { useEffect } from "react";
import { Camera, Calendar } from "lucide-react";
import { Input, Select, Textarea, PageTitle, Divider, Button, Avatar } from "@/ui";
import { FormField } from "@/components";
import { getDefaultAvatarUrl } from "@/lib/avatar";
import { Controller, FormProvider, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";

interface UserProfile {
  id: string;
  firstName: string;
  lastName: string;
  username: string;
  gender: string;
  birthDate: string;
  bio: string;
  city: string;
  avatar?: string;
}

interface ProfileEditProps {
  user: UserProfile;
  onSave: (user: UserProfile) => void;
  onCancel: () => void;
}

export default function ProfileEdit({ user, onSave, onCancel: _onCancel }: ProfileEditProps) {
  const { t } = useTranslation();
  const methods = useForm<UserProfile>({ defaultValues: user });
  const avatar = methods.watch("avatar");

  useEffect(() => {
    methods.reset(user);
  }, [methods, user]);

  const handleSubmit = methods.handleSubmit((data) => {
    onSave(data);
  });

  return (
    <FormProvider {...methods}>
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
        style={{ paddingTop: "26px" }}
      >
        <PageTitle>{t("profile.edit.title")}</PageTitle>
      </div>

      <div
        className="relative flex justify-center"
        style={{ padding: "24px 20px", minHeight: "176px" }}
      >
        <div
          className="absolute cursor-pointer group"
          style={{ left: "70px", top: "50%", transform: "translateY(-50%)" }}
        >
          <Avatar src={avatar || getDefaultAvatarUrl(200)} alt="Avatar" size="xl" />
          <div className="absolute inset-0 bg-black/40 flex items-center justify-center opacity-0 group-hover:opacity-100 transition-opacity rounded-full">
            <Camera className="w-8 h-8 text-white" />
          </div>
        </div>

        <div
          className="flex flex-col items-end gap-[18px]"
          style={{ width: "100%", maxWidth: "400px" }}
        >
          <FormField label={t("profile.edit.firstName")} name="firstName">
            <Controller
              name="firstName"
              control={methods.control}
              render={({ field }) => (
                <Input variant="profile" type="text" className="w-[326px] font-bold" {...field} />
              )}
            />
          </FormField>

          <FormField label={t("profile.edit.lastName")} name="lastName">
            <Controller
              name="lastName"
              control={methods.control}
              render={({ field }) => (
                <Input variant="profile" type="text" className="w-[326px] font-bold" {...field} />
              )}
            />
          </FormField>
        </div>
      </div>

      <div className="px-5">
        <Divider />
      </div>

      <div
        className="flex flex-col justify-center items-center flex-1"
        style={{
          padding: "39px 20px",
          gap: "16px",
        }}
      >
        <div
          className="flex flex-col justify-center items-end gap-[18px]"
          style={{ width: "100%", maxWidth: "400px" }}
        >

          <FormField label={t("profile.edit.gender")} name="gender">
            <Controller
              name="gender"
              control={methods.control}
              render={({ field }) => (
                <Select variant="profile" className="w-[326px] font-bold" {...field}>
                  <option value="male">{t("profile.edit.male")}</option>
                  <option value="female">{t("profile.edit.female")}</option>
                  <option value="other">{t("profile.edit.other")}</option>
                </Select>
              )}
            />
          </FormField>

          <FormField label={t("profile.edit.birthDate")} name="birthDate">
            <Controller
              name="birthDate"
              control={methods.control}
              render={({ field }) => (
                <Input
                  variant="profile"
                  type="text"
                  placeholder={t("profile.edit.birthDatePlaceholder")}
                  icon={<Calendar className="w-5 h-5 text-heading-black" />}
                  className="w-[326px]"
                  {...field}
                />
              )}
            />
          </FormField>

          <FormField label={t("profile.edit.username")} name="username">
            <Controller
              name="username"
              control={methods.control}
              render={({ field }) => (
                <Input variant="profile" type="text" className="w-[326px] font-bold" {...field} />
              )}
            />
          </FormField>

          <FormField label={t("profile.edit.bio")} labelAlign="start" name="bio">
            <Controller
              name="bio"
              control={methods.control}
              render={({ field }) => (
                <Textarea
                  variant="profile"
                  placeholder={t("profile.edit.bioPlaceholder")}
                  className="w-[326px] h-[104px]"
                  {...field}
                />
              )}
            />
          </FormField>

          <FormField label={t("profile.edit.city")} name="city">
            <Controller
              name="city"
              control={methods.control}
              render={({ field }) => (
                <Input variant="profile" type="text" className="w-[326px] font-bold" {...field} />
              )}
            />
          </FormField>
        </div>
      </div>

      <div className="px-5">
        <Divider />
      </div>

      <div
        className="flex justify-center"
        style={{ padding: "24px 0" }}
      >
        <Button
          variant="action"
          onClick={handleSubmit}
          className="w-[100px] h-[45px] rounded-lg text-[15px] font-medium"
        >
          {t("profile.edit.save")}
        </Button>
      </div>
      </div>
    </FormProvider>
  );
}
