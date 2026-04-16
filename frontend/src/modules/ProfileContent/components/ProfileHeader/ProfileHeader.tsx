import { Plus } from "lucide-react";
import { useTranslation } from "react-i18next";
import { SearchInput, Button } from "@/ui";

interface ProfileHeaderProps {
  onSearch?: (query: string) => void;
  onCreatePost?: () => void;
}

export default function ProfileHeader({ onSearch, onCreatePost }: ProfileHeaderProps) {
  const { t } = useTranslation();

  return (
    <div 
      className="flex flex-row justify-between items-start bg-accent-white w-full"
      style={{ 
        padding: '20px 32px',
        gap: '16px',
        height: '88px',
        borderWidth: '0px 1px 1px 1px',
        borderStyle: 'solid',
        borderColor: 'var(--color-accent-gray)',
        isolation: 'isolate'
      }}
    >

      <div 
        className="flex flex-col items-end flex-1"
        style={{ gap: '8px', height: '48px' }}
      >
        <SearchInput
          placeholder={t("profile.header.searchPlaceholder")}
          onChange={(e) => onSearch?.(e.target.value)}
          className="w-full"
          style={{ width: '100%', height: '48px', borderRadius: '12px' }}
        />
      </div>

      <Button
        onClick={onCreatePost}
        variant="default"
        className="shrink-0 font-bold"
        style={{
          width: '173px',
          height: '48px',
          minHeight: '48px',
          borderRadius: '12px'
        }}
      >
        <span 
          className="font-roboto font-bold text-heading-black text-body-md"
        >
          {t("profile.header.createPost")}
        </span>
        <Plus className="w-5 h-5 text-heading-black" />
      </Button>
    </div>
  );
}

