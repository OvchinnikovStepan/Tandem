import { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import { useAtomValue } from "jotai";
import { useInfiniteScroll } from "@/lib/hooks/useInfiniteScroll";
import { authAtom } from "@/modules/Auth";
import {
    ProfileInfo,
    ProfileTabs,
    PostsGrid,
    ProfileEdit,
    ProfileEditInterests,
    ProfileEditCareer,
    RightSidebarEdit,
    type EditSection,
    type TabType,
} from "@/modules/ProfileContent";
import { useAppShellSlots } from "@/modules/AppShell";
import { SuggestedFriendsPanel } from "@/modules/Chats/components/panels/SuggestedFriendsPanel.tsx";

interface EditUserProfile {
    id: string;
    firstName: string;
    lastName: string;
    username: string;
    gender: string;
    birthDate: string;
    bio: string;
    city: string;
    avatar?: string;
}

const generateMockPosts = (count: number) => {
    return Array.from({ length: count }, (_, i) => ({
        id: String(i + 1),
        imageUrl: undefined,
        type: i % 5 === 0 ? ("video" as const) : ("image" as const),
        likesCount: Math.floor(Math.random() * 1000),
        commentsCount: Math.floor(Math.random() * 100),
    }));
};

const POSTS_PER_PAGE = 9;

export default function Profile() {
    const { t } = useTranslation();
    const auth = useAtomValue(authAtom);
    const profileUser = useMemo(() => {
        const fallbackName = "Azunyan U. Wu";
        const fallbackFirstName = "U. Wu";
        const fallbackLastName = "Azunyan";
        const name =
            auth.user?.onboardingCompleted && auth.user.name
                ? auth.user.name
                : fallbackName;

        return {
            profileInfo: {
                id: auth.user?.id ?? "1",
                name,
                avatar: undefined,
                location:
                    auth.user?.onboardingCompleted && auth.user.city
                        ? auth.user.city
                        : t("profile.mock.location"),
                occupation: "Product Manager",
                bio: t("profile.mock.bio"),
                postsCount: 11,
                followersCount: 41,
                friendsCount: 17,
                isOwnProfile: true,
            },
            editUser: {
                id: auth.user?.id ?? "1",
                firstName:
                    auth.user?.onboardingCompleted && auth.user.firstName
                        ? auth.user.firstName
                        : fallbackFirstName,
                lastName:
                    auth.user?.onboardingCompleted && auth.user.lastName
                        ? auth.user.lastName
                        : fallbackLastName,
                username: (auth.user?.email?.split("@")[0] ?? "azunyan_0777")
                    .replace(/\s+/g, "_"),
                gender:
                    auth.user?.onboardingCompleted && auth.user.gender
                        ? auth.user.gender
                        : "male",
                birthDate:
                    auth.user?.onboardingCompleted && auth.user.birthDate
                        ? auth.user.birthDate
                        : "",
                bio: "",
                city:
                    auth.user?.onboardingCompleted && auth.user.city
                        ? auth.user.city
                        : t("profile.mock.city"),
                avatar: undefined,
            } satisfies EditUserProfile,
        };
    }, [auth.user, t]);

    const [activeTab, setActiveTab] = useState<TabType>("posts");
    const [currentPage, setCurrentPage] = useState(1);
    const [searchQuery, setSearchQuery] = useState("");
    const [isEditing, setIsEditing] = useState(false);
    const [editUser, setEditUser] = useState<EditUserProfile>(
        profileUser.editUser,
    );
    const [editSection, setEditSection] = useState<EditSection>("profile");
    const [isLoadingMore, setIsLoadingMore] = useState(false);

    const allPosts = useMemo(() => {
        switch (activeTab) {
            case "posts":
                return generateMockPosts(27);
            case "saved":
                return generateMockPosts(6);
            default:
                return [];
        }
    }, [activeTab]);

    const filteredPosts = useMemo(() => {
        if (!searchQuery) return allPosts;
        return allPosts;
    }, [allPosts, searchQuery]);

    const totalPages = Math.ceil(filteredPosts.length / POSTS_PER_PAGE);
    const hasMore = currentPage < totalPages;
    const paginatedPosts = useMemo(() => {
        return filteredPosts.slice(0, currentPage * POSTS_PER_PAGE);
    }, [filteredPosts, currentPage]);

    const { sentinelRef } = useInfiniteScroll({
        hasMore,
        isLoading: isLoadingMore,
        onLoadMore: () => {
            setIsLoadingMore(true);
            setCurrentPage((prev) => prev + 1);
        },
    });

    useEffect(() => {
        setIsLoadingMore(false);
    }, [currentPage, activeTab, searchQuery]);

    useEffect(() => {
        setEditUser(profileUser.editUser);
    }, [profileUser]);

    const handleTabChange = (tab: TabType) => {
        setActiveTab(tab);
        setCurrentPage(1);
    };

    const handleStartEditing = () => {
        setEditSection("profile");
        setIsEditing(true);
    };

    const handleStopEditing = () => {
        setIsEditing(false);
        setEditSection("profile");
    };

    const { clearTopBarContent, setRightPanel, closeRightPanel } =
        useAppShellSlots();

    useEffect(() => {
        clearTopBarContent();

        if (isEditing) {
            setRightPanel({
                isOpen: true,
                content: (
                    <RightSidebarEdit
                        activeSection={editSection}
                        onSectionChange={setEditSection}
                        currentUser={{
                            name: profileUser.profileInfo.name,
                        }}
                    />
                ),
            });
        } else {
            setRightPanel({
                isOpen: true,
                content: <SuggestedFriendsPanel />,
            });
        }

        return () => {
            closeRightPanel();
        };
    }, [
        isEditing,
        editSection,
        profileUser.profileInfo.name,
        clearTopBarContent,
        setRightPanel,
        closeRightPanel,
    ]);

    return (
        <div className="flex h-[calc(100vh-88px)] min-h-0 max-h-[calc(100vh-88px)] min-w-0 flex-1 flex-col overflow-hidden overflow-y-auto no-scrollbar">
            <div
                className="flex justify-center"
                style={{ padding: "24px 32px", gap: "16px" }}
            >
                {isEditing ? (
                    editSection === "profile" ? (
                        <ProfileEdit
                            user={editUser}
                            onSave={(updatedUser) => {
                                setEditUser(updatedUser);
                                handleStopEditing();
                                console.log("Saved user:", updatedUser);
                            }}
                            onCancel={handleStopEditing}
                        />
                    ) : editSection === "interests" ? (
                        <ProfileEditInterests
                            onSave={(interests) => {
                                console.log("Saved interests:", interests);
                            }}
                        />
                    ) : (
                        <ProfileEditCareer
                            onSave={(career) => {
                                console.log("Saved career:", career);
                            }}
                        />
                    )
                ) : (
                    <div
                        className="w-full overflow-hidden bg-accent-white"
                        style={{
                            boxShadow: "0px 2px 8px rgba(0, 0, 0, 0.25)",
                            maxWidth: "975px",
                            borderRadius: "24px",
                        }}
                    >
                        <ProfileInfo
                            user={profileUser.profileInfo}
                            onEditProfile={handleStartEditing}
                            onSettings={() => console.log("Settings")}
                        />

                        <div style={{ marginTop: "44px" }}>
                            <ProfileTabs
                                activeTab={activeTab}
                                onTabChange={handleTabChange}
                            />
                        </div>

                        <div style={{ padding: "0 20px 20px 20px" }}>
                            <PostsGrid posts={paginatedPosts} />
                            <div ref={sentinelRef} className="h-8" />
                            {isLoadingMore && (
                                <div className="flex justify-center py-4 text-sm text-gray-400">
                                    {t("profile.posts.loading")}
                                </div>
                            )}
                        </div>
                    </div>
                )}
            </div>
        </div>
    );
}
