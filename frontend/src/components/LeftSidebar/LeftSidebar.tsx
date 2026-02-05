import { NavLink } from "react-router-dom";
import {
  User,
  Home,
  MessageCircle,
  Users,
  UsersRound,
  Settings,
  HelpCircle,
  LogOut,
} from "lucide-react";
import { cn } from "@/lib/utils";

interface NavItem {
  icon: React.ElementType;
  label: string;
  path: string;
  badge?: number;
}

const navItems: NavItem[] = [
  { icon: User, label: "Профиль", path: "/profile" },
  { icon: Home, label: "Главная", path: "/feed" },
  { icon: MessageCircle, label: "Чаты", path: "/chats", badge: 2 },
  { icon: Users, label: "Друзья", path: "/friends", badge: 2 },
  { icon: UsersRound, label: "Групповые чаты", path: "/groups" },
  { icon: Settings, label: "Настройки", path: "/settings" },
  { icon: HelpCircle, label: "Помощь", path: "/help" },
];

interface CurrentUser {
  name: string;
  avatar?: string;
}

interface LeftSidebarProps {
  currentUser?: CurrentUser;
}

const defaultUser: CurrentUser = {
  name: "Azunyan U. Wu",
  avatar: undefined,
};

export default function LeftSidebar({
  currentUser = defaultUser,
}: LeftSidebarProps) {
  return (
    <aside 
      className="bg-[#FEFEFE] flex flex-col justify-between items-start shrink-0 h-full"
      style={{ width: '312px', padding: '32px 16px', gap: '32px' }}
    >
      {/* Top section: Logo + Navigation */}
      <div className="w-[280px] flex flex-col items-start gap-8 mx-auto flex-1">
        {/* Logo */}
        <div className="w-[280px] h-14 flex flex-col items-start gap-2.5 border-b border-[#EAECEE]">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-yellow-400 rounded-lg flex items-center justify-center">
              <span className="text-lg font-bold">&lt;&gt;</span>
            </div>
            <span className="text-xl font-bold text-[#333333]">Tandem</span>
          </div>
        </div>

        {/* Navigation */}
        <nav className="w-[280px] flex flex-col items-start gap-2">
          {navItems.map((item) => (
            <NavLink
              key={item.path}
              to={item.path}
              className={({ isActive }) =>
                cn(
                  "w-[280px] h-12 min-h-12 flex items-center px-3 py-2.5 gap-2 rounded-xl transition-colors",
                  isActive
                    ? "bg-gray-100"
                    : "hover:bg-gray-50"
                )
              }
            >
              {({ isActive }) => (
                <div className="flex items-center gap-2 flex-1">
                  <item.icon className="w-6 h-6 shrink-0 text-[#333333]" />
                  <span 
                    className={cn(
                      "flex-1 text-base leading-[22px] tracking-[-0.007em] text-[#333333]",
                      isActive ? "font-bold" : "font-normal"
                    )}
                    style={{ fontFamily: 'Roboto, sans-serif' }}
                  >
                    {item.label}
                  </span>
                  {item.badge && (
                    <span 
                      className="flex justify-center items-center w-7 h-7 rounded-full text-sm font-semibold text-[#126DF7]"
                      style={{ 
                        background: 'rgba(18, 109, 247, 0.1)',
                        border: '1px solid rgba(18, 109, 247, 0.5)'
                      }}
                    >
                      {item.badge}
                    </span>
                  )}
                </div>
              )}
            </NavLink>
          ))}
        </nav>
      </div>

      {/* Bottom section: Current User */}
      <div className="w-[280px] h-[70px] flex flex-col justify-end items-center mx-auto">
        <div className="w-[280px] flex items-end pt-6 gap-4 border-t border-[#EAECEE]">
          <div className="flex items-center gap-3 flex-1">
            {/* Avatar */}
            <div className="w-10 h-10 rounded-full bg-gray-200 overflow-hidden shrink-0">
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
            {/* Name */}
            <span 
              className="flex-1 text-base font-bold leading-[22px] tracking-[-0.007em] text-[#333333]"
              style={{ fontFamily: 'Roboto, sans-serif' }}
            >
              {currentUser.name}
            </span>
          </div>
          {/* Logout button */}
          <button
            className="w-10 h-10 flex justify-center items-center rounded-full hover:bg-gray-100 transition-colors"
            title="Выйти"
          >
            <LogOut className="w-7 h-7 text-[#333333]" />
          </button>
        </div>
      </div>
    </aside>
  );
}


