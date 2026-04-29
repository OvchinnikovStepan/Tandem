import { AuthButton as Button } from "@/ui/auth/AuthButton";
import { AuthInput as Input } from "@/ui/auth/AuthInput";
import { Mail, Lock } from "lucide-react";
import type { UseFormRegister, FieldErrors } from "react-hook-form";
import { useTranslation } from "react-i18next";

interface CredentialsFormData {
  email: string;
  password: string;
  confirmPassword: string;
}

interface CredentialsStepProps {
  register: UseFormRegister<CredentialsFormData>;
  errors: FieldErrors<CredentialsFormData>;
  isSubmitting: boolean;
  password: string;
  passwordStrength: number;
  serverError?: string | null;
}

export default function CredentialsStep({
  register,
  errors,
  isSubmitting,
  password,
  passwordStrength,
  serverError,
}: CredentialsStepProps) {
  const { t } = useTranslation();

  return (
    <div className="space-y-5">
      <div className="space-y-2">
        <label
          htmlFor="email"
          className="block text-sm font-medium text-gray-900"
        >
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
            autoComplete="email"
            {...register("email")}
          />
        </div>
        {errors.email && (
          <p className="text-xs text-red-600">{errors.email.message}</p>
        )}
      </div>

      <div>
        <div className="relative">
          <Lock className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <Input
            id="password"
            type="password"
            variant="form"
            leftIcon
            placeholder={t("common.password")}
            autoComplete="new-password"
            {...register("password")}
          />
        </div>
        {errors.password && (
          <p className="text-xs text-red-600 mt-1">{errors.password.message}</p>
        )}
      </div>

      {password && (
        <div className="flex gap-1 py-2">
          <div
            className={`h-1.5 flex-1 rounded-full ${passwordStrength >= 1 ? "bg-red-500" : "bg-gray-200"}`}
          />
          <div
            className={`h-1.5 flex-1 rounded-full ${passwordStrength >= 2 ? "bg-orange-500" : "bg-gray-200"}`}
          />
          <div
            className={`h-1.5 flex-1 rounded-full ${passwordStrength >= 3 ? "bg-yellow-400" : "bg-gray-200"}`}
          />
          <div
            className={`h-1.5 flex-1 rounded-full ${passwordStrength >= 4 ? "bg-lime-500" : "bg-gray-200"}`}
          />
          <div
            className={`h-1.5 flex-1 rounded-full ${passwordStrength >= 5 ? "bg-green-500" : "bg-gray-200"}`}
          />
        </div>
      )}

      <div>
        <div className="relative">
          <Lock className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <Input
            id="confirmPassword"
            type="password"
            variant="form"
            leftIcon
            placeholder={t("common.confirmPassword")}
            autoComplete="new-password"
            {...register("confirmPassword")}
          />
        </div>
        {errors.confirmPassword && (
          <p className="text-xs text-red-600 mt-1">
            {errors.confirmPassword.message}
          </p>
        )}
      </div>

      {serverError && <p className="text-xs text-red-600">{serverError}</p>}

      <Button type="submit" variant="primary" size="xl" disabled={isSubmitting}>
        {isSubmitting ? t("register.submitting") : t("register.submit")}
      </Button>
    </div>
  );
}
