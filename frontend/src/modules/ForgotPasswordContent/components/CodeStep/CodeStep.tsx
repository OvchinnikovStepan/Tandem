import { AuthButton as Button } from "@/ui/auth/AuthButton";
import { LinkButton } from "@/ui/auth/LinkButton";
import { useRef, useState, useEffect } from "react";
import type { UseFormSetValue, FieldErrors } from "react-hook-form";
import { useTranslation } from "react-i18next";

interface CodeFormData {
    code: string;
}

interface CodeStepProps {
    setValue: UseFormSetValue<CodeFormData>;
    errors: FieldErrors<CodeFormData>;
    isSubmitting: boolean;
    email: string;
    onResendCode: () => void;
}

export default function CodeStep({
    setValue,
    errors,
    isSubmitting,
    email,
    onResendCode,
}: CodeStepProps) {
    const { t } = useTranslation();
    const [digits, setDigits] = useState<string[]>(["", "", "", "", "", ""]);
    const inputRefs = useRef<(HTMLInputElement | null)[]>([]);

    useEffect(() => {
        setValue("code", digits.join(""));
    }, [digits, setValue]);

    const handleChange = (index: number, value: string) => {
        if (!/^\d*$/.test(value)) return;

        const newDigits = [...digits];
        newDigits[index] = value.slice(-1);
        setDigits(newDigits);

        if (value && index < 5) {
            inputRefs.current[index + 1]?.focus();
        }
    };

    const handleKeyDown = (
        index: number,
        e: React.KeyboardEvent<HTMLInputElement>,
    ) => {
        if (e.key === "Backspace" && !digits[index] && index > 0) {
            inputRefs.current[index - 1]?.focus();
        }
    };

    const handlePaste = (e: React.ClipboardEvent) => {
        e.preventDefault();
        const pastedData = e.clipboardData
            .getData("text")
            .replace(/\D/g, "")
            .slice(0, 6);
        const newDigits = [...digits];
        for (let i = 0; i < pastedData.length; i++) {
            newDigits[i] = pastedData[i];
        }
        setDigits(newDigits);
        inputRefs.current[Math.min(pastedData.length, 5)]?.focus();
    };

    return (
        <div className="space-y-5">
            <div className="p-4 bg-yellow-50 border border-yellow-200 rounded-xl">
                <p className="text-sm text-gray-700">
                    {t("forgotPassword.codeSent")}{" "}
                    <span className="font-semibold text-black">{email}</span>
                </p>
            </div>

            <div className="space-y-2">
                <label className="block text-sm font-medium text-gray-900">
                    {t("forgotPassword.codeLabel")}
                </label>
                <div className="flex gap-1 justify-between">
                    {digits.map((digit, index) => (
                        <input
                            key={index}
                            ref={(el) => {
                                inputRefs.current[index] = el;
                            }}
                            type="text"
                            inputMode="numeric"
                            maxLength={1}
                            value={digit}
                            onChange={(e) =>
                                handleChange(index, e.target.value)
                            }
                            onKeyDown={(e) => handleKeyDown(index, e)}
                            onPaste={handlePaste}
                            className="w-12 h-14 text-center text-2xl font-bold bg-gray-50 border border-gray-200 text-black rounded-xl focus:bg-white focus:border-blue-500 focus:ring-2 focus:ring-blue-500/20 outline-none transition-all"
                        />
                    ))}
                </div>
                {errors.code && (
                    <p className="text-xs text-red-600">
                        {errors.code.message}
                    </p>
                )}
            </div>

            <Button
                type="submit"
                variant="primary"
                size="xl"
                disabled={isSubmitting}
            >
                {isSubmitting
                    ? t("forgotPassword.submittingCode")
                    : t("forgotPassword.submitCode")}
            </Button>

            <div className="text-center">
                <p className="text-sm text-gray-600">
                    {t("forgotPassword.noCode")}{" "}
                    <LinkButton onClick={onResendCode}>
                        {t("forgotPassword.resendCode")}
                    </LinkButton>
                </p>
            </div>
        </div>
    );
}
