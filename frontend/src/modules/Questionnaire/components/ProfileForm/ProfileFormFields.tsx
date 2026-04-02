import { useFormContext, Controller } from "react-hook-form";
import { useTranslation } from "react-i18next";
import { formatDate } from "@/utils/date.ts";
import {
    FORM_TEXT_FIELDS,
    GENDER_OPTIONS,
} from "@/modules/Questionnaire/constants/constants.ts";
import { Calendar } from "lucide-react";
import { Input } from "@/ui/Input";
import type { ProfileFormValues } from "@/modules/Questionnaire/schemas/profileSchema";
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/ui/Select.tsx";
import { Field, FieldError, FieldLabel } from "@/ui/Field.tsx";

export function ProfileFormFields({ disabled }: { disabled: boolean }) {
    const { t } = useTranslation();
    const {
        register,
        setValue,
        formState: { errors },
        control,
    } = useFormContext<ProfileFormValues>();

    const handleKeyDown =
        (name: string) => (e: React.KeyboardEvent<HTMLInputElement>) => {
            if (name !== "city") return;

            const isAllowedChar = /[a-zA-Zа-яА-ЯёЁ\s-]/.test(e.key);
            if (e.key.length === 1 && !isAllowedChar) {
                e.preventDefault();
            }
        };

    return (
        <div className="border border-accent-gray rounded-3xl 2xl:p-10 p-7.5 space-y-0.5 shadow-default">
            {FORM_TEXT_FIELDS.map(({ name, required }) => {
                const kebabCaseName = name
                    .replace(/([A-Z])/g, "-$1")
                    .toLowerCase();

                return (
                    <Field key={name}>
                        <FieldLabel aria-required={required} htmlFor={name}>
                            {t(`questionnaire.profile.${kebabCaseName}.title`)}
                        </FieldLabel>
                        <Input
                            id={name}
                            placeholder={t(
                                `questionnaire.profile.${kebabCaseName}.placeholder`,
                            )}
                            disabled={disabled}
                            onKeyDown={handleKeyDown(name)}
                            {...register(name)}
                        />
                        <FieldError>
                            {t(errors[name]?.message as string)}
                        </FieldError>
                    </Field>
                );
            })}

            <Field>
                <FieldLabel htmlFor="birthDate">
                    {t("questionnaire.profile.birth-date.title")}
                </FieldLabel>
                <div className="relative">
                    <Input
                        id="birthDate"
                        {...register("birthDate")}
                        placeholder={t(
                            "questionnaire.profile.birth-date.placeholder",
                        )}
                        maxLength={10}
                        disabled={disabled}
                        onChange={(e) => {
                            setValue("birthDate", formatDate(e.target.value), {
                                shouldValidate: true,
                            });
                        }}
                    />
                    <Calendar
                        className="absolute right-3 top-1/2 -translate-y-1/2 text-heading-black size-5 pointer-events-none"
                        aria-hidden
                    />
                </div>
                <FieldError>
                    {errors.birthDate && t(errors.birthDate.message!)}
                </FieldError>
            </Field>

            <Field>
                <FieldLabel htmlFor="gender">
                    {t("questionnaire.profile.gender.title")}
                </FieldLabel>
                <Controller
                    name="gender"
                    control={control}
                    render={({ field: { value, onChange } }) => (
                        <Select
                            value={value}
                            onValueChange={onChange}
                            disabled={disabled}
                            name="gender"
                        >
                            <SelectTrigger id="gender">
                                <SelectValue />
                            </SelectTrigger>
                            <SelectContent position="popper">
                                <SelectGroup>
                                    {GENDER_OPTIONS.map((optionId) => (
                                        <SelectItem
                                            key={optionId}
                                            value={optionId}
                                        >
                                            {t(
                                                `questionnaire.profile.gender.options.${optionId}`,
                                            )}
                                        </SelectItem>
                                    ))}
                                </SelectGroup>
                            </SelectContent>
                        </Select>
                    )}
                />
            </Field>
        </div>
    );
}
