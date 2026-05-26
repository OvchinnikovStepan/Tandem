import type { CurrentUser } from "../../types/types.ts";
import { LogOut } from "lucide-react";
import Button from "@/ui/Button.tsx";
import { NavLink } from "react-router";
import { UserAvatar } from "@/components/UserAvatar";

type LeftSidebarFooterProps = {
    currentUser?: CurrentUser;
    onLogout?: () => void;
};

function LeftSidebarFooter({ currentUser, onLogout }: LeftSidebarFooterProps) {
    return (
        <div className="flex mt-auto justify-between border-t border-accent-gray pt-4">
            <NavLink
                className="inline-flex gap-3 items-center text-md font-roboto font-medium transition-all ease-out duration-300 disabled:pointer-events-none disabled:opacity-50 [&_svg]:pointer-events-none shrink-0 outline-none"
                to="/profile"
            >
                <UserAvatar name={currentUser.name} size="md" />
                <span className="text-base font-bold">{currentUser.name}</span>
            </NavLink>
            <Button
                variant="custom"
                size="custom"
                aria-label="Выйти"
                className="text-icon-gray hover:text-heading-black"
                onClick={onLogout}
            >
                <LogOut className="size-7" />
            </Button>
        </div>
    );
}

export default LeftSidebarFooter;
