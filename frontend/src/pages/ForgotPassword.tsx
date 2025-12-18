import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { EmailStep, CodeStep, NewPasswordStep } from "@/modules/ForgotPasswordContent";
import HeroBlock from "@/components/HeroBlock";
import Header from "@/components/Header";

const emailSchema = z.object({
  email: z.string().email("Введите корректный email"),
});

const codeSchema = z.object({
  code: z.string().length(6, "Код должен содержать 6 цифр"),
});

const newPasswordSchema = z.object({
  password: z.string().min(8, "Минимум 8 символов"),
  confirmPassword: z.string(),
}).refine((data) => data.password === data.confirmPassword, {
  message: "Пароли не совпадают",
  path: ["confirmPassword"],
});

type EmailFormData = z.infer<typeof emailSchema>;
type CodeFormData = z.infer<typeof codeSchema>;
type NewPasswordFormData = z.infer<typeof newPasswordSchema>;

export default function ForgotPassword() {
  const [step, setStep] = useState(1);
  const [email, setEmail] = useState("");
  const navigate = useNavigate();

  const emailForm = useForm<EmailFormData>({
    resolver: zodResolver(emailSchema),
  });

  const codeForm = useForm<CodeFormData>({
    resolver: zodResolver(codeSchema),
  });

  const newPasswordForm = useForm<NewPasswordFormData>({
    resolver: zodResolver(newPasswordSchema),
  });

  const onEmailSubmit = async (data: EmailFormData) => {
    console.log("Email for password reset:", data);
    setEmail(data.email);
    setStep(2);
  };

  const onCodeSubmit = async (data: CodeFormData) => {
    console.log("Code verification:", data);
    setStep(3);
  };

  const onNewPasswordSubmit = async (data: NewPasswordFormData) => {
    console.log("New password set:", data);
    navigate("/login");
  };

  const handleResendCode = () => {
    console.log("Resending code to:", email);
  };

  const getStepTitle = () => {
    switch (step) {
      case 1:
        return "Восстановить пароль";
      case 2:
        return "Введите код";
      case 3:
        return "Создать новый пароль";
      default:
        return "Восстановить пароль";
    }
  };

  const getStepDescription = () => {
    switch (step) {
      case 1:
        return "Введите ваш email для подтверждения";
      default:
        return "";
    }
  };

  return (
    <div className="min-h-screen bg-white">
      <Header onBack={step > 1 ? () => setStep(step - 1) : undefined} closeUrl="/login" />
      
      <div className="flex h-[calc(100vh-73px)]">
        <HeroBlock variant="forgot-password" />

        <div className="w-full lg:w-1/2 flex items-center justify-center p-6 sm:p-8">
          <div className="w-full max-w-md">
            <div className="mb-8">
              <h1 className="text-4xl font-bold text-black mb-2">
                {getStepTitle()}
              </h1>
              <p className="text-gray-600">
                {getStepDescription()}
              </p>
            </div>

            {step === 1 && (
              <form onSubmit={emailForm.handleSubmit(onEmailSubmit)}>
                <EmailStep
                  register={emailForm.register}
                  errors={emailForm.formState.errors}
                  isSubmitting={emailForm.formState.isSubmitting}
                />
              </form>
            )}

            {step === 2 && (
              <form onSubmit={codeForm.handleSubmit(onCodeSubmit)}>
                <CodeStep
                  setValue={codeForm.setValue}
                  errors={codeForm.formState.errors}
                  isSubmitting={codeForm.formState.isSubmitting}
                  email={email}
                  onResendCode={handleResendCode}
                />
              </form>
            )}

            {step === 3 && (
              <form onSubmit={newPasswordForm.handleSubmit(onNewPasswordSubmit)}>
                <NewPasswordStep
                  register={newPasswordForm.register}
                  errors={newPasswordForm.formState.errors}
                  isSubmitting={newPasswordForm.formState.isSubmitting}
                  password={newPasswordForm.watch("password") || ""}
                />
              </form>
            )}
          </div>
        </div>
      </div>
    </div>
  );
}
