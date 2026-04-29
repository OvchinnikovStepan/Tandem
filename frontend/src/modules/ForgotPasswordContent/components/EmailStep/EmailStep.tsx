import { AuthButton as Button } from "@/ui/auth/AuthButton";
import { AuthInput as Input } from "@/ui/auth/AuthInput";
import { Mail } from "lucide-react";
import { UseFormRegister, FieldErrors } from "react-hook-form";
import { useTranslation } from "react-i18next";

interface EmailFormData {
  email: string;
}

interface EmailStepProps {
  register: UseFormRegister<EmailFormData>;
  errors: FieldErrors<EmailFormData>;
  isSubmitting: boolean;
}

export default function EmailStep({ register, errors, isSubmitting }: EmailStepProps) {
  const { t } = useTranslation();

  return (
    <div className="space-y-5">
      <div className="space-y-2">
        <div className="relative">
          <Mail className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <Input
            id="email"
            type="email"
            variant="form"
            leftIcon
            placeholder={t("forgotPassword.emailPlaceholder")}
            {...register("email")}
          />
        </div>
        {errors.email && (
          <p className="text-xs text-red-600">{errors.email.message}</p>
        )}
      </div>

      <Button
        type="submit"
        variant="primary"
        size="xl"
        disabled={isSubmitting}
      >
        {isSubmitting ? t("forgotPassword.submittingEmail") : t("forgotPassword.submitEmail")}
      </Button>
    </div>
  );
}
