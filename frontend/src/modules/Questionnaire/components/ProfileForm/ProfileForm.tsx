import { useState, useEffect } from "react";
import { useNavigate } from "react-router";
import Button from "@/ui/Button.tsx";
import { Input } from "@/ui/Input.tsx";
import { Label } from "@/ui/Label.tsx";
import { Calendar } from "lucide-react";
import { saveUserProfile } from "@/api/profile";
import type { ProfileData } from "@/api/profile";
import { useAtom } from "jotai";
import { questionnaireStepperAtom } from "@/modules/Questionnaire/atoms/questionnaireStepperAtom";
import { useTranslation } from "react-i18next";

function ProfileForm() {
    const [formData, setFormData] = useState({
        firstName: "",
        lastName: "",
        city: "",
        birthDate: "",
        gender: "" as "" | "male" | "female",
    });
    const [interests, setInterests] = useState<string[]>([]);
    const [isSaving, setIsSaving] = useState(false);
    const [error, setError] = useState<string | null>(null);
    const [, setSelectedForm] = useAtom(questionnaireStepperAtom);
    const navigate = useNavigate();
    const { t } = useTranslation();

    useEffect(() => {
        const savedInterests = localStorage.getItem("userInterests");
        if (savedInterests) {
            try {
                const parsedInterests = JSON.parse(savedInterests);
                setInterests(
                    Array.isArray(parsedInterests) ? parsedInterests : [],
                );
            } catch (err) {
                console.error("Failed to parse saved interests:", err);
            }
        }
    }, []);

    const handleChange = (field: string, value: string) => {
        setFormData((prev) => ({ ...prev, [field]: value }));
    };

    const formatDate = (value: string) => {
        const numbers = value.replace(/\D/g, "");
        if (numbers.length <= 2) return numbers;
        if (numbers.length <= 4)
            return `${numbers.slice(0, 2)}/${numbers.slice(2)}`;
        return `${numbers.slice(0, 2)}/${numbers.slice(2, 4)}/${numbers.slice(
            4,
            8,
        )}`;
    };

    const handleDateChange = (value: string) => {
        const formatted = formatDate(value);
        handleChange("birthDate", formatted);
    };

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        if (!formData.firstName.trim() || !formData.lastName.trim()) {
            setError("Пожалуйста, заполните обязательные поля");
            return;
        }

        setIsSaving(true);
        setError(null);

        try {
            // Потом userId будет получен из контекста авторизации
            const userId = localStorage.getItem("userId") || "temp-user-id";

            const profileData: ProfileData = {
                firstName: formData.firstName.trim(),
                lastName: formData.lastName.trim(),
                city: formData.city.trim() || undefined,
                birthDate: formData.birthDate.trim() || undefined,
                gender: formData.gender || undefined,
                interests: interests,
            };

            await saveUserProfile(userId, profileData);

            // Очищаем временные данные
            localStorage.removeItem("userInterests");

            // После успешного сохранения переходим на главную страницу
            navigate("/");
        } catch (err) {
            setError(
                err instanceof Error
                    ? err.message
                    : "Ошибка сохранения профиля",
            );
            console.error("Failed to save profile:", err);
        } finally {
            setIsSaving(false);
        }
    };

    const handleBack = () => {
        // Сохраняем текущие данные формы в localStorage на случай возврата
        localStorage.setItem("profileFormData", JSON.stringify(formData));
        setSelectedForm(1);
    };

    // Загружаем сохраненные данные формы при возврате
    useEffect(() => {
        const savedFormData = localStorage.getItem("profileFormData");
        if (savedFormData) {
            try {
                const parsed = JSON.parse(savedFormData);
                setFormData((prev) => ({ ...prev, ...parsed }));
                localStorage.removeItem("profileFormData");
            } catch (err) {
                console.error("Failed to parse saved form data:", err);
            }
        }
    }, []);

    return (
        <>
            <div className="flex flex-col justify-center items-center">
                {error && (
                    <div className="mb-4 p-3 bg-red-50 border border-red-200 rounded-lg text-red-700 text-sm">
                        {error}
                    </div>
                )}
                {/* Карточка с формой */}
                <form
                    id="profile-form"
                    onSubmit={handleSubmit}
                    className="mb-6 w-md"
                >
                    <div className="bg-white border border-gray-200 rounded-lg p-6 space-y-6">
                        <div className="space-y-2">
                            <Label
                                htmlFor="firstName"
                                className="text-gray-800"
                            >
                                {t("questionnaire.profile.first-name.title")}
                                <span className="text-red-500">{"*"}</span>
                            </Label>
                            <Input
                                id="firstName"
                                type="text"
                                value={formData.firstName}
                                onChange={(e) =>
                                    handleChange("firstName", e.target.value)
                                }
                                placeholder={t(
                                    "questionnaire.profile.first-name.placeholder",
                                )}
                                required
                                disabled={isSaving}
                                className="border-[#E1E4F0] focus-visible:ring-0 focus-visible:border-[#FFCC00]"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="lastName" className="text-gray-800">
                                {t("questionnaire.profile.last-name.title")}
                                <span className="text-red-500">{"*"}</span>
                            </Label>
                            <Input
                                id="lastName"
                                type="text"
                                value={formData.lastName}
                                onChange={(e) =>
                                    handleChange("lastName", e.target.value)
                                }
                                placeholder={t(
                                    "questionnaire.profile.last-name.placeholder",
                                )}
                                required
                                disabled={isSaving}
                                className="border-[#E1E4F0] focus-visible:ring-0 focus-visible:border-[#FFCC00]"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="city" className="text-gray-800">
                                {t("questionnaire.profile.city.title")}
                            </Label>
                            <Input
                                id="city"
                                type="text"
                                value={formData.city}
                                onChange={(e) =>
                                    handleChange("city", e.target.value)
                                }
                                placeholder={t(
                                    "questionnaire.profile.city.placeholder",
                                )}
                                disabled={isSaving}
                                className="border-[#E1E4F0] focus-visible:ring-0 focus-visible:border-[#FFCC00]"
                            />
                        </div>

                        <div className="space-y-2">
                            <Label
                                htmlFor="birthDate"
                                className="text-gray-800"
                            >
                                {t("questionnaire.profile.birth-date.title")}
                            </Label>
                            <div className="relative">
                                <Input
                                    id="birthDate"
                                    type="text"
                                    value={formData.birthDate}
                                    onChange={(e) =>
                                        handleDateChange(e.target.value)
                                    }
                                    placeholder={t(
                                        "questionnaire.profile.birth-date.placeholder",
                                    )}
                                    maxLength={10}
                                    disabled={isSaving}
                                    className="border-[#E1E4F0] pr-10 focus-visible:ring-0 focus-visible:border-[#FFCC00]"
                                />
                                <Calendar className="absolute right-3 top-1/2 -translate-y-1/2 text-[#C3C7D6] w-5 h-5 pointer-events-none" />
                            </div>
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="gender" className="text-gray-800">
                                {t("questionnaire.profile.gender.title")}
                            </Label>
                            <div className="relative">
                                <select
                                    id="gender"
                                    value={formData.gender}
                                    onChange={(e) =>
                                        handleChange("gender", e.target.value)
                                    }
                                    disabled={isSaving}
                                    className="h-9 w-full min-w-0 rounded-xl border border-[#E1E4F0] bg-white px-4 pr-10 text-sm text-heading-black outline-none focus-visible:border-[#FFCC00] appearance-none"
                                >
                                    <option value="">
                                        {t(
                                            "questionnaire.profile.gender.options.no-select",
                                        )}
                                    </option>
                                    <option value="male">
                                        {t(
                                            "questionnaire.profile.gender.options.male",
                                        )}
                                    </option>
                                    <option value="female">
                                        {t(
                                            "questionnaire.profile.gender.options.female",
                                        )}
                                    </option>
                                </select>
                                <span className="pointer-events-none absolute right-3 top-1/2 -translate-y-1/2 text-[#434650]">
                                    {"▼"}
                                </span>
                            </div>
                        </div>
                    </div>
                </form>
            </div>
            {/* Кнопки навигации */}
            <div className="flex justify-end gap-4 mr-32 mt-15.75">
                <Button
                    onClick={handleBack}
                    disabled={isSaving}
                    variant="action"
                    className="h-[2.8125rem] w-25 font-medium text-[0.9375rem] leading-5"
                >
                    {t("questionnaire.buttons.back")}
                </Button>
                <Button
                    onClick={(e) => {
                        e.preventDefault();
                        const form = document.getElementById(
                            "profile-form",
                        ) as HTMLFormElement;
                        if (form) {
                            form.requestSubmit();
                        }
                    }}
                    disabled={isSaving}
                    className="h-[2.8125rem] w-25"
                >
                    {isSaving ? "Сохранение..." : "Готово"}
                </Button>
            </div>
        </>
    );
}

export default ProfileForm;
