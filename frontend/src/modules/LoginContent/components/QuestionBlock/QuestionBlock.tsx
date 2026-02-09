import { Button } from "@/ui/button";
import { Input } from "@/ui/input";
import { PasswordToggle } from "@/ui/password-toggle";
import { LinkButton } from "@/ui/link-button";
import { Mail, Lock } from "lucide-react";
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
                variant="form"
                leftIcon
                placeholder="name@company.com"
                {...register("email")}
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
                variant="form"
                leftIcon
                rightIcon
                placeholder=""
                {...register("password")}
              />
              <PasswordToggle
                showPassword={showPassword}
                onClick={() => setShowPassword(!showPassword)}
              />
            </div>
            {errors.password && (
              <p className="text-xs text-red-600">{errors.password.message}</p>
            )}
          </div>

          <div className="flex justify-end">
            <LinkButton
              onClick={() => navigate("/forgot-password")}
              className="text-sm"
            >
              Забыли пароль?
            </LinkButton>
          </div>

          <Button
            type="submit"
            variant="primary"
            size="xl"
            disabled={isSubmitting}
          >
            {isSubmitting ? "Вход..." : "Войти"}
          </Button>
        </form>

        <div className="mt-8 text-center">
          <p className="text-gray-600">
            Нет аккаунта?{" "}
            <LinkButton
              onClick={() => navigate("/register")}
              className="font-bold"
            >
              Зарегистрироваться
            </LinkButton>
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
