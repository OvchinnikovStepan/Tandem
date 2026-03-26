import { useTranslation } from "react-i18next";
import { Globe } from "lucide-react";
import { useEffect, useRef, useState } from "react";
import { cn } from "@/lib/utils.ts";
import { LANGUAGES } from "@/constants/languages.ts";
import Button from "@/ui/Button.tsx";

export function LanguageSwitcher() {
    const { i18n } = useTranslation();
    const [isOpen, setIsOpen] = useState(false);
    const langFormRef = useRef<HTMLDivElement>(null);
    const langButtonRef = useRef<HTMLButtonElement>(null);

    const currentLanguage =
        LANGUAGES.find((lang) => lang.code === i18n.language) || LANGUAGES[0];

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
            <Button
                ref={langButtonRef}
                variant="link"
                size="custom"
                onClick={() => setIsOpen(!isOpen)}
                className="flex gap-1 px-2 py-1.5 sm:px-0 sm:py-0 has-[>svg]:px-0 text-sm sm:text-base hover:no-underline"
                aria-label="Change language"
                aria-expanded={isOpen}
                aria-haspopup="menu"
            >
                <Globe className="size-5 sm:size-4" />
                <span className="hidden sm:inline">
                    {currentLanguage.label}
                </span>
            </Button>
            {isOpen && (
                <div
                    ref={langFormRef}
                    className="absolute right-0 mt-2 w-fit bg-accent-white border border-accent-gray rounded-lg shadow-default overflow-hidden"
                >
                    {LANGUAGES.map((lang) => (
                        <Button
                            key={lang.code}
                            variant="ghost"
                            onClick={() => handleLanguageChange(lang.code)}
                            className={cn(
                                "w-full h-fit rounded-none duration-300 px-4 py-2 text-heading-black",
                                i18n.language === lang.code && "text-blue-600",
                            )}
                        >
                            {lang.label}
                        </Button>
                    ))}
                </div>
            )}
        </div>
    );
}
