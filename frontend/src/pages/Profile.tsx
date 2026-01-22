import { useState, useMemo } from "react";
import LeftSidebar from "@/components/LeftSidebar";
import RightSidebar from "@/components/RightSidebar";
import {
  ProfileHeader,
  ProfileInfo,
  ProfileTabs,
  PostsGrid,
  Pagination,
  TabType,
} from "@/modules/ProfileContent";

const mockUser = {
  id: "1",
  name: "Azunyan U. Wu",
  avatar: undefined,
  location: "Омск",
  occupation: "Product Manager",
  bio: "Люблю путешествовать по миру 🌍",
  postsCount: 11,
  followersCount: 41,
  friendsCount: 17,
  isOwnProfile: true,
};

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
  const [activeTab, setActiveTab] = useState<TabType>("posts");
  const [currentPage, setCurrentPage] = useState(1);
  const [searchQuery, setSearchQuery] = useState("");

  // Mock posts data based on active tab
  const allPosts = useMemo(() => {
    switch (activeTab) {
      case "posts":
        return generateMockPosts(27); // 3 pages
      case "videos":
        return generateMockPosts(12).map(p => ({ ...p, type: "video" as const }));
      case "saved":
        return generateMockPosts(6);
      default:
        return [];
    }
  }, [activeTab]);

  // Filter posts by search query (mock implementation)
  const filteredPosts = useMemo(() => {
    if (!searchQuery) return allPosts;
    // In real implementation, this would filter by post content
    return allPosts;
  }, [allPosts, searchQuery]);

  // Paginate posts
  const totalPages = Math.ceil(filteredPosts.length / POSTS_PER_PAGE);
  const paginatedPosts = useMemo(() => {
    const start = (currentPage - 1) * POSTS_PER_PAGE;
    return filteredPosts.slice(start, start + POSTS_PER_PAGE);
  }, [filteredPosts, currentPage]);

  // Reset page when tab changes
  const handleTabChange = (tab: TabType) => {
    setActiveTab(tab);
    setCurrentPage(1);
  };

  const handleSearch = (query: string) => {
    setSearchQuery(query);
    setCurrentPage(1);
  };

  return (
    <div className="min-h-screen bg-gray-50 flex">
      {/* Left Sidebar */}
      <LeftSidebar />

      {/* Main Content */}
      <main className="flex-1 min-w-0 overflow-hidden">
        <div className="max-w-3xl mx-auto px-4 py-6">
          {/* Header with search and create post */}
          <ProfileHeader
            onSearch={handleSearch}
            onCreatePost={() => console.log("Create post")}
          />

          {/* Profile Content with border */}
          <div className="bg-white rounded-2xl border border-gray-200 overflow-hidden">
            {/* Profile Info */}
            <ProfileInfo
              user={mockUser}
              onEditProfile={() => console.log("Edit profile")}
              onSettings={() => console.log("Settings")}
            />

            {/* Tabs */}
            <ProfileTabs activeTab={activeTab} onTabChange={handleTabChange} />

            {/* Posts Grid */}
            <div className="p-4">
              <PostsGrid posts={paginatedPosts} />

              {/* Pagination */}
              <Pagination
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={setCurrentPage}
              />
            </div>
          </div>
        </div>
      </main>

      {/* Right Sidebar */}
      <RightSidebar />
    </div>
  );
}

