import { useTranslation } from "react-i18next";
import { Globe } from "lucide-react";
import { useEffect, useRef, useState } from "react";

export function LanguageSwitcher() {
    const { i18n } = useTranslation();
    const [isOpen, setIsOpen] = useState(false);
    const langFormRef = useRef<HTMLDivElement>(null);

    const languages = [
        { code: "ru", label: "Русский" },
        { code: "en", label: "English" },
    ];

    const normalizedLanguage = (
        i18n.resolvedLanguage || i18n.language
    ).split("-")[0];

    const currentLanguage =
        languages.find((lang) => lang.code === normalizedLanguage) ||
        languages[0];

    const handleLanguageChange = (langCode: string) => {
        void i18n.changeLanguage(langCode);
        localStorage.setItem("language", langCode);
        setIsOpen(false);
    };

    useEffect(() => {
        function handleClickOutside(event: MouseEvent) {
            if (
                langFormRef.current &&
                !langFormRef.current.contains(event.target as Node)
            ) {
                setIsOpen(false);
            }
        }
        document.addEventListener("mousedown", handleClickOutside);

        return () => {
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, []);

    return (
        <div className="relative">
            <button
                type="button"
                onClick={() => setIsOpen(!isOpen)}
                className="flex items-center gap-1 text-header-button-text hover:text-blue-700 transition-all ease-out duration-300 font-medium text-base font-roboto"
                aria-label="Change language"
            >
                <Globe className="size-4" />
                <span>{currentLanguage.label}</span>
            </button>
            {isOpen && (
                <div
                    ref={langFormRef}
                    className="absolute right-0 mt-2 w-25 bg-accent-white border border-accent-gray rounded-lg shadow-default overflow-hidden"
                >
                    {languages.map((lang) => (
                        <button
                            key={lang.code}
                            type="button"
                            onClick={() => handleLanguageChange(lang.code)}
                            className={`w-full text-center px-4 py-2 hover:bg-accent-gray transition-colors ease-out duration-300 leading-5.5 text-[0.9375rem] font-roboto font-medium ${
                                normalizedLanguage === lang.code
                                    ? "text-blue-600"
                                    : "text-heading-black"
                            }`}
                        >
                            {lang.label}
                        </button>
                    ))}
                </div>
            )}
        </div>
    );
}
