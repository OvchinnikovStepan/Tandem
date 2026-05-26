import { useMemo, useState } from "react";
import { useNavigate } from "react-router";
import { useForm } from "react-hook-form";
import { zodResolver } from "@hookform/resolvers/zod";
import { z } from "zod";
import { CredentialsStep } from "@/modules/RegisterContent";
import HeroBlock from "@/components/HeroBlock/HeroBlock";
import Header from "@/components/Header/Header";
import { LinkButton } from "@/ui/auth/LinkButton";
import { useTranslation } from "react-i18next";
import { useAuthActions } from "@/modules/Auth/hooks/useAuthActions";

type CredentialsFormData = {
    email: string;
    password: string;
    confirmPassword: string;
};

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
    const [serverError, setServerError] = useState<string | null>(null);
    const navigate = useNavigate();
    const { register: registerUser } = useAuthActions();
    const { t } = useTranslation();

    const credentialsSchema = z
        .object({
            email: z.email(t("validation.invalidEmail")),
            password: z.string().min(8, t("validation.minPassword")),
            confirmPassword: z.string(),
        })
        .refine((data) => data.password === data.confirmPassword, {
            message: t("validation.passwordsMismatch"),
            path: ["confirmPassword"],
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

    const handleCredentialsSubmit = credentialsForm.handleSubmit(
        async (data) => {
            setServerError(null);
            try {
                await registerUser(data.email, data.password);
                navigate("/questionnaire");
            } catch {
                setServerError(t("register.errors.unknown"));
            }
        },
    );

    return (
        <div className="min-h-screen bg-white">
            <Header closeUrl="/" />

            <div className="flex h-[calc(100vh-73px)]">
                <HeroBlock variant="register" />

                <div className="w-full lg:w-1/2 flex items-center justify-center p-6 sm:p-8">
                    <div className="w-full max-w-md">
                        <div className="mb-8">
                            <h1 className="text-4xl font-bold text-black mb-2">
                                {t("register.titleCredentials")}
                            </h1>
                        </div>

                        <form onSubmit={handleCredentialsSubmit}>
                            <CredentialsStep
                                register={credentialsForm.register}
                                errors={credentialsForm.formState.errors}
                                isSubmitting={
                                    credentialsForm.formState.isSubmitting
                                }
                                password={password}
                                passwordStrength={passwordStrength}
                                serverError={serverError}
                            />
                        </form>

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
