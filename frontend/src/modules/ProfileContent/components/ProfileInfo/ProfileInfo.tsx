import { getDefaultAvatarUrl } from "@/lib/avatar";
import { useTranslation } from "react-i18next";
import { Button } from "@/ui";

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

export default function ProfileInfo({ user, onEditProfile }: ProfileInfoProps) {
  const { t } = useTranslation();

  return (
    <div 
      className="flex w-full gap-8"
      style={{ padding: '30px 20px 0 20px' }}
    >
      <div 
        className="rounded-full overflow-hidden bg-avatar-bg flex items-center justify-center shrink-0"
        style={{ width: '150px', height: '150px' }}
      >
        <img
          src={user.avatar || getDefaultAvatarUrl(200)}
          alt={user.name}
          className="w-full h-full object-cover rounded-full"
        />
      </div>

      <div className="flex flex-col flex-1 gap-4">

        <div className="flex items-center gap-4">
          <h1 
            className="font-roboto font-bold text-heading-black text-body-md"
          >
            {user.name}
          </h1>
          {user.isOwnProfile && (
            <Button
              variant="secondary"
              size="sm"
              onClick={onEditProfile}
            >
              {t("profile.info.editProfile")}
            </Button>
          )}
        </div>

        <div className="flex items-start gap-10">
          <span className="font-roboto font-semibold text-text-dark text-body-sm">
            {user.postsCount} {t("profile.info.posts")}
          </span>
          <span className="font-roboto font-semibold text-text-dark text-body-sm">
            {user.followersCount} {t("profile.info.followers")}
          </span>
          <span className="font-roboto font-semibold text-text-dark text-body-sm">
            {user.friendsCount} {t("profile.info.friends")}
          </span>
        </div>

        <div className="flex flex-col">
          <p className="font-roboto font-semibold text-heading-black text-body-sm">
            {user.location}
          </p>
          <p className="font-roboto font-medium text-text-muted text-body-sm" style={{ fontSize: '13px' }}>
            {user.occupation}
          </p>
          <p className="font-roboto font-normal text-heading-black text-body-sm">
            {user.bio}
          </p>
          <Button
            variant="link"
            className="p-0 h-auto font-roboto font-normal text-header-button-text text-body-sm justify-start"
          >
            {t("profile.info.more")}
          </Button>
        </div>
      </div>
    </div>
  );
}

