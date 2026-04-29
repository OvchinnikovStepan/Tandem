import { AuthButton as Button } from "@/ui/auth/AuthButton";
import { AuthInput as Input } from "@/ui/auth/AuthInput";
import { Mail } from "lucide-react";
import type { UseFormRegister, FieldErrors } from "react-hook-form";
import { useTranslation } from "react-i18next";

interface FormData {
  email: string;
  password: string;
  confirmPassword: string;
}

interface FeaturesBlockProps {
  register: UseFormRegister<FormData>;
  errors: FieldErrors<FormData>;
  onNext: () => void;
}

export default function FeaturesBlock({ register, errors, onNext }: FeaturesBlockProps) {
  const { t } = useTranslation();

  return (
    <div className="space-y-5">
      <div className="space-y-2">
        <label htmlFor="email" className="block text-sm font-medium text-gray-900">
          {t("common.email")}
        </label>
        <div className="relative">
          <Mail className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <Input
            id="email"
            type="email"
            variant="form"
            leftIcon
            placeholder="john@company.com"
            {...register("email")}
          />
        </div>
        {errors.email && (
          <p className="text-xs text-red-600">{errors.email.message}</p>
        )}
      </div>

      <Button
        type="button"
        variant="primary"
        size="xl"
        onClick={onNext}
      >
        {t("register.next")}
      </Button>
    </div>
  );
}
