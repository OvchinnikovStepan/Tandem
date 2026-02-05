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
  return (
    <div 
      className="relative w-full"
      style={{ height: '176px', margin: '30px 20px 0 20px' }}
    >
      <div 
        className="absolute rounded-full overflow-hidden bg-[#FAFAFA] flex items-center justify-center"
        style={{ width: '150px', height: '150px', left: '0px', top: '13px' }}
      >
        {user.avatar ? (
          <img
            src={user.avatar}
            alt={user.name}
            className="w-full h-full object-cover rounded-full"
          />
        ) : (
          <img
            src="https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=200&h=200&fit=crop&crop=face"
            alt={user.name}
            className="w-full h-full object-cover rounded-full"
          />
        )}
      </div>

      <div 
        className="absolute"
        style={{ width: '613px', height: '176px', left: '321px', top: '0px' }}
      >

        <div 
          className="absolute flex items-center"
          style={{ gap: '15px', height: '32px', left: '0px', top: '4px' }}
        >
          <h1 
            className="font-roboto font-bold text-[#333333] flex items-center"
            style={{ fontSize: '16px', lineHeight: '22px', letterSpacing: '-0.007em' }}
          >
            {user.name}
          </h1>
          {user.isOwnProfile && (
            <button
              onClick={onEditProfile}
              className="font-roboto font-normal text-[#126DF7] bg-[#F1F3F6] flex items-center justify-center text-center"
              style={{ 
                fontSize: '14px', 
                lineHeight: '18px',
                padding: '7px 17px 7px 16px',
                height: '32px',
                borderRadius: '8px'
              }}
            >
              Редактировать профиль
            </button>
          )}
        </div>

        <div 
          className="absolute flex items-start"
          style={{ gap: '40px', height: '24px', left: '0px', top: '52px' }}
        >
          <span 
            className="font-roboto font-semibold text-[#262626] flex items-center"
            style={{ fontSize: '14px', lineHeight: '24px' }}
          >
            {user.postsCount} постов
          </span>
          <span 
            className="font-roboto font-semibold text-[#262626] flex items-center"
            style={{ fontSize: '15px', lineHeight: '24px' }}
          >
            {user.followersCount} подписчик
          </span>
          <span 
            className="font-roboto font-semibold text-[#262626] flex items-center"
            style={{ fontSize: '15px', lineHeight: '24px' }}
          >
            {user.friendsCount} друзей
          </span>
        </div>

        <div 
          className="absolute flex flex-col"
          style={{ width: '613px', height: '76px', left: '0px', top: '92px' }}
        >
          <div className="flex flex-col" style={{ paddingBottom: '4px' }}>
            <p 
              className="font-roboto font-semibold text-[#333333] flex items-center"
              style={{ fontSize: '14px', lineHeight: '18px' }}
            >
              {user.location}
            </p>
            <p 
              className="font-roboto font-medium text-[#747474] flex items-center"
              style={{ fontSize: '13px', lineHeight: '18px' }}
            >
              {user.occupation}
            </p>
            <p 
              className="font-roboto font-normal text-[#333333] flex items-center"
              style={{ fontSize: '14px', lineHeight: '18px' }}
            >
              {user.bio}
            </p>
          </div>
          <button 
            className="font-roboto font-normal text-[#126DF7] flex items-center hover:underline"
            style={{ fontSize: '14px', lineHeight: '18px', width: '84px' }}
          >
            Подробнее...
          </button>
        </div>
      </div>
    </div>
  );
}

