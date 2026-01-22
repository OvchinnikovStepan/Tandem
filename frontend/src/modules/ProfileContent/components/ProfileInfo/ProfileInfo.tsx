import { Settings, MapPin } from "lucide-react";
import { Button } from "@/ui/button";

interface UserProfile {
  id: string;
  name: string;
  avatar?: string;
  location: string;
  occupation: string;
  bio: string;
  postsCount: number;
  followersCount: number;
  friendsCount: number;
  isOwnProfile?: boolean;
}

interface ProfileInfoProps {
  user: UserProfile;
  onEditProfile?: () => void;
  onSettings?: () => void;
}

export default function ProfileInfo({ user, onEditProfile, onSettings }: ProfileInfoProps) {
  return (
    <div className="p-6 overflow-hidden">
      <div className="flex items-start gap-5">
        {/* Avatar */}
        <div className="w-28 h-28 min-w-[7rem] rounded-full overflow-hidden shrink-0 ring-4 ring-gray-100">
          {user.avatar ? (
            <img
              src={user.avatar}
              alt={user.name}
              className="w-full h-full object-cover"
            />
          ) : (
            <div className="w-full h-full flex items-center justify-center bg-gradient-to-br from-blue-400 to-blue-600 text-white text-3xl font-medium">
              {user.name.charAt(0)}
            </div>
          )}
        </div>

        {/* Info */}
        <div className="flex-1 min-w-0">
          {/* Name and actions */}
          <div className="flex flex-wrap items-center gap-2 mb-3">
            <h1 className="text-lg font-semibold text-gray-900 truncate">{user.name}</h1>
            {user.isOwnProfile && (
              <div className="flex items-center gap-2">
                <Button
                  variant="outline"
                  size="sm"
                  onClick={onEditProfile}
                  className="rounded-full text-xs font-medium whitespace-nowrap"
                >
                  Редактировать профиль
                </Button>
                <button
                  onClick={onSettings}
                  className="w-8 h-8 rounded-full border border-gray-200 flex items-center justify-center hover:bg-gray-50 transition-colors shrink-0"
                >
                  <Settings className="w-4 h-4 text-gray-600" />
                </button>
              </div>
            )}
          </div>

          {/* Stats */}
          <div className="flex flex-wrap items-center gap-x-5 gap-y-1 mb-3">
            <div className="text-sm whitespace-nowrap">
              <span className="font-semibold text-gray-900">{user.postsCount}</span>
              <span className="text-gray-500 ml-1">постов</span>
            </div>
            <div className="text-sm whitespace-nowrap">
              <span className="font-semibold text-gray-900">{user.followersCount}</span>
              <span className="text-gray-500 ml-1">подписчик</span>
            </div>
            <div className="text-sm whitespace-nowrap">
              <span className="font-semibold text-gray-900">{user.friendsCount}</span>
              <span className="text-gray-500 ml-1">друзей</span>
            </div>
          </div>

          {/* Bio */}
          <div className="space-y-0.5">
            <div className="flex items-center gap-1 text-sm text-gray-600">
              <MapPin className="w-4 h-4 shrink-0" />
              <span className="truncate">{user.location}</span>
            </div>
            <p className="text-sm text-blue-500 truncate">{user.occupation}</p>
            <p className="text-sm text-gray-700 line-clamp-2">{user.bio}</p>
          </div>
        </div>
      </div>
    </div>
  );
}

