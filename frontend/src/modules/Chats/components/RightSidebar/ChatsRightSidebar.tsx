import {
    SuggestedFriendsPanel,
    type SuggestedFriendsPanelProps,
} from "@/modules/Chats/components/panels/SuggestedFriendsPanel";

export type ChatsRightSidebarProps = SuggestedFriendsPanelProps;

export function ChatsRightSidebar(props: ChatsRightSidebarProps) {
    return <SuggestedFriendsPanel {...props} />;
}
