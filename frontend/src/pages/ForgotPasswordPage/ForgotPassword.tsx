import { useState } from "react";
import { useNavigate } from "react-router";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import {
    EmailStep,
    CodeStep,
    NewPasswordStep,
} from "@/modules/ForgotPasswordContent";
import HeroBlock from "@/components/HeroBlock/HeroBlock";
import Header from "@/components/Header/Header";
import { useTranslation } from "react-i18next";
import { useAuthActions } from "@/modules/Auth/hooks/useAuthActions";

type EmailFormData = {
    email: string;
};

type CodeFormData = {
    code: string;
};

type NewPasswordFormData = {
    password: string;
    confirmPassword: string;
};

export default function ForgotPassword() {
    const [step, setStep] = useState(1);
    const [email, setEmail] = useState("");
    const [code, setCode] = useState("");
    const [serverError, setServerError] = useState<string | null>(null);
    const navigate = useNavigate();
    const { forgotPassword, resetPassword } = useAuthActions();
    const { t } = useTranslation();

    const emailSchema = z.object({
        email: z.email(t("validation.invalidEmail")),
    });

    const codeSchema = z.object({
        code: z.string().length(6, t("validation.codeLength")),
    });

    const newPasswordSchema = z
        .object({
            password: z.string().min(8, t("validation.minPasswordShort")),
            confirmPassword: z.string(),
        })
        .refine((data) => data.password === data.confirmPassword, {
            message: t("validation.passwordsMismatch"),
            path: ["confirmPassword"],
        });

    const emailForm = useForm<EmailFormData>({
        resolver: zodResolver(emailSchema),
    });

    const codeForm = useForm<CodeFormData>({
        resolver: zodResolver(codeSchema),
    });

    const newPasswordForm = useForm<NewPasswordFormData>({
        resolver: zodResolver(newPasswordSchema),
    });

    const onEmailSubmit = emailForm.handleSubmit(async (data) => {
        setServerError(null);
        try {
            await forgotPassword(data.email);
            setEmail(data.email);
            setStep(2);
        } catch {
            setServerError(t("forgotPassword.errors.send"));
        }
    });

    const onCodeSubmit = codeForm.handleSubmit((data) => {
        setCode(data.code);
        setStep(3);
    });

    const onNewPasswordSubmit = newPasswordForm.handleSubmit(async (data) => {
        setServerError(null);
        try {
            await resetPassword(email, code, data.password);
            navigate("/login");
        } catch {
            setServerError(t("forgotPassword.errors.reset"));
        }
    });

    const handleResendCode = async () => {
        if (!email) return;
        setServerError(null);
        try {
            await forgotPassword(email);
        } catch {
            setServerError(t("forgotPassword.errors.send"));
        }
    };

    const getStepTitle = () => {
        switch (step) {
            case 1:
                return t("forgotPassword.titleStep1");
            case 2:
                return t("forgotPassword.titleStep2");
            case 3:
                return t("forgotPassword.titleStep3");
            default:
                return t("forgotPassword.titleStep1");
        }
    };

    const getStepDescription = () => {
        switch (step) {
            case 1:
                return t("forgotPassword.subtitleStep1");
            default:
                return "";
        }
    };

    return (
        <div className="min-h-screen bg-white">
            <Header
                onBack={step > 1 ? () => setStep(step - 1) : undefined}
                closeUrl="/login"
            />

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
                            <form onSubmit={onEmailSubmit}>
                                <EmailStep
                                    register={emailForm.register}
                                    errors={emailForm.formState.errors}
                                    isSubmitting={
                                        emailForm.formState.isSubmitting
                                    }
                                    serverError={serverError}
                                />
                            </form>
                        )}

                        {step === 2 && (
                            <form onSubmit={onCodeSubmit}>
                                <CodeStep
                                    setValue={codeForm.setValue}
                                    errors={codeForm.formState.errors}
                                    isSubmitting={
                                        codeForm.formState.isSubmitting
                                    }
                                    email={email}
                                    onResendCode={handleResendCode}
                                />
                            </form>
                        )}

                        {step === 3 && (
                            <form onSubmit={onNewPasswordSubmit}>
                                <NewPasswordStep
                                    register={newPasswordForm.register}
                                    errors={newPasswordForm.formState.errors}
                                    isSubmitting={
                                        newPasswordForm.formState.isSubmitting
                                    }
                                    password={
                                        newPasswordForm.watch("password") || ""
                                    }
                                    serverError={serverError}
                                />
                            </form>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}
