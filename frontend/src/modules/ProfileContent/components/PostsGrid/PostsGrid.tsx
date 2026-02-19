import { Heart, MessageCircle, Play } from "lucide-react";

interface Post {
  id: string;
  imageUrl?: string;
  videoUrl?: string;
  type: "image" | "video";
  likesCount: number;
  commentsCount: number;
}

interface PostsGridProps {
  posts: Post[];
  isLoading?: boolean;
}

function PostCard({ post }: { post: Post }) {
  return (
    <div
      className="relative bg-[#C4C4C4] overflow-hidden cursor-pointer group aspect-square rounded-xl"
    >
      {post.imageUrl && (
        <img
          src={post.imageUrl}
          alt=""
          className="w-full h-full object-cover"
        />
      )}
      
      {post.type === "video" && (
        <div className="absolute top-3 right-3">
          <Play className="w-5 h-5 text-white drop-shadow-lg" fill="white" />
        </div>
      )}

      <div className="absolute inset-0 bg-black/40 flex items-center justify-center gap-6 opacity-0 transition-opacity group-hover:opacity-100">
        <div className="flex items-center gap-2 text-white">
          <Heart className="w-5 h-5" fill="white" />
          <span className="font-semibold">{post.likesCount}</span>
        </div>
        <div className="flex items-center gap-2 text-white">
          <MessageCircle className="w-5 h-5" fill="white" />
          <span className="font-semibold">{post.commentsCount}</span>
        </div>
      </div>
    </div>
  );
}

export default function PostsGrid({ posts, isLoading }: PostsGridProps) {
  if (isLoading) {
    return (
      <div className="grid grid-cols-3 gap-1 w-full">
        {Array.from({ length: 9 }).map((_, index) => (
          <div key={index} className="aspect-square bg-[#C4C4C4] rounded-xl" />
        ))}
      </div>
    );
  }

  if (posts.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-16 text-gray-400 w-full">
        <p className="text-lg font-roboto">Пока нет постов</p>
        <p className="text-sm mt-1 font-roboto">Создайте свой первый пост!</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-3 gap-1 w-full">
      {posts.map((post) => (
        <PostCard key={post.id} post={post} />
      ))}
    </div>
  );
}


