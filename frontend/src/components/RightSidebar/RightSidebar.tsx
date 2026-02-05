import { Bell, Settings, Plus, MoreHorizontal, TrendingUp } from "lucide-react";
import { Avatar, IconButton } from "@/ui";
import { SectionHeader } from "@/components/SectionHeader";

interface SuggestedFriend {
  id: string;
  name: string;
  username: string;
  avatar?: string;
}

interface ProfileActivity {
  followersCount: number;
  growthPercent: number;
  period: string;
  message: string;
  recentFollowers: Array<{ id: string; avatar?: string }>;
}

interface CurrentUser {
  name: string;
  avatar?: string;
}

interface RightSidebarProps {
  currentUser?: CurrentUser;
  suggestedFriends?: SuggestedFriend[];
  profileActivity?: ProfileActivity;
}

const defaultCurrentUser: CurrentUser = {
  name: "Azunyan U. Wu",
  avatar: undefined,
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
  recentFollowers: [
    { id: "1" },
    { id: "2" },
    { id: "3" },
    { id: "4" },
    { id: "5" },
    { id: "6" },
  ],
};

export default function RightSidebar({
  currentUser = defaultCurrentUser,
  suggestedFriends = defaultSuggestedFriends,
  profileActivity = defaultProfileActivity,
}: RightSidebarProps) {
  return (
    <aside 
      className="bg-[#FEFEFE] flex flex-col shrink-0 overflow-y-auto h-full"
      style={{ width: '312px' }}
    >
      {/* Header */}
      <div 
        className="flex justify-between items-start bg-white"
        style={{ padding: '20px 24px', gap: '46px', height: '88px' }}
      >
        {/* Avatar with online indicator */}
        <Avatar
          src={currentUser.avatar || "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&h=100&fit=crop&crop=face"}
          alt={currentUser.name}
          size="md"
          showOnlineIndicator
          isOnline
        />

        {/* Action buttons */}
        <div className="flex gap-2">
          <IconButton size="md">
            <Bell className="w-7 h-7 text-[#333333]" />
          </IconButton>
          <IconButton size="md">
            <Settings className="w-7 h-7 text-[#333333]" />
          </IconButton>
        </div>
      </div>

      {/* Content Section */}
      <div 
        className="flex flex-col bg-[#FEFEFE]"
        style={{ padding: '0px 24px 24px', gap: '32px' }}
      >
        {/* Suggested Friends Section */}
        <div className="flex flex-col border-t border-[#EAECEE]">
          <SectionHeader
            title="Возможные друзья"
            linkText="Посмотреть"
            linkHref="/friends/suggestions"
          />

          {/* Friends List */}
          <div className="flex flex-col">
            {suggestedFriends.map((friend) => (
              <div 
                key={friend.id} 
                className="flex items-center bg-[#FEFEFE] border-b border-[#EAECEE]"
                style={{ padding: '12px 0px', gap: '12px', height: '72px', minHeight: '72px' }}
              >
                {/* Avatar */}
                <Avatar
                  src={friend.avatar}
                  alt={friend.name}
                  size="sm"
                  fallback={
                    <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-purple-400 to-pink-500 text-white text-sm font-medium">
                      {friend.name.charAt(0)}
                    </div>
                  }
                  className="shrink-0"
                  style={{ width: '40px', height: '40px' }}
                />
                
                {/* Name and username */}
                <div 
                  className="flex flex-col justify-center flex-1"
                  style={{ width: '180px', height: '42px' }}
                >
                  <p 
                    className="font-roboto font-bold text-[#333333] truncate"
                    style={{ fontSize: '14px', lineHeight: '20px', letterSpacing: '-0.006em' }}
                  >
                    {friend.name}
                  </p>
                  <p 
                    className="font-roboto font-normal text-[#126DF7] truncate"
                    style={{ fontSize: '14px', lineHeight: '22px' }}
                  >
                    @{friend.username}
                  </p>
                </div>
                
                {/* Add button */}
                <button className="text-[rgba(51,51,51,0.5)] hover:text-[#333333] transition-colors">
                  <Plus className="w-5 h-5" />
                </button>
              </div>
            ))}
          </div>
        </div>

        {/* Profile Activity Section */}
        <div className="flex flex-col" style={{ gap: '20px' }}>
          {/* Header */}
          <div className="flex items-center" style={{ gap: '16px' }}>
            <h3 
              className="flex-1 font-roboto font-bold text-[#333333]"
              style={{ fontSize: '18px', lineHeight: '24px', letterSpacing: '-0.008em' }}
            >
              Активность профиля
            </h3>
            <button className="text-[rgba(51,51,51,0.5)] hover:text-[#333333] transition-colors">
              <MoreHorizontal className="w-6 h-6" />
            </button>
          </div>

          {/* Activity Card */}
          <div 
            className="flex flex-col bg-[#EAECEE] border border-[#EAECEE]"
            style={{ padding: '24px 16px', gap: '20px', borderRadius: '24px' }}
          >
            {/* Avatar Group */}
            <div className="flex" style={{ marginLeft: '12px' }}>
              {profileActivity.recentFollowers.slice(0, 7).map((follower, index) => (
                <div
                  key={follower.id}
                  className="rounded-full border-2 border-[#FEFEFE] overflow-hidden"
                  style={{ 
                    width: '40px', 
                    height: '40px',
                    marginLeft: index === 0 ? '-12px' : '-12px',
                    zIndex: 7 - index
                  }}
                >
                  {follower.avatar ? (
                    <img
                      src={follower.avatar}
                      alt=""
                      className="w-full h-full object-cover"
                    />
                  ) : (
                    <div
                      className="w-full h-full flex items-center justify-center text-white text-xs font-medium"
                      style={{
                        background: `linear-gradient(135deg, hsl(${index * 50 + 200}, 70%, 55%), hsl(${index * 50 + 230}, 70%, 45%))`,
                      }}
                    >
                      {String.fromCharCode(65 + index)}
                    </div>
                  )}
                </div>
              ))}
            </div>

            {/* Stats */}
            <div className="flex flex-col" style={{ gap: '8px' }}>
              {/* Main stat row */}
              <div className="flex items-end" style={{ gap: '4px' }}>
                <span 
                  className="font-bold text-[#333333]"
                  style={{ fontFamily: 'Plus Jakarta Sans', fontSize: '24px', lineHeight: '32px', letterSpacing: '-0.012em' }}
                >
                  +{profileActivity.followersCount.toLocaleString()}
                </span>
                <span 
                  className="font-roboto font-medium text-[#333333]"
                  style={{ fontSize: '16px', lineHeight: '22px', letterSpacing: '-0.007em', paddingBottom: '2px' }}
                >
                  Подписчиков
                </span>
              </div>

              {/* Trend row */}
              <div className="flex items-center" style={{ gap: '4px' }}>
                <div className="flex items-center" style={{ gap: '4px' }}>
                  <TrendingUp className="w-5 h-5 text-[#22C55E]" />
                  <span 
                    className="font-roboto font-bold text-[#22C55E] text-center"
                    style={{ fontSize: '14px', lineHeight: '20px', letterSpacing: '-0.006em' }}
                  >
                    100%
                  </span>
                </div>
                <span 
                  className="font-roboto font-medium text-[#333333]"
                  style={{ fontSize: '16px', lineHeight: '22px', letterSpacing: '-0.007em' }}
                >
                  {profileActivity.period}
                </span>
              </div>
            </div>

            {/* Message */}
            <p 
              className="font-roboto font-medium text-[#333333]"
              style={{ fontSize: '16px', lineHeight: '22px', letterSpacing: '-0.007em' }}
            >
              {profileActivity.message}
            </p>
          </div>
        </div>
      </div>
    </aside>
  );
}


