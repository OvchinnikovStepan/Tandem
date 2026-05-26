import type { NavItem } from "@/modules/LeftSidebar/types/types.ts";
import {
    ProfileIcon,
    HomeIcon,
    ChatsIcon,
    FriendsIcon,
    SettingsIcon,
    HelpIcon,
} from "@/ui/navigationIcons";

export const NAV_ITEMS: NavItem[] = [
    { icon: ProfileIcon, label: "Профиль", path: "/profile" },
    { icon: HomeIcon, label: "Главная", path: "/feed" },
    { icon: ChatsIcon, label: "Чаты", path: "/chats", badge: 2 },
    { icon: FriendsIcon, label: "Друзья", path: "/friends", badge: 2 },
    { icon: SettingsIcon, label: "Настройки", path: "/settings" },
    { icon: HelpIcon, label: "Помощь", path: "/help" },
] as const;
