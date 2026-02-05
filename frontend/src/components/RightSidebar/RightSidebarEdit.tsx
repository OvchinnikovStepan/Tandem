import { Bell, Settings } from "lucide-react";

export type EditSection = "profile" | "interests" | "career";

interface MenuItem {
  id: EditSection;
  label: string;
}

const menuItems: MenuItem[] = [
  { id: "profile", label: "Профиль" },
  { id: "interests", label: "Интересы" },
  { id: "career", label: "Карьера" },
];

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
  return (
    <aside
      className="bg-[#FEFEFE] flex flex-col shrink-0 h-full"
      style={{ width: "312px" }}
    >
      {/* Header - Avatar and Actions */}
      <div 
        className="flex justify-between items-start bg-white"
        style={{ padding: '20px 24px', gap: '46px', height: '88px' }}
      >
        {/* Avatar with online indicator */}
        <div className="relative" style={{ width: '48px', height: '48px' }}>
          <div className="w-12 h-12 rounded-full bg-gray-200 overflow-hidden">
            {currentUser.avatar ? (
              <img
                src={currentUser.avatar}
                alt={currentUser.name}
                className="w-full h-full object-cover"
              />
            ) : (
              <img
                src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop&crop=face"
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
        <div className="flex" style={{ gap: '8px' }}>
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
              Редактирование профиля
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
