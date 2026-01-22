import { useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import {
  User,
  Home,
  MessageCircle,
  Users,
  UsersRound,
  Settings,
  HelpCircle,
  LogOut,
  ChevronLeft,
  ChevronRight,
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
  { icon: UsersRound, label: "Группы", path: "/groups" },
  { icon: Settings, label: "Настройки", path: "/settings" },
  { icon: HelpCircle, label: "Помощь", path: "/help" },
];

interface CurrentUser {
  name: string;
  username: string;
  avatar?: string;
  memberType: string;
}

interface LeftSidebarProps {
  currentUser?: CurrentUser;
  defaultCollapsed?: boolean;
}

const defaultUser: CurrentUser = {
  name: "Azunyan U. Wu",
  username: "azunyandesu",
  memberType: "Basic Member",
  avatar: undefined,
};

export default function LeftSidebar({
  currentUser = defaultUser,
  defaultCollapsed = false,
}: LeftSidebarProps) {
  const [isCollapsed, setIsCollapsed] = useState(defaultCollapsed);
  const navigate = useNavigate();

  return (
    <aside
      className={cn(
        "h-screen bg-white border-r border-gray-200 flex flex-col transition-all duration-300 sticky top-0",
        isCollapsed ? "w-20" : "w-64"
      )}
    >
      {/* Logo */}
      <div className="p-4 border-b border-gray-200">
        <div className="flex items-center gap-2">
          <img
            src="/logo/Black_Logo.png"
            alt="Tandem"
            className={cn("transition-all duration-300", isCollapsed ? "h-10" : "h-12")}
          />
        </div>
      </div>

      {/* Navigation */}
      <nav className="flex-1 p-4">
        <ul className="space-y-1">
          {navItems.map((item) => (
            <li key={item.path}>
              <NavLink
                to={item.path}
                className={({ isActive }) =>
                  cn(
                    "flex items-center gap-3 px-3 py-2.5 rounded-lg transition-colors relative group",
                    isActive
                      ? "bg-gray-100 text-gray-900 font-medium"
                      : "text-gray-600 hover:bg-gray-50 hover:text-gray-900"
                  )
                }
              >
                <item.icon className="w-5 h-5 shrink-0" />
                {!isCollapsed && (
                  <>
                    <span className="flex-1">{item.label}</span>
                    {item.badge && (
                      <span className="bg-primary text-primary-foreground text-xs font-medium px-2 py-0.5 rounded-full">
                        {item.badge}
                      </span>
                    )}
                  </>
                )}
                {isCollapsed && item.badge && (
                  <span className="absolute -top-1 -right-1 bg-primary text-primary-foreground text-xs font-medium w-5 h-5 rounded-full flex items-center justify-center">
                    {item.badge}
                  </span>
                )}
              </NavLink>
            </li>
          ))}
        </ul>
      </nav>

      {/* Collapse Toggle */}
      <button
        onClick={() => setIsCollapsed(!isCollapsed)}
        className="absolute -right-3 top-1/2 -translate-y-1/2 w-6 h-6 bg-white border border-gray-200 rounded-full flex items-center justify-center hover:bg-gray-50 transition-colors shadow-sm"
      >
        {isCollapsed ? (
          <ChevronRight className="w-4 h-4 text-gray-600" />
        ) : (
          <ChevronLeft className="w-4 h-4 text-gray-600" />
        )}
      </button>

      {/* Current User */}
      <div className="p-4 border-t border-gray-200">
        <div className="flex items-center gap-3">
          <div className="w-10 h-10 rounded-full bg-gray-200 overflow-hidden shrink-0">
            {currentUser.avatar ? (
              <img
                src={currentUser.avatar}
                alt={currentUser.name}
                className="w-full h-full object-cover"
              />
            ) : (
              <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-blue-400 to-blue-600 text-white font-medium">
                {currentUser.name.charAt(0)}
              </div>
            )}
          </div>
          {!isCollapsed && (
            <>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-900 truncate">
                  {currentUser.name}
                </p>
                <p className="text-xs text-blue-500 truncate">
                  {currentUser.memberType}
                </p>
              </div>
              <button
                onClick={() => navigate("/login")}
                className="p-2 text-gray-400 hover:text-gray-600 transition-colors"
                title="Выйти"
              >
                <LogOut className="w-5 h-5" />
              </button>
            </>
          )}
        </div>
      </div>
    </aside>
  );
}


