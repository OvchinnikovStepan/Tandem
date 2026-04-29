import { useTranslation } from "react-i18next";

type HeroBlockVariant = "login" | "register" | "forgot-password";

interface HeroBlockProps {
    variant: HeroBlockVariant;
}

export default function HeroBlock({ variant: _variant }: HeroBlockProps) {
    const { t } = useTranslation();

    return (
        <div className="hidden lg:flex lg:w-1/2 bg-linear-to-br from-yellow-50 to-yellow-100 items-center justify-center p-8">
            <div className="w-full max-w-sm">
                <img
                    src="/security_shield.png"
                    alt={t("hero.illustrationAlt")}
                    className="w-full max-w-md h-auto object-contain"
                />
            </div>
        </div>
    );
}
