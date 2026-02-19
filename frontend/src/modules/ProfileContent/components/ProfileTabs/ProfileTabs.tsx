import { Grid3X3, Bookmark } from "lucide-react";
import { cn } from "@/lib/utils";
import { useTranslation } from "react-i18next";

export type TabType = "posts" | "saved";

interface Tab {
  id: TabType;
  label: string;
  icon: React.ElementType;
}

interface ProfileTabsProps {
  activeTab: TabType;
  onTabChange: (tab: TabType) => void;
}

export default function ProfileTabs({ activeTab, onTabChange }: ProfileTabsProps) {
  const { t } = useTranslation();
  const tabs: Tab[] = [
    { id: "posts", label: t("profile.tabs.posts"), icon: Grid3X3 },
    { id: "saved", label: t("profile.tabs.saved"), icon: Bookmark },
  ];

  return (
    <div 
      className="border-t border-[#DBDBDB] mx-5"
      style={{ height: '53px' }}
    >
      <div 
        className="flex items-center justify-center"
        style={{ gap: '70px', height: '52px' }}
      >
        {tabs.map((tab) => (
          <button
            key={tab.id}
            onClick={() => onTabChange(tab.id)}
            className={cn(
              "flex items-center gap-[7px] transition-colors relative",
              activeTab === tab.id
                ? "border-t-2 border-[#333333]"
                : ""
            )}
            style={{ padding: '16px 0', height: '52px' }}
          >
            <div className="flex items-center" style={{ gap: '10px', width: '24px', height: '24px' }}>
              <tab.icon 
                className={cn(
                  "w-6 h-6",
                  activeTab === tab.id ? "text-[#333333]" : "text-[#8E8E8E]"
                )}
                strokeWidth={2}
              />
            </div>
            <span 
              className={cn(
                "font-roboto font-semibold uppercase flex items-center text-center",
                activeTab === tab.id ? "text-[#333333]" : "text-[#8E8E8E]"
              )}
              style={{ 
                fontSize: tab.id === 'posts' ? '11px' : '12px', 
                lineHeight: '18px',
                letterSpacing: '1px'
              }}
            >
              {tab.label}
            </span>
          </button>
        ))}
      </div>
    </div>
  );
}

