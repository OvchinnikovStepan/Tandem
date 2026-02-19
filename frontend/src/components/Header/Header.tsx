import { IconButton } from "@/ui/icon-button";
import { useNavigate } from "react-router-dom";
import { LanguageSwitcher } from "../LanguageSwitcher/LanguageSwitcher";

interface HeaderProps {
  onBack?: () => void;
  closeUrl?: string;
  showClose?: boolean;
}

export default function Header({ onBack, closeUrl = "/", showClose = true }: HeaderProps) {
  const navigate = useNavigate();

  return (
    <header className="border-b border-gray-200 bg-white">
      <div className="container py-4 flex items-center justify-between">
        <div className="flex items-center gap-2">
          <img src="/logo/Black_Logo.png" alt="Tandem" className="h-14" />
        </div>
        <div className="flex items-center gap-2">
          <LanguageSwitcher />
          {onBack && (
            <IconButton icon="back" onClick={onBack} />
          )}
          {showClose && (
            <IconButton icon="close" onClick={() => navigate(closeUrl)} />
          )}
        </div>
      </div>
    </header>
  );
}
