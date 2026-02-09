import { Button } from "@/ui/button";
import { Input } from "@/ui/input";
import { Mail } from "lucide-react";
import { UseFormRegister, FieldErrors } from "react-hook-form";

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
  return (
    <div className="space-y-5">
      <div className="space-y-2">
        <label htmlFor="email" className="block text-sm font-medium text-gray-900">
          Email
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
        Далее
      </Button>
    </div>
  );
}
