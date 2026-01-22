import { Button } from "@/ui/button";
import { Input } from "@/ui/input";
import { User, Mail } from "lucide-react";
import { UseFormRegister, FieldErrors } from "react-hook-form";

interface FormData {
  firstName: string;
  lastName: string;
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
        <label htmlFor="firstName" className="block text-sm font-medium text-gray-900">
          Имя
        </label>
        <div className="relative">
          <User className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <Input
            id="firstName"
            type="text"
            variant="form"
            leftIcon
            placeholder="John"
            {...register("firstName")}
          />
        </div>
        {errors.firstName && (
          <p className="text-xs text-red-600">{errors.firstName.message}</p>
        )}
      </div>

      <div className="space-y-2">
        <label htmlFor="lastName" className="block text-sm font-medium text-gray-900">
          Фамилия
        </label>
        <div className="relative">
          <User className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <Input
            id="lastName"
            type="text"
            variant="form"
            leftIcon
            placeholder="Doe"
            {...register("lastName")}
          />
        </div>
        {errors.lastName && (
          <p className="text-xs text-red-600">{errors.lastName.message}</p>
        )}
      </div>

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
