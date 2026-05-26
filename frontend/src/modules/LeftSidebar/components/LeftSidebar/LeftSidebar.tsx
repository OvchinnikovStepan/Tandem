import type { CurrentUser } from "@/modules/LeftSidebar/types/types.ts";
import { LeftSidebarNavigation } from "@/modules/LeftSidebar/components/LeftSidebarNavigation/LeftSidebarNavigation.tsx";
import LeftSidebarFooter from "@/modules/LeftSidebar/components/LeftSidebarFooter/LeftSidebarFooter.tsx";

type LeftSidebarProps = {
    currentUser?: CurrentUser;
    onLogout?: () => void;
};

const defaultUser: CurrentUser = {
    name: "Azunyan U. Wu",
};

function LeftSidebar({
    currentUser = defaultUser,
    onLogout,
}: LeftSidebarProps) {
    return (
        <aside className="flex shrink-0 flex-col w-78 bg-accent-white px-4 py-8 shadow-side-lines">
            {/*<div className="mx-auto flex w-70 flex-1 flex-col items-start gap-8">*/}
            {/*    <div className="flex w-full flex-col border-b border-accent-gray min-h-14">*/}
            {/*        <div className="flex items-center justify-center">*/}
            {/*            <img*/}
            {/*                src="/logo/Black_Horiz_Logo.svg"*/}
            {/*                alt="Tandem"*/}
            {/*                draggable="false"*/}
            {/*                className="flex max-h-11"*/}
            {/*            />*/}
            {/*        </div>*/}
            {/*    </div>*/}
            {/*</div>*/}
            <div className="mx-auto flex w-full items-start justify-center border-b border-accent-gray min-h-14 mb-8">
                <img
                    src="/logo/Black_Horiz_Logo.svg"
                    alt="Tandem"
                    draggable="false"
                    className="flex max-h-11"
                />
            </div>
            <LeftSidebarNavigation />
            <LeftSidebarFooter currentUser={currentUser} onLogout={onLogout} />
        </aside>
    );
}

export default LeftSidebar;
