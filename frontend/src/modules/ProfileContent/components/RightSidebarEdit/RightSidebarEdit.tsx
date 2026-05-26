import type { ElementType } from "react";
import { Briefcase, Heart } from "lucide-react";
import { useTranslation } from "react-i18next";
import { SidebarPanelHeader } from "@/components/SidebarPanelHeader";
import ProfileIcon from "@/ui/navigationIcons/ProfileIcon";
import { cn } from "@/lib/utils";

export type EditSection = "profile" | "interests" | "career";

type MenuIconProps = {
    isActive?: boolean;
    className?: string;
};

function InterestsMenuIcon({ isActive = false, className }: MenuIconProps) {
    return (
        <Heart
            className={cn(
                "size-6 shrink-0 transition-colors duration-200",
                isActive && "fill-current",
                className,
            )}
            strokeWidth={2}
        />
    );
}

function CareerMenuIcon({ isActive = false, className }: MenuIconProps) {
    return (
        <Briefcase
            className={cn(
                "size-6 shrink-0 transition-colors duration-200",
                isActive && "fill-current",
                className,
            )}
            strokeWidth={2}
        />
    );
}

interface RightSidebarEditProps {
    activeSection: EditSection;
    onSectionChange: (section: EditSection) => void;
    currentUser?: {
        name: string;
    };
}

export default function RightSidebarEdit({
    activeSection,
    onSectionChange,
    currentUser = { name: "Azunyan U. Wu" },
}: RightSidebarEditProps) {
    const { t } = useTranslation();

    const menuItems: Array<{
        id: EditSection;
        label: string;
        icon: ElementType<MenuIconProps>;
    }> = [
        {
            id: "profile",
            label: t("profile.rightSidebarEdit.menu.profile"),
            icon: ProfileIcon,
        },
        {
            id: "interests",
            label: t("profile.rightSidebarEdit.menu.interests"),
            icon: InterestsMenuIcon,
        },
        {
            id: "career",
            label: t("profile.rightSidebarEdit.menu.career"),
            icon: CareerMenuIcon,
        },
    ];

    return (
        <aside className="flex h-full w-[312px] shrink-0 flex-col bg-accent-white shadow-side-lines">
            <SidebarPanelHeader fallbackName={currentUser.name} />

            <div className="flex flex-1 flex-col px-4 pb-6 pt-6">
                <h2 className="mb-4 px-3 text-base font-roboto font-bold text-heading-black">
                    {t("profile.rightSidebarEdit.title")}
                </h2>

                <nav className="flex w-full flex-col gap-2">
                    {menuItems.map((item) => {
                        const isActive = activeSection === item.id;
                        const Icon = item.icon;

                        return (
                            <button
                                key={item.id}
                                type="button"
                                onClick={() => onSectionChange(item.id)}
                                className={cn(
                                    "flex h-12 min-h-12 w-full items-center gap-2 rounded-default px-3 py-2.5 text-heading-black transition-colors hover:bg-landing-bg",
                                    isActive && "bg-landing-bg",
                                )}
                            >
                                <Icon
                                    isActive={isActive}
                                    className="shrink-0 text-heading-black"
                                />
                                <span
                                    className={cn(
                                        "flex-1 text-left text-base leading-[1.375rem] tracking-[-0.007em] font-roboto",
                                        isActive ? "font-bold" : "font-normal",
                                    )}
                                >
                                    {item.label}
                                </span>
                            </button>
                        );
                    })}
                </nav>
            </div>
        </aside>
    );
}
