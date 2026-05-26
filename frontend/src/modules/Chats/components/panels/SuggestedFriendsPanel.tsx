import { Plus } from "lucide-react";
import Button from "@/ui/Button.tsx";
import AppLink from "@/ui/AppLink.tsx";
import { SidebarPanelHeader } from "@/components/SidebarPanelHeader";
import { UserAvatar } from "@/components/UserAvatar";
import type { SuggestedFriend } from "@/modules/Chats/types/chatTypes";

type ProfileActivity = {
    followersCount: number;
    growthPercent: number;
    period: string;
    message: string;
};

type CurrentUser = {
    name: string;
    avatarUrl?: string;
};

export type SuggestedFriendsPanelProps = {
    currentUser?: CurrentUser;
    suggestedFriends?: SuggestedFriend[];
    profileActivity?: ProfileActivity;
    onStartChat?: (friend: SuggestedFriend) => void;
};

const defaultCurrentUser: CurrentUser = {
    name: "Azunyan U. Wu",
};

const defaultSuggestedFriends: SuggestedFriend[] = [
    { id: "1", name: "Julia Smith", username: "juliasmith" },
    { id: "2", name: "Vermillion D. Gray", username: "vermilliongray" },
    { id: "3", name: "Mai Senpai", username: "maisenpai" },
    { id: "4", name: "Azunyan U. Wu", username: "azunyandesu" },
    { id: "5", name: "Oarack Babama", username: "obama21" },
];

const defaultProfileActivity: ProfileActivity = {
    followersCount: 1158,
    growthPercent: 23,
    period: "за месяц",
    message: "В этом месяце вы значительно увеличили число своих подписчиков!",
};

export function SuggestedFriendsPanel({
    currentUser = defaultCurrentUser,
    suggestedFriends = defaultSuggestedFriends,
    profileActivity = defaultProfileActivity,
    onStartChat,
}: SuggestedFriendsPanelProps) {
    return (
        <aside className="flex h-full w-[312px] shrink-0 flex-col bg-accent-white shadow-side-lines">
            <SidebarPanelHeader fallbackName={currentUser.name} />

            <div className="flex flex-1 flex-col gap-8 overflow-y-auto bg-accent-white px-6 pb-6 pt-4">
                <section className="flex flex-col border-b-2 border-accent-gray pb-4">
                    <div className="mb-3 flex items-center justify-between">
                        <h2 className="text-base font-roboto font-bold text-heading-black">
                            Схожие интересы
                        </h2>
                        <button
                            type="button"
                            className="text-sm font-roboto font-medium text-action-button-text hover:underline"
                        >
                            Посмотреть
                        </button>
                    </div>

                    <div className="flex flex-col">
                        {suggestedFriends.map((friend) => {
                            return (
                                <div
                                    key={friend.id}
                                    className="flex h-[72px] items-center gap-3 border-b border-accent-gray last:border-b-0"
                                >
                                    <UserAvatar name={friend.name} size="md" />
                                    <div className="flex min-w-0 flex-1 flex-col">
                                        <p className="truncate text-sm font-roboto font-bold text-heading-black">
                                            {friend.name}
                                        </p>
                                        <AppLink
                                            className="truncate text-sm font-roboto cursor-pointer"
                                            variant="action"
                                            href={`/user/${friend.id}`}
                                        >
                                            @{friend.username}
                                        </AppLink>
                                    </div>
                                    <button
                                        type="button"
                                        onClick={() => onStartChat?.(friend)}
                                        className="text-heading-black transition-colors hover:bg-heading-black/15 rounded-full p-1"
                                        aria-label={`Написать ${friend.name}`}
                                    >
                                        <Plus className="size-5" />
                                    </button>
                                </div>
                            );
                        })}
                    </div>
                </section>
                <section className="flex flex-col pb-4">
                    <div className="mb-3 flex items-center justify-center gap-5">
                        <h2 className="text-base font-roboto font-bold text-heading-black">
                            Группы по схожим интересам
                        </h2>
                        <Button
                            variant="secondary"
                            size="sm"
                            className="text-sm"
                        >
                            Найти
                        </Button>
                    </div>
                </section>
            </div>
        </aside>
    );
}
