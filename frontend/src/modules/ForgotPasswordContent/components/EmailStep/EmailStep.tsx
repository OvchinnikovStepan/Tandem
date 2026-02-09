import { Button } from "@/ui/button";
import { Input } from "@/ui/input";
import { Mail } from "lucide-react";
import { UseFormRegister, FieldErrors } from "react-hook-form";

interface EmailFormData {
  email: string;
}

interface EmailStepProps {
  register: UseFormRegister<EmailFormData>;
  errors: FieldErrors<EmailFormData>;
  isSubmitting: boolean;
}

export default function EmailStep({ register, errors, isSubmitting }: EmailStepProps) {
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
            placeholder="email"
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
        {isSubmitting ? "Отправка..." : "Подтвердить email"}
      </Button>
    </div>
  );
}
