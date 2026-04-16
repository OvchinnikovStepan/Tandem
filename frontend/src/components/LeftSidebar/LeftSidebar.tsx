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
import { getDefaultAvatarUrl } from "@/lib/avatar";
import { useTranslation } from "react-i18next";
import { Badge, IconButton } from "@/ui";

interface NavItem {
  icon: React.ElementType;
  label: string;
  path: string;
  badge?: number;
}

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
  const { t } = useTranslation();
  const navItems: NavItem[] = [
    { icon: User, label: t("profile.sidebar.nav.profile"), path: "/profile" },
    { icon: Home, label: t("profile.sidebar.nav.home"), path: "/feed" },
    { icon: MessageCircle, label: t("profile.sidebar.nav.chats"), path: "/chats", badge: 2 },
    { icon: Users, label: t("profile.sidebar.nav.friends"), path: "/friends", badge: 2 },
    { icon: UsersRound, label: t("profile.sidebar.nav.groupChats"), path: "/groups" },
    { icon: Settings, label: t("profile.sidebar.nav.settings"), path: "/settings" },
    { icon: HelpCircle, label: t("profile.sidebar.nav.help"), path: "/help" },
  ];

  return (
    <aside 
      className="bg-accent-white hidden lg:flex flex-col justify-between items-start shrink-0 h-full"
      style={{ width: '312px', padding: '32px 16px', gap: '32px' }}
    >
      {/* Top section: Logo + Navigation */}
      <div className="w-[280px] flex flex-col items-start gap-8 mx-auto flex-1">
        {/* Logo */}
        <div className="w-[280px] h-14 flex flex-col items-start gap-2.5 border-b border-accent-gray">
          <div className="flex items-center gap-2">
            <div className="w-8 h-8 bg-yellow-400 rounded-lg flex items-center justify-center">
              <span className="text-lg font-bold">&lt;&gt;</span>
            </div>
            <span className="text-xl font-bold text-heading-black">Tandem</span>
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
                  <item.icon className="w-6 h-6 shrink-0 text-heading-black" />
                  <span 
                    className={cn(
                      "flex-1 text-base leading-[22px] tracking-[-0.007em] text-heading-black font-roboto",
                      isActive ? "font-bold" : "font-normal"
                    )}
                  >
                    {item.label}
                  </span>
                  {item.badge && (
                    <Badge variant="default" size="md">
                      {item.badge}
                    </Badge>
                  )}
                </div>
              )}
            </NavLink>
          ))}
        </nav>
      </div>

      {/* Bottom section: Current User */}
      <div className="w-[280px] h-[70px] flex flex-col justify-end items-center mx-auto">
        <div className="w-[280px] flex items-end pt-6 gap-4 border-t border-accent-gray">
          <div className="flex items-center gap-3 flex-1">
            {/* Avatar */}
            <div className="w-10 h-10 rounded-full bg-gray-200 overflow-hidden shrink-0">
              <img
                src={currentUser.avatar || getDefaultAvatarUrl(100)}
                alt={currentUser.name}
                className="w-full h-full object-cover"
              />
            </div>
            {/* Name */}
            <span 
              className="flex-1 text-base font-bold leading-[22px] tracking-[-0.007em] text-heading-black font-roboto"
            >
              {currentUser.name}
            </span>
          </div>
          {/* Logout button */}
          <IconButton
            size="md"
            title={t("profile.sidebar.logout")}
          >
            <LogOut className="w-7 h-7 text-heading-black" />
          </IconButton>
        </div>
      </div>
    </aside>
  );
}


