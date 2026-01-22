import { MessageCircle, Bell, Settings, Plus, MoreHorizontal, TrendingUp, ExternalLink } from "lucide-react";
import { Link } from "react-router-dom";

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
    <aside className="w-72 h-screen bg-white border-l border-gray-200 flex flex-col sticky top-0 overflow-y-auto">
      <div className="p-4 flex items-center justify-between">
        <div className="w-10 h-10 rounded-full bg-gray-200 overflow-hidden">
          {currentUser.avatar ? (
            <img
              src={currentUser.avatar}
              alt={currentUser.name}
              className="w-full h-full object-cover"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-blue-400 to-blue-600 text-white font-medium">
              {currentUser.name.charAt(0)}
            </div>
          )}
        </div>
        <div className="flex items-center gap-2">
          <button className="w-10 h-10 rounded-full border border-gray-200 flex items-center justify-center hover:bg-gray-50 transition-colors">
            <MessageCircle className="w-5 h-5 text-gray-600" />
          </button>
          <button className="w-10 h-10 rounded-full border border-gray-200 flex items-center justify-center hover:bg-gray-50 transition-colors">
            <Bell className="w-5 h-5 text-gray-600" />
          </button>
          <button className="w-10 h-10 rounded-full border border-gray-200 flex items-center justify-center hover:bg-gray-50 transition-colors">
            <Settings className="w-5 h-5 text-gray-600" />
          </button>
        </div>
      </div>

      <div className="p-4">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-sm font-medium text-gray-900">Возможные друзья</h3>
          <Link
            to="/friends/suggestions"
            className="text-sm text-blue-500 hover:text-blue-600 flex items-center gap-1"
          >
            Посмотреть
            <ExternalLink className="w-3 h-3" />
          </Link>
        </div>
        <ul className="space-y-3">
          {suggestedFriends.map((friend) => (
            <li key={friend.id} className="flex items-center gap-3">
              <div className="w-10 h-10 rounded-full bg-gray-200 overflow-hidden shrink-0">
                {friend.avatar ? (
                  <img
                    src={friend.avatar}
                    alt={friend.name}
                    className="w-full h-full object-cover"
                  />
                ) : (
                  <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-purple-400 to-pink-500 text-white text-sm font-medium">
                    {friend.name.charAt(0)}
                  </div>
                )}
              </div>
              <div className="flex-1 min-w-0">
                <p className="text-sm font-medium text-gray-900 truncate">
                  {friend.name}
                </p>
                <p className="text-xs text-blue-500 truncate">@{friend.username}</p>
              </div>
              <button className="p-1.5 text-gray-400 hover:text-gray-600 transition-colors">
                <Plus className="w-5 h-5" />
              </button>
            </li>
          ))}
        </ul>
      </div>

      <div className="p-4">
        <div className="flex items-center justify-between mb-4">
          <h3 className="text-sm font-medium text-gray-900">Активность профиля</h3>
          <button className="p-1 text-gray-400 hover:text-gray-600 transition-colors">
            <MoreHorizontal className="w-5 h-5" />
          </button>
        </div>
        <div className="bg-gray-50 rounded-xl p-4">
          <div className="flex -space-x-2 mb-3">
            {profileActivity.recentFollowers.slice(0, 6).map((follower, index) => (
              <div
                key={follower.id}
                className="w-8 h-8 rounded-full border-2 border-white overflow-hidden"
                style={{ zIndex: 6 - index }}
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
                      background: `linear-gradient(135deg, hsl(${index * 60}, 70%, 50%), hsl(${index * 60 + 30}, 70%, 40%))`,
                    }}
                  >
                    {String.fromCharCode(65 + index)}
                  </div>
                )}
              </div>
            ))}
          </div>

          <div className="mb-2">
            <span className="text-2xl font-bold text-gray-900">
              +{profileActivity.followersCount.toLocaleString()}
            </span>
            <span className="text-sm text-gray-600 ml-1">Подписчиков</span>
          </div>
          <div className="flex items-center gap-1 mb-3">
            <TrendingUp className="w-4 h-4 text-green-500" />
            <span className="text-sm text-green-500 font-medium">
              {profileActivity.growthPercent}%
            </span>
            <span className="text-sm text-gray-500">{profileActivity.period}</span>
          </div>
          <p className="text-sm text-gray-600">{profileActivity.message}</p>
        </div>
      </div>
    </aside>
  );
}


