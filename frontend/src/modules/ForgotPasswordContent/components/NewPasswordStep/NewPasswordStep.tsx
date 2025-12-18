import { Button } from "@/ui/button";
import { Input } from "@/ui/input";
import { Lock, Eye, EyeOff } from "lucide-react";
import { useState, useMemo } from "react";
import { UseFormRegister, FieldErrors } from "react-hook-form";

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

  return (
    <div className="space-y-5">
      <div>
        <div className="relative">
          <Lock className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <Input
            id="password"
            type={showPassword ? "text" : "password"}
            placeholder="Пароль"
            {...register("password")}
            className="pl-12 pr-12 h-12 bg-gray-50 border-gray-200 text-black placeholder:text-gray-400 rounded-xl focus:bg-white focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20"
          />
          <button
            type="button"
            onClick={() => setShowPassword(!showPassword)}
            className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
          >
            {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
          </button>
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
            placeholder="Повторите пароль"
            {...register("confirmPassword")}
            className="pl-12 pr-12 h-12 bg-gray-50 border-gray-200 text-black placeholder:text-gray-400 rounded-xl focus:bg-white focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20"
          />
          <button
            type="button"
            onClick={() => setShowConfirmPassword(!showConfirmPassword)}
            className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
          >
            {showConfirmPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
          </button>
        </div>
        {errors.confirmPassword && (
          <p className="text-xs text-red-600 mt-1">{errors.confirmPassword.message}</p>
        )}
      </div>

      <Button
        type="submit"
        disabled={isSubmitting}
        className="w-full h-12 bg-yellow-400 hover:bg-yellow-500 disabled:bg-gray-300 disabled:cursor-not-allowed text-black font-bold text-lg rounded-xl transition-all duration-200 transform hover:scale-[0.98] active:scale-95 shadow-md hover:shadow-lg"
      >
        {isSubmitting ? "Сохранение..." : "Установить новый пароль"}
      </Button>
    </div>
  );
}
