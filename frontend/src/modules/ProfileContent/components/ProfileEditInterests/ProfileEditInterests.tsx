import { useEffect, useMemo } from "react";
import { Plus } from "lucide-react";
import { SearchInput, Tag, PageTitle, Divider, Button } from "@/ui";
import { Controller, useForm } from "react-hook-form";
import { useTranslation } from "react-i18next";

interface Interest {
  id: string;
  label: string;
}

interface ProfileEditInterestsProps {
  interests?: Interest[];
  onSave: (interests: Interest[]) => void;
  onBack: () => void;
}

interface InterestsFormData {
  searchQuery: string;
  selectedInterests: Interest[];
}

const defaultInterestKeys = [
  { id: "1", key: "basketball" },
  { id: "2", key: "movies" },
  { id: "3", key: "football" },
  { id: "4", key: "games" },
  { id: "5", key: "development" },
  { id: "6", key: "music" },
] as const;

export default function ProfileEditInterests({
  interests,
  onSave,
  onBack: _onBack,
}: ProfileEditInterestsProps) {
  const { t } = useTranslation();
  const resolvedInterests = useMemo(
    () =>
      interests ??
      defaultInterestKeys.map((item) => ({
        id: item.id,
        label: t(`profile.interests.defaults.${item.key}`),
      })),
    [interests, t]
  );

  const methods = useForm<InterestsFormData>({
    defaultValues: {
      searchQuery: "",
      selectedInterests: resolvedInterests,
    },
  });
  const selectedInterests = methods.watch("selectedInterests");

  useEffect(() => {
    methods.reset({
      searchQuery: "",
      selectedInterests: resolvedInterests,
    });
  }, [resolvedInterests, methods]);

  const handleRemoveInterest = (id: string) => {
    const updatedInterests = methods
      .getValues("selectedInterests")
      .filter((interest) => interest.id !== id);
    methods.setValue("selectedInterests", updatedInterests);
  };

  const handleSave = methods.handleSubmit((data) => {
    onSave(data.selectedInterests);
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
        style={{ paddingTop: "26px" }}
      >
        <PageTitle>{t("profile.interests.title")}</PageTitle>
      </div>

      <div
        className="flex items-center justify-center gap-4"
        style={{ paddingTop: "24px", paddingBottom: "24px" }}
      >

        <Controller
          name="searchQuery"
          control={methods.control}
          render={({ field }) => (
            <SearchInput
              placeholder={t("profile.interests.searchPlaceholder")}
              {...field}
            />
          )}
        />

        <Button
          variant="primary"
          className="w-[138px] h-10 rounded-xl gap-1.5"
        >
          <span>{t("profile.interests.add")}</span>
          <Plus className="w-5 h-5 text-heading-black" />
        </Button>
      </div>

      <div className="px-5">
        <Divider />
      </div>

      <div
        className="flex justify-center items-start"
        style={{ padding: "20px" }}
      >
        <div className="grid grid-cols-2 gap-x-6 gap-y-4 place-items-center">
          {selectedInterests.map((interest) => (
            <Tag
              key={interest.id}
              label={interest.label}
              onRemove={() => handleRemoveInterest(interest.id)}
              removeIcon="trash"
            />
          ))}
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
          onClick={handleSave}
          className="w-[100px] h-[45px] rounded-lg text-[15px] font-medium"
        >
          {t("profile.interests.save")}
        </Button>
      </div>
    </div>
  );
}
