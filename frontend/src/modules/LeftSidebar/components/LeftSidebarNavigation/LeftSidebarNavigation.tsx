import { NAV_ITEMS } from "@/modules/LeftSidebar/constants/constants.ts";
import { NavLink } from "react-router";
import { cn } from "@/lib/utils.ts";

export function LeftSidebarNavigation() {
    return (
        <nav className="flex w-full flex-col gap-2">
            {NAV_ITEMS.map((item) => (
                <NavLink
                    key={item.path}
                    to={item.path}
                    className={cn(
                        "flex h-12 min-h-12 w-full items-center gap-2 rounded-default",
                        "px-3 py-2.5 text-heading-black transition-colors hover:bg-landing-bg",
                    )}
                >
                    {({ isActive }) => (
                        <div className="flex flex-1 items-center gap-2">
                            <item.icon
                                className="shrink-0"
                                isActive={isActive}
                            />
                            <span
                                className={cn(
                                    "flex-1 text-base leading-[1.375rem] tracking-[-0.007em] font-roboto font-normal",
                                    isActive && "font-bold",
                                )}
                            >
                                {item.label}
                            </span>
                            {item.badge && (
                                <span className="flex size-7 items-center justify-center rounded-full border border-action-button-text/50 bg-action-button-text/10 text-sm font-semibold text-action-button-text">
                                    {item.badge}
                                </span>
                            )}
                        </div>
                    )}
                </NavLink>
            ))}
        </nav>
    );
}
