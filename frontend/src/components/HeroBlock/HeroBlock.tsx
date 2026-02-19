import { useTranslation } from "react-i18next";

type HeroBlockVariant = "login" | "register" | "forgot-password";

interface HeroBlockProps {
  variant: HeroBlockVariant;
}

export default function HeroBlock({ variant }: HeroBlockProps) {
  const { t } = useTranslation();

  const getImageSrc = () => {
    switch (variant) {
      case "login":
        return "/security_shield.png";
      case "register":
        return "/security_shield.png";
      case "forgot-password":
        return "/security_shield.png";
      default:
        return "/security_shield.png";
    }
  };

  return (
    <div className="hidden lg:flex lg:w-1/2 bg-linear-to-br from-yellow-50 to-yellow-100 items-center justify-center p-8">
      <div className="w-full max-w-sm">
        <img 
          src={getImageSrc()} 
          alt={t("hero.illustrationAlt")}
          className="w-full max-w-md h-auto object-contain"
        />
      </div>
    </div>
  );
}
