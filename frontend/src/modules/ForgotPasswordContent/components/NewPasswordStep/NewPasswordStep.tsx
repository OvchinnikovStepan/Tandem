import { AuthButton as Button } from "@/ui/auth/AuthButton";
import { AuthInput as Input } from "@/ui/auth/AuthInput";
import { PasswordToggle } from "@/ui/auth/PasswordToggle";
import { Lock } from "lucide-react";
import { useState, useMemo } from "react";
import type { UseFormRegister, FieldErrors } from "react-hook-form";
import { useTranslation } from "react-i18next";

interface NewPasswordFormData {
  password: string;
  confirmPassword: string;
}

interface NewPasswordStepProps {
  register: UseFormRegister<NewPasswordFormData>;
  errors: FieldErrors<NewPasswordFormData>;
  isSubmitting: boolean;
  password: string;
}

const calculatePasswordStrength = (pwd: string) => {
  let strength = 0;
  if (pwd.length >= 8) strength++;
  if (pwd.length >= 12) strength++;
  if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) strength++;
  if (/\d/.test(pwd)) strength++;
  if (/[^a-zA-Z\d]/.test(pwd)) strength++;
  return Math.min(strength, 5);
};

export default function NewPasswordStep({ register, errors, isSubmitting, password }: NewPasswordStepProps) {
  const [showPassword, setShowPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const passwordStrength = useMemo(() => calculatePasswordStrength(password), [password]);
  const { t } = useTranslation();

  return (
    <div className="space-y-5">
      <div>
        <div className="relative">
          <Lock className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <Input
            id="password"
            type={showPassword ? "text" : "password"}
            variant="form"
            leftIcon
            rightIcon
            placeholder={t("common.password")}
            {...register("password")}
          />
          <PasswordToggle
            showPassword={showPassword}
            onClick={() => setShowPassword(!showPassword)}
          />
        </div>
        {errors.password && (
          <p className="text-xs text-red-600 mt-1">{errors.password.message}</p>
        )}
      </div>

      {password && (
        <div className="flex gap-1 py-2">
          <div className={`h-1.5 flex-1 rounded-full ${passwordStrength >= 1 ? "bg-red-500" : "bg-gray-200"}`} />
          <div className={`h-1.5 flex-1 rounded-full ${passwordStrength >= 2 ? "bg-orange-500" : "bg-gray-200"}`} />
          <div className={`h-1.5 flex-1 rounded-full ${passwordStrength >= 3 ? "bg-yellow-400" : "bg-gray-200"}`} />
          <div className={`h-1.5 flex-1 rounded-full ${passwordStrength >= 4 ? "bg-lime-500" : "bg-gray-200"}`} />
          <div className={`h-1.5 flex-1 rounded-full ${passwordStrength >= 5 ? "bg-green-500" : "bg-gray-200"}`} />
        </div>
      )}

      <div>
        <div className="relative">
          <Lock className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <Input
            id="confirmPassword"
            type={showConfirmPassword ? "text" : "password"}
            variant="form"
            leftIcon
            rightIcon
            placeholder={t("common.confirmPassword")}
            {...register("confirmPassword")}
          />
          <PasswordToggle
            showPassword={showConfirmPassword}
            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
          />
        </div>
        {errors.confirmPassword && (
          <p className="text-xs text-red-600 mt-1">{errors.confirmPassword.message}</p>
        )}
      </div>

      <Button
        type="submit"
        variant="primary"
        size="xl"
        disabled={isSubmitting}
      >
        {isSubmitting ? t("forgotPassword.submittingNewPassword") : t("forgotPassword.submitNewPassword")}
      </Button>
    </div>
  );
}
