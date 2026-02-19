import { Search, Plus } from "lucide-react";
import { useTranslation } from "react-i18next";

interface ProfileHeaderProps {
  onSearch?: (query: string) => void;
  onCreatePost?: () => void;
}

export default function ProfileHeader({ onSearch, onCreatePost }: ProfileHeaderProps) {
  const { t } = useTranslation();

  return (
    <div 
      className="flex flex-row justify-between items-start bg-[#FEFEFE] w-full"
      style={{ 
        padding: '20px 32px',
        gap: '16px',
        height: '88px',
        borderWidth: '0px 1px 1px 1px',
        borderStyle: 'solid',
        borderColor: '#EAECEE',
        isolation: 'isolate'
      }}
    >

      <div 
        className="flex flex-col items-end flex-1"
        style={{ gap: '8px', height: '48px' }}
      >
        <div 
          className="flex flex-row items-center bg-[#FEFEFE] border border-[#EAECEE] w-full"
          style={{ 
            padding: '12px',
            gap: '12px',
            height: '48px',
            minHeight: '48px',
            borderRadius: '12px'
          }}
        >
          <div className="flex flex-row items-center flex-1" style={{ gap: '8px' }}>
            <input
              type="text"
              placeholder={t("profile.header.searchPlaceholder")}
              className="flex-1 bg-transparent focus:outline-none font-roboto font-medium text-[#333333] placeholder:text-[rgba(51,51,51,0.75)]"
              style={{ 
                fontSize: '16px',
                lineHeight: '22px',
                letterSpacing: '-0.007em'
              }}
              onChange={(e) => onSearch?.(e.target.value)}
            />
          </div>
          <Search className="w-5 h-5 text-[#333333] shrink-0" />
        </div>
      </div>

      <button
        onClick={onCreatePost}
        className="flex flex-row justify-center items-center bg-[#FFDD2D] hover:bg-[#f0d029] transition-colors shrink-0"
        style={{
          padding: '12px 20px',
          gap: '10px',
          width: '173px',
          height: '48px',
          minHeight: '48px',
          borderRadius: '12px'
        }}
      >
        <span 
          className="font-roboto font-bold text-[#333333]"
          style={{ fontSize: '16px', lineHeight: '22px', letterSpacing: '-0.007em' }}
        >
          {t("profile.header.createPost")}
        </span>
        <Plus className="w-5 h-5 text-[#333333]" />
      </button>
    </div>
  );
}

