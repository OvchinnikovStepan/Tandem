import { useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { FeaturesBlock, QuestionBlock } from "@/modules/RegisterContent";
import HeroBlock from "@/components/HeroBlock";
import Header from "@/components/Header";
import { LinkButton } from "@/ui/link-button";

const formSchema = z
  .object({
    email: z.string().email("Введите корректный email"),
    password: z.string().min(8, "Пароль должен содержать минимум 8 символов"),
    confirmPassword: z.string(),
  })
  .refine((data) => data.password === data.confirmPassword, {
    message: "Пароли не совпадают",
    path: ["confirmPassword"],
  });

type FormData = z.infer<typeof formSchema>;

const calculatePasswordStrength = (pwd: string) => {
  let strength = 0;
  if (pwd.length >= 8) strength++;
  if (pwd.length >= 12) strength++;
  if (/[a-z]/.test(pwd) && /[A-Z]/.test(pwd)) strength++;
  if (/\d/.test(pwd)) strength++;
  if (/[^a-zA-Z\d]/.test(pwd)) strength++;
  return Math.min(strength, 5);
};

export default function Register() {
  const [step, setStep] = useState(1);
  const navigate = useNavigate();

  const {
    register,
    handleSubmit,
    watch,
    trigger,
    formState: { errors, isSubmitting },
  } = useForm<FormData>({
    resolver: zodResolver(formSchema),
    mode: "onChange",
  });

  const password = watch("password", "");
  const passwordStrength = useMemo(() => calculatePasswordStrength(password), [password]);

  const handleNextStep = async () => {
    const isValid = await trigger(["email"]);
    if (isValid) {
      setStep(2);
    }
  };

  const onSubmit = handleSubmit(async (data) => {
    console.log("Registration:", data);
  });

  return (
    <div className="min-h-screen bg-white">
      <Header onBack={step === 2 ? () => setStep(1) : undefined} closeUrl="/" />
      
      <div className="flex h-[calc(100vh-73px)]">
        <HeroBlock variant="register" />

        <div className="w-full lg:w-1/2 flex items-center justify-center p-6 sm:p-8">
          <div className="w-full max-w-md">
            <div className="mb-8">
              <h1 className="text-4xl font-bold text-black mb-2">
                {step === 1 ? "Create account" : "Придумайте пароль"}
              </h1>
              <p className="text-gray-600">
                Шаг {step} из 2
              </p>
            </div>

            <div className="mb-8 flex gap-1">
              <div className={`h-1 flex-1 rounded-full ${step >= 1 ? "bg-yellow-400" : "bg-gray-200"}`} />
              <div className={`h-1 flex-1 rounded-full ${step >= 2 ? "bg-yellow-400" : "bg-gray-200"}`} />
            </div>

            {step === 1 ? (
              <FeaturesBlock 
                register={register}
                errors={errors}
                onNext={handleNextStep}
              />
            ) : (
              <form onSubmit={onSubmit}>
                <QuestionBlock
                  register={register}
                  errors={errors}
                  isSubmitting={isSubmitting}
                  password={password}
                  passwordStrength={passwordStrength}
                />
              </form>
            )}

            <div className="mt-8 text-center">
              <p className="text-gray-600">
                Уже есть аккаунт?{" "}
                <LinkButton
                  onClick={() => navigate("/login")}
                  className="font-bold"
                >
                  Войти
                </LinkButton>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
