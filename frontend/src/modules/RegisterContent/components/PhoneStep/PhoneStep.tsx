import { Button } from "@/ui/button";
import { Input } from "@/ui/input";
import { Phone } from "lucide-react";
import { UseFormRegister, FieldErrors } from "react-hook-form";
import { useTranslation } from "react-i18next";

interface PhoneFormData {
  phoneNumber: string;
}

interface PhoneStepProps {
  register: UseFormRegister<PhoneFormData>;
  errors: FieldErrors<PhoneFormData>;
  isSubmitting: boolean;
  serverError?: string | null;
}

export default function PhoneStep({
  register,
  errors,
  isSubmitting,
  serverError,
}: PhoneStepProps) {
  const { t } = useTranslation();

  return (
    <div className="space-y-5">
      <div className="space-y-2">
        <label
          htmlFor="phoneNumber"
          className="block text-sm font-medium text-gray-900"
        >
          {t("register.phoneLabel")}
        </label>
        <div className="relative">
          <Phone className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <Input
            id="phoneNumber"
            type="tel"
            variant="form"
            leftIcon
            placeholder={t("register.phonePlaceholder")}
            autoComplete="tel"
            {...register("phoneNumber")}
          />
        </div>
        {errors.phoneNumber && (
          <p className="text-xs text-red-600">{errors.phoneNumber.message}</p>
        )}
        {serverError && (
          <p className="text-xs text-red-600">{serverError}</p>
        )}
      </div>

      <Button type="submit" variant="primary" size="xl" disabled={isSubmitting}>
        {isSubmitting ? t("register.sendingCode") : t("register.next")}
      </Button>
    </div>
  );
}
