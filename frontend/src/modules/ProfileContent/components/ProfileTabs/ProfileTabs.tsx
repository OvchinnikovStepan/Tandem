import { Grid3X3, Video, Bookmark } from "lucide-react";
import { cn } from "@/lib/utils";

export type TabType = "posts" | "videos" | "saved";

interface Tab {
  id: TabType;
  label: string;
  icon: React.ElementType;
}

const tabs: Tab[] = [
  { id: "posts", label: "ПОСТЫ", icon: Grid3X3 },
  { id: "videos", label: "ВИДЕО", icon: Video },
  { id: "saved", label: "СОХРАНЕННОЕ", icon: Bookmark },
];

interface ProfileTabsProps {
  activeTab: TabType;
  onTabChange: (tab: TabType) => void;
}

export default function ProfileTabs({ activeTab, onTabChange }: ProfileTabsProps) {
  return (
    <div className="border-t border-gray-200 overflow-x-auto">
      <div className="flex items-center justify-center gap-6 min-w-max mx-auto">
        {tabs.map((tab) => (
          <button
            key={tab.id}
            onClick={() => onTabChange(tab.id)}
            className={cn(
              "flex items-center gap-1.5 py-4 px-2 text-xs font-medium tracking-wider transition-colors relative whitespace-nowrap",
              activeTab === tab.id
                ? "text-gray-900"
                : "text-gray-400 hover:text-gray-600"
            )}
          >
            <tab.icon className="w-4 h-4 shrink-0" />
            <span>{tab.label}</span>
            {activeTab === tab.id && (
              <div className="absolute top-0 left-0 right-0 h-0.5 bg-gray-900" />
            )}
          </button>
        ))}
      </div>
    </div>
  );
}

