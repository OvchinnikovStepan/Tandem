import { Button } from "@/ui/button";
import { Input } from "@/ui/input";
import { User } from "lucide-react";
import { UseFormRegister, FieldErrors } from "react-hook-form";
import { useTranslation } from "react-i18next";

interface FormData {
  email: string;
  password: string;
  confirmPassword: string;
}

interface QuestionBlockProps {
  register: UseFormRegister<FormData>;
  errors: FieldErrors<FormData>;
  isSubmitting: boolean;
  password: string;
  passwordStrength: number;
}

export default function QuestionBlock({ 
  register, 
  errors, 
  isSubmitting, 
  password, 
  passwordStrength 
}: QuestionBlockProps) {
  const { t } = useTranslation();

  return (
    <div className="space-y-5">
      <div>
        <div className="relative">
          <User className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <Input
            id="password"
            type="password"
            variant="form"
            leftIcon
            placeholder={t("common.password")}
            {...register("password")}
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
          <User className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <Input
            id="confirmPassword"
            type="password"
            variant="form"
            leftIcon
            placeholder={t("common.confirmPassword")}
            {...register("confirmPassword")}
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
        {isSubmitting ? t("register.submitting") : t("register.submit")}
      </Button>
    </div>
  );
}
