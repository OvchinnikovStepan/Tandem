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
            placeholder="email"
            {...register("email")}
            className="pl-12 h-12 bg-gray-50 border-gray-200 text-black placeholder:text-gray-400 rounded-xl focus:bg-white focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20"
          />
        </div>
        {errors.email && (
          <p className="text-xs text-red-600">{errors.email.message}</p>
        )}
      </div>

      <Button
        type="submit"
        disabled={isSubmitting}
        className="w-full h-12 bg-yellow-400 hover:bg-yellow-500 disabled:bg-gray-300 disabled:cursor-not-allowed text-black font-bold text-lg rounded-xl transition-all duration-200 transform hover:scale-[0.98] active:scale-95 shadow-md hover:shadow-lg"
      >
        {isSubmitting ? "Отправка..." : "Подтвердить email"}
      </Button>
    </div>
  );
}
