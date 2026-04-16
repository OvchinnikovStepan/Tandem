import { Bell, Settings } from "lucide-react";
import { getDefaultAvatarUrl } from "@/lib/avatar";
import { useTranslation } from "react-i18next";
import { LanguageSwitcher } from "@/components/LanguageSwitcher/LanguageSwitcher";
import { Avatar, IconButton } from "@/ui";
import { cn } from "@/lib/utils";

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
      className="bg-accent-white hidden xl:flex flex-col shrink-0 overflow-hidden h-full"
      style={{ width: "312px" }}
    >
      {/* Header - Avatar and Actions */}
      <div 
        className="flex items-center bg-white"
        style={{ padding: '20px 24px', gap: '12px', minHeight: '88px' }}
      >
        {/* Avatar with online indicator */}
        <Avatar
          src={currentUser.avatar || getDefaultAvatarUrl(100)}
          alt={currentUser.name}
          size="md"
          showOnlineIndicator
          isOnline
          className="shrink-0"
        />

        {/* Action buttons */}
        <div className="ml-auto flex items-center shrink-0" style={{ gap: '8px' }}>
          <LanguageSwitcher />
          <IconButton size="md">
            <Bell className="w-7 h-7 text-heading-black" />
          </IconButton>
          <IconButton size="md">
            <Settings className="w-7 h-7 text-heading-black" />
          </IconButton>
        </div>
      </div>

      {/* Content Section */}
      <div 
        className="flex flex-col bg-accent-white"
        style={{ padding: '0px 24px 24px', gap: '32px' }}
      >
        {/* Menu Container */}
        <div
          className="flex flex-col items-start w-full border-t border-accent-gray"
          style={{ height: "240px" }}
        >
          {/* Header */}
          <div
            className="flex items-center w-full border-b border-accent-gray"
            style={{ padding: "24px 0px", gap: "16px", height: "72px" }}
          >
            <h2
              className="flex-1 font-roboto font-bold text-heading-black text-center text-section-title"
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
                className={cn(
                  "flex items-center justify-center w-full border-b border-accent-gray cursor-pointer transition-colors",
                  activeSection === item.id && "bg-light-bg"
                )}
                style={{
                  height: "56px",
                }}
              >
                <span
                  className="font-roboto font-bold text-heading-black text-center text-body-sm"
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
