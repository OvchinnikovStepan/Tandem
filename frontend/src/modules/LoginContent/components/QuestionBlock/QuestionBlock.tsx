import { Button } from "@/ui/button";
import { Input } from "@/ui/input";
import { Eye, EyeOff, Mail, Lock } from "lucide-react";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";

const loginSchema = z.object({
  email: z.string().email("Введите корректный email"),
  password: z.string().min(8, "Пароль должен содержать минимум 8 символов"),
});

type LoginFormData = z.infer<typeof loginSchema>;

export default function QuestionBlock() {
  const [showPassword, setShowPassword] = useState(false);
  const navigate = useNavigate();
  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<LoginFormData>({
    resolver: zodResolver(loginSchema),
  });

  const onSubmit = async (data: LoginFormData) => {
    console.log("Login attempt:", data);
  };

  return (
    <div className="w-full lg:w-1/2 flex items-center justify-center p-6 sm:p-8">
      <div className="w-full max-w-md">
        <div className="mb-8">
          <h1 className="text-4xl font-bold text-black mb-2">
            Вход в Tandem
          </h1>
          <p className="text-gray-600">
            Введите свои данные для доступа к аккаунту
          </p>
        </div>

        <form onSubmit={handleSubmit(onSubmit)} className="space-y-5">
          <div className="space-y-2">
            <label htmlFor="email" className="block text-sm font-medium text-gray-900">
              Email
            </label>
            <div className="relative">
              <Mail className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <Input
                id="email"
                type="email"
                placeholder="name@company.com"
                {...register("email")}
                className="pl-12 h-12 bg-gray-50 border-gray-200 text-black placeholder:text-gray-400 rounded-xl focus:bg-white focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20"
              />
            </div>
            {errors.email && (
              <p className="text-xs text-red-600">{errors.email.message}</p>
            )}
          </div>

          <div className="space-y-2">
            <label htmlFor="password" className="block text-sm font-medium text-gray-900">
              Пароль
            </label>
            <div className="relative">
              <Lock className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
              <Input
                id="password"
                type={showPassword ? "text" : "password"}
                placeholder=""
                {...register("password")}
                className="pl-12 pr-12 h-12 bg-gray-50 border-gray-200 text-black placeholder:text-gray-400 rounded-xl focus:bg-white focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20"
              />
              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className="absolute right-4 top-1/2 -translate-y-1/2 text-gray-400 hover:text-gray-600 transition-colors"
              >
                {showPassword ? (
                  <EyeOff className="w-5 h-5" />
                ) : (
                  <Eye className="w-5 h-5" />
                )}
              </button>
            </div>
            {errors.password && (
              <p className="text-xs text-red-600">{errors.password.message}</p>
            )}
          </div>

          <div className="flex justify-end">
            <button
              type="button"
              onClick={() => navigate("/forgot-password")}
              className="text-sm text-blue-600 hover:text-blue-700 font-medium transition-colors"
            >
              Забыли пароль?
            </button>
          </div>

          <Button
            type="submit"
            disabled={isSubmitting}
            className="w-full h-12 bg-yellow-400 hover:bg-yellow-500 disabled:bg-gray-300 disabled:cursor-not-allowed text-black font-bold text-lg rounded-xl transition-all duration-200 transform hover:scale-[0.98] active:scale-95 shadow-md hover:shadow-lg"
          >
            {isSubmitting ? "Вход..." : "Войти"}
          </Button>
        </form>

        <div className="mt-8 text-center">
          <p className="text-gray-600">
            Нет аккаунта?{" "}
            <button
              onClick={() => navigate("/register")}
              className="text-blue-600 hover:text-blue-700 font-bold transition-colors"
            >
              Зарегистрироваться
            </button>
          </p>
        </div>

        <div className="mt-8 pt-8 border-t border-gray-200">
          <p className="text-xs text-gray-500 text-center">
            Входя в систему, вы соглашаетесь с Условиями использования и Политикой конфиденциальности
          </p>
        </div>
      </div>
    </div>
  );
}
