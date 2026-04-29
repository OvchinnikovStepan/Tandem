import { useState, useMemo } from "react";
import { useNavigate } from "react-router-dom";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import {
  PhoneStep,
  CodeStep,
  CredentialsStep,
} from "@/modules/RegisterContent";
import HeroBlock from "@/components/HeroBlock/HeroBlock";
import Header from "@/components/Header/Header";
import { LinkButton } from "@/ui/link-button";
import { useTranslation } from "react-i18next";
import { registerPhone, verifyPhone, registerEmail } from "@/api/auth";
import { ApiError } from "@/api/client";
import { setAuthTokens } from "@/lib/auth-storage";

type PhoneFormData = { phoneNumber: string };
type CodeFormData = { code: string };
type CredentialsFormData = {
  email: string;
  password: string;
  confirmPassword: string;
};

const TOTAL_STEPS = 3;

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
  const [verificationId, setVerificationId] = useState<string | null>(null);
  const [phoneNumber, setPhoneNumber] = useState("");
  const [serverError, setServerError] = useState<string | null>(null);
  const navigate = useNavigate();
  const { t } = useTranslation();

  const phoneSchema = z.object({
    phoneNumber: z
      .string()
      .trim()
      .min(5, t("validation.invalidPhone"))
      .regex(/^\+?[0-9 ()-]+$/, t("validation.invalidPhone")),
  });

  const codeSchema = z.object({
    code: z.string().length(6, t("validation.codeLength")),
  });

  const credentialsSchema = z
    .object({
      email: z.string().email(t("validation.invalidEmail")),
      password: z.string().min(8, t("validation.minPassword")),
      confirmPassword: z.string(),
    })
    .refine((data) => data.password === data.confirmPassword, {
      message: t("validation.passwordsMismatch"),
      path: ["confirmPassword"],
    });

  const phoneForm = useForm<PhoneFormData>({
    resolver: zodResolver(phoneSchema),
    mode: "onChange",
  });

  const codeForm = useForm<CodeFormData>({
    resolver: zodResolver(codeSchema),
  });

  const credentialsForm = useForm<CredentialsFormData>({
    resolver: zodResolver(credentialsSchema),
    mode: "onChange",
  });

  const password = credentialsForm.watch("password", "");
  const passwordStrength = useMemo(
    () => calculatePasswordStrength(password),
    [password],
  );

  const handlePhoneSubmit = phoneForm.handleSubmit(async (data) => {
    setServerError(null);
    try {
      const trimmed = data.phoneNumber.trim();
      const res = await registerPhone(trimmed);
      setVerificationId(res.verificationId);
      setPhoneNumber(trimmed);
      setStep(2);
    } catch (e) {
      const message =
        e instanceof ApiError ? e.message : t("register.errors.unknown");
      setServerError(message);
    }
  });

  const handleCodeSubmit = codeForm.handleSubmit(async (data) => {
    if (!verificationId) {
      setServerError(t("register.errors.unknown"));
      return;
    }
    setServerError(null);
    try {
      await verifyPhone(verificationId, data.code);
      setStep(3);
    } catch (e) {
      const message =
        e instanceof ApiError ? e.message : t("register.errors.invalidCode");
      setServerError(message);
    }
  });

  const handleCredentialsSubmit = credentialsForm.handleSubmit(async (data) => {
    if (!verificationId) {
      setServerError(t("register.errors.unknown"));
      return;
    }
    setServerError(null);
    try {
      const result = await registerEmail(
        verificationId,
        data.email,
        data.password,
      );
      setAuthTokens(result.accessToken, result.refreshToken);
      navigate("/");
    } catch (e) {
      const message =
        e instanceof ApiError ? e.message : t("register.errors.unknown");
      setServerError(message);
    }
  });

  const handleResendCode = async () => {
    if (!phoneNumber) return;
    setServerError(null);
    try {
      const res = await registerPhone(phoneNumber);
      setVerificationId(res.verificationId);
    } catch (e) {
      const message =
        e instanceof ApiError ? e.message : t("register.errors.unknown");
      setServerError(message);
    }
  };

  const handleBack = () => {
    setServerError(null);
    setStep((s) => Math.max(1, s - 1));
  };

  const getStepTitle = () => {
    switch (step) {
      case 1:
        return t("register.titlePhone");
      case 2:
        return t("register.titleCode");
      case 3:
        return t("register.titleCredentials");
      default:
        return t("register.titlePhone");
    }
  };

  return (
    <div className="min-h-screen bg-white">
      <Header onBack={step > 1 ? handleBack : undefined} closeUrl="/" />

      <div className="flex h-[calc(100vh-73px)]">
        <HeroBlock variant="register" />

        <div className="w-full lg:w-1/2 flex items-center justify-center p-6 sm:p-8">
          <div className="w-full max-w-md">
            <div className="mb-8">
              <h1 className="text-4xl font-bold text-black mb-2">
                {getStepTitle()}
              </h1>
              <p className="text-gray-600">
                {t("register.step", { step, total: TOTAL_STEPS })}
              </p>
            </div>

            <div className="mb-8 flex gap-1">
              {Array.from({ length: TOTAL_STEPS }).map((_, i) => (
                <div
                  key={i}
                  className={`h-1 flex-1 rounded-full ${
                    step >= i + 1 ? "bg-yellow-400" : "bg-gray-200"
                  }`}
                />
              ))}
            </div>

            {step === 1 && (
              <form onSubmit={handlePhoneSubmit}>
                <PhoneStep
                  register={phoneForm.register}
                  errors={phoneForm.formState.errors}
                  isSubmitting={phoneForm.formState.isSubmitting}
                  serverError={serverError}
                />
              </form>
            )}

            {step === 2 && (
              <form onSubmit={handleCodeSubmit}>
                <CodeStep
                  setValue={codeForm.setValue}
                  errors={codeForm.formState.errors}
                  isSubmitting={codeForm.formState.isSubmitting}
                  phoneNumber={phoneNumber}
                  onResendCode={handleResendCode}
                  serverError={serverError}
                />
              </form>
            )}

            {step === 3 && (
              <form onSubmit={handleCredentialsSubmit}>
                <CredentialsStep
                  register={credentialsForm.register}
                  errors={credentialsForm.formState.errors}
                  isSubmitting={credentialsForm.formState.isSubmitting}
                  password={password}
                  passwordStrength={passwordStrength}
                  serverError={serverError}
                />
              </form>
            )}

            <div className="mt-8 text-center">
              <p className="text-gray-600">
                {t("register.haveAccount")}{" "}
                <LinkButton
                  onClick={() => navigate("/login")}
                  className="font-bold"
                >
                  {t("register.login")}
                </LinkButton>
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
