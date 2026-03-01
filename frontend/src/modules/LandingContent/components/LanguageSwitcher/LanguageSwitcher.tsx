import { useTranslation } from "react-i18next";
import { Globe } from "lucide-react";
import { useEffect, useRef, useState } from "react";

export function LanguageSwitcher() {
    const { i18n } = useTranslation();
    const [isOpen, setIsOpen] = useState(false);
    const langFormRef = useRef<HTMLInputElement>(null);
    const langButtonRef = useRef<HTMLButtonElement>(null);

    const languages = [
        { code: "ru", label: "Русский" },
        { code: "en", label: "English" },
    ];

    const currentLanguage =
        languages.find((lang) => lang.code === i18n.language) || languages[0];

    const handleLanguageChange = (langCode: string) => {
        i18n.changeLanguage(langCode);
        localStorage.setItem("language", langCode);
        setIsOpen(false);
    };

    useEffect(() => {
        function handleClickOutside(event: MouseEvent) {
            if (
                langFormRef.current &&
                !langFormRef.current.contains(event.target as Node) &&
                langButtonRef.current &&
                !langButtonRef.current.contains(event.target as Node)
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
                ref={langButtonRef}
                type="button"
                onClick={() => setIsOpen(!isOpen)}
                className="flex items-center gap-1 px-2 py-1.5 sm:px-0 sm:py-0 text-sm sm:text-base text-header-button-text hover:text-blue-700 transition-all ease-out duration-300 font-medium font-roboto"
                aria-label="Change language"
            >
                <Globe className="size-5 sm:size-4" />
                <span className="hidden sm:inline">
                    {currentLanguage.label}
                </span>
            </button>
            {isOpen && (
                <div
                    ref={langFormRef}
                    className="absolute right-0 mt-2 w-fit bg-accent-white border border-accent-gray rounded-lg shadow-default overflow-hidden"
                >
                    {languages.map((lang) => (
                        <button
                            key={lang.code}
                            type="button"
                            onClick={() => handleLanguageChange(lang.code)}
                            aria-expanded={isOpen}
                            aria-haspopup="menu"
                            className={`w-full text-center px-4 py-2 hover:bg-accent-gray transition-colors ease-out duration-300 leading-5.5 text-[0.9375rem] font-roboto font-medium ${
                                i18n.language === lang.code
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
