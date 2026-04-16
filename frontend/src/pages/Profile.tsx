import { useEffect, useMemo, useState } from "react";
import { useTranslation } from "react-i18next";
import LeftSidebar from "@/components/LeftSidebar";
import RightSidebar, { RightSidebarEdit, EditSection } from "@/components/RightSidebar";
import { useInfiniteScroll } from "@/lib/hooks/useInfiniteScroll";
import {
  ProfileHeader,
  ProfileInfo,
  ProfileTabs,
  PostsGrid,
  ProfileEdit,
  ProfileEditInterests,
  ProfileEditCareer,
  TabType,
} from "@/modules/ProfileContent";

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
    type: i % 5 === 0 ? "video" as const : "image" as const,
    likesCount: Math.floor(Math.random() * 1000),
    commentsCount: Math.floor(Math.random() * 100),
  }));
};

const POSTS_PER_PAGE = 9;

export default function Profile() {
  const { t } = useTranslation();
  const mockUser = {
    id: "1",
    name: "Azunyan U. Wu",
    avatar: undefined,
    location: t("profile.mock.location"),
    occupation: "Product Manager",
    bio: t("profile.mock.bio"),
    postsCount: 11,
    followersCount: 41,
    friendsCount: 17,
    isOwnProfile: true,
  };
  const mockEditUser: EditUserProfile = {
    id: "1",
    firstName: "U. Wu",
    lastName: "Azunyan",
    username: "azunyan_0777",
    gender: "male",
    birthDate: "",
    bio: "",
    city: t("profile.mock.city"),
    avatar: undefined,
  };

  const [activeTab, setActiveTab] = useState<TabType>("posts");
  const [currentPage, setCurrentPage] = useState(1);
  const [searchQuery, setSearchQuery] = useState("");
  const [isEditing, setIsEditing] = useState(false);
  const [editUser, setEditUser] = useState<EditUserProfile>(mockEditUser);
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


  const handleTabChange = (tab: TabType) => {
    setActiveTab(tab);
    setCurrentPage(1);
  };

  const handleSearch = (query: string) => {
    setSearchQuery(query);
    setCurrentPage(1);
  };

  return (
    <div className="h-screen w-screen bg-landing-bg flex overflow-hidden">
      <LeftSidebar />

      <main className="flex-1 flex flex-col overflow-y-auto no-scrollbar min-w-0">
        <ProfileHeader
          onSearch={handleSearch}
          onCreatePost={() => console.log("Create post")}
        />

        <div 
          className="flex justify-center flex-1"
          style={{ padding: '24px 32px', gap: '16px' }}
        >
          {isEditing ? (
            editSection === "profile" ? (
              <ProfileEdit
                user={editUser}
                onSave={(updatedUser) => {
                  setEditUser(updatedUser);
                  setIsEditing(false);
                  console.log("Saved user:", updatedUser);
                }}
                onCancel={() => setIsEditing(false)}
              />
            ) : editSection === "interests" ? (
              <ProfileEditInterests
                onSave={(interests) => {
                  console.log("Saved interests:", interests);
                }}
                onBack={() => setEditSection("profile")}
              />
            ) : (
              <ProfileEditCareer
                onSave={(career) => {
                  console.log("Saved career:", career);
                }}
                onBack={() => setEditSection("profile")}
              />
            )
          ) : (
            <div 
              className="bg-accent-white overflow-hidden w-full"
              style={{ 
                boxShadow: '0px 2px 8px rgba(0, 0, 0, 0.25)',
                maxWidth: '975px',
                borderRadius: '24px'
              }}
            >

              <ProfileInfo
                user={mockUser}
                onEditProfile={() => setIsEditing(true)}
                onSettings={() => console.log("Settings")}
              />

              <div style={{ marginTop: '44px' }}>
                <ProfileTabs activeTab={activeTab} onTabChange={handleTabChange} />
              </div>

              <div style={{ padding: '0 20px 20px 20px' }}>
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
        </main>

        {isEditing ? (
          <RightSidebarEdit
            activeSection={editSection}
            onSectionChange={setEditSection}
          />
        ) : (
          <RightSidebar />
        )}
    </div>
  );
}

