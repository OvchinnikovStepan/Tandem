import { useEffect, useState } from "react";
import { SunIcon, MoonIcon } from "lucide-react"; // Пример иконок

type ThemeSwitcherProps = {
    className?: string;
};

export default function ThemeSwitcher({ className }: ThemeSwitcherProps) {
    const [isDark, setIsDark] = useState(() => {
        return (
            localStorage.getItem("theme") === "dark" ||
            (!localStorage.getItem("theme") &&
                window.matchMedia("(prefers-color-scheme: dark)").matches)
        );
    });

    useEffect(() => {
        const root = window.document.documentElement;
        if (isDark) {
            root.classList.add("dark");
            localStorage.setItem("theme", "dark");
        } else {
            root.classList.remove("dark");
            localStorage.setItem("theme", "light");
        }
    }, [isDark]);

    return (
        <button
            onClick={() => setIsDark(!isDark)}
            className={`h-12 w-12 rounded-full flex items-center justify-center
                 bg-gray-200 dark:bg-gray-700 text-gray-800 dark:text-gray-200
                 hover:bg-gray-300 dark:hover:bg-gray-600 transition-all duration-300
                 ${className}
            `}
            aria-label="Переключить тему"
        >
            {isDark ? (
                <SunIcon className="h-6 w-6" />
            ) : (
                <MoonIcon className="h-6 w-6" />
            )}
        </button>
    );
}
