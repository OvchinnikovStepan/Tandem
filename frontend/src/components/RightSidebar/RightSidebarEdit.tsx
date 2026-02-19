import { Bell, Settings } from "lucide-react";
import { getDefaultAvatarUrl } from "@/lib/avatar";
import { useTranslation } from "react-i18next";
import { LanguageSwitcher } from "@/components/LanguageSwitcher/LanguageSwitcher";

export type EditSection = "profile" | "interests" | "career";

interface CurrentUser {
  name: string;
  avatar?: string;
}

interface RightSidebarEditProps {
  activeSection: EditSection;
  onSectionChange: (section: EditSection) => void;
  currentUser?: CurrentUser;
}

const defaultCurrentUser: CurrentUser = {
  name: "Azunyan U. Wu",
  avatar: undefined,
};

export default function RightSidebarEdit({
  activeSection,
  onSectionChange,
  currentUser = defaultCurrentUser,
}: RightSidebarEditProps) {
  const { t } = useTranslation();
  const menuItems: Array<{ id: EditSection; label: string }> = [
    { id: "profile", label: t("profile.rightSidebarEdit.menu.profile") },
    { id: "interests", label: t("profile.rightSidebarEdit.menu.interests") },
    { id: "career", label: t("profile.rightSidebarEdit.menu.career") },
  ];

  return (
    <aside
      className="bg-[#FEFEFE] flex flex-col shrink-0 overflow-hidden h-full"
      style={{ width: "312px" }}
    >
      {/* Header - Avatar and Actions */}
      <div 
        className="flex items-center bg-white"
        style={{ padding: '20px 24px', gap: '12px', minHeight: '88px' }}
      >
        {/* Avatar with online indicator */}
        <div className="relative shrink-0" style={{ width: '48px', height: '48px' }}>
          <div className="w-12 h-12 rounded-full bg-gray-200 overflow-hidden">
            {currentUser.avatar ? (
              <img
                src={currentUser.avatar}
                alt={currentUser.name}
                className="w-full h-full object-cover"
              />
            ) : (
              <img
                src={getDefaultAvatarUrl(100)}
                alt={currentUser.name}
                className="w-full h-full object-cover"
              />
            )}
          </div>
          {/* Online indicator */}
          <div 
            className="absolute bottom-0 right-0 bg-[#22C55E] border-[1.5px] border-[#FEFEFE] rounded-full"
            style={{ width: '12px', height: '12px' }}
          />
        </div>

        {/* Action buttons */}
        <div className="ml-auto flex items-center shrink-0" style={{ gap: '8px' }}>
          <LanguageSwitcher />
          <button 
            className="flex items-center justify-center rounded-full hover:bg-gray-100 transition-colors"
            style={{ width: '48px', height: '48px', padding: '16px' }}
          >
            <Bell className="w-7 h-7 text-[#333333]" />
          </button>
          <button 
            className="flex items-center justify-center rounded-full hover:bg-gray-100 transition-colors"
            style={{ width: '48px', height: '48px', padding: '16px' }}
          >
            <Settings className="w-7 h-7 text-[#333333]" />
          </button>
        </div>
      </div>

      {/* Content Section */}
      <div 
        className="flex flex-col bg-[#FEFEFE]"
        style={{ padding: '0px 24px 24px', gap: '32px' }}
      >
        {/* Menu Container */}
        <div
          className="flex flex-col items-start w-full border-t border-[#EAECEE]"
          style={{ height: "240px" }}
        >
          {/* Header */}
          <div
            className="flex items-center w-full border-b border-[#EAECEE]"
            style={{ padding: "24px 0px", gap: "16px", height: "72px" }}
          >
            <h2
              className="flex-1 font-roboto font-bold text-[#333333] text-center"
              style={{
                fontSize: "18px",
                lineHeight: "24px",
                letterSpacing: "-0.008em",
              }}
            >
              {t("profile.rightSidebarEdit.title")}
            </h2>
          </div>

          {/* Menu Items */}
          <div className="flex flex-col items-start w-full" style={{ height: "168px" }}>
            {menuItems.map((item) => (
              <button
                key={item.id}
                onClick={() => onSectionChange(item.id)}
                className="flex items-center justify-center w-full border-b border-[#EAECEE] cursor-pointer transition-colors"
                style={{
                  height: "56px",
                  background: activeSection === item.id ? "#F5F5F5" : "#FEFEFE",
                }}
              >
                <span
                  className="font-roboto font-bold text-[#333333] text-center"
                  style={{
                    fontSize: "14px",
                    lineHeight: "20px",
                    letterSpacing: "-0.006em",
                  }}
                >
                  {item.label}
                </span>
              </button>
            ))}
          </div>
        </div>
      </div>
    </aside>
  );
}
