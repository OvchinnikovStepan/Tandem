import { ArrowLeft, X } from "lucide-react";
import { useNavigate } from "react-router-dom";

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
        {(onBack || showClose) && (
          <div className="flex items-center gap-2">
            {onBack && (
              <button
                onClick={onBack}
                className="w-10 h-10 rounded-full border border-gray-200 flex items-center justify-center hover:bg-gray-50 transition-colors"
              >
                <ArrowLeft className="w-5 h-5 text-gray-600" />
              </button>
            )}
            {showClose && (
              <button
                onClick={() => navigate(closeUrl)}
                className="w-10 h-10 rounded-full border border-gray-200 flex items-center justify-center hover:bg-gray-50 transition-colors"
              >
                <X className="w-5 h-5 text-gray-600" />
              </button>
            )}
          </div>
        )}
      </div>
    </header>
  );
}
