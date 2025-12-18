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
            placeholder="John"
            {...register("firstName")}
            className="pl-12 h-12 bg-gray-50 border-gray-200 text-black placeholder:text-gray-400 rounded-xl focus:bg-white focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20"
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
            placeholder="Doe"
            {...register("lastName")}
            className="pl-12 h-12 bg-gray-50 border-gray-200 text-black placeholder:text-gray-400 rounded-xl focus:bg-white focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20"
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
            placeholder="john@company.com"
            {...register("email")}
            className="pl-12 h-12 bg-gray-50 border-gray-200 text-black placeholder:text-gray-400 rounded-xl focus:bg-white focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20"
          />
        </div>
        {errors.email && (
          <p className="text-xs text-red-600">{errors.email.message}</p>
        )}
      </div>

      <Button
        type="button"
        onClick={onNext}
        className="w-full h-12 bg-yellow-400 hover:bg-yellow-500 text-black font-bold text-lg rounded-xl transition-all duration-200 transform hover:scale-[0.98] active:scale-95 shadow-md hover:shadow-lg"
      >
        Далее
      </Button>
    </div>
  );
}
