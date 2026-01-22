import { useState } from "react";
import { Heart, MessageCircle, Play } from "lucide-react";
import { cn } from "@/lib/utils";

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
  const [isHovered, setIsHovered] = useState(false);

  return (
    <div
      className="relative aspect-square bg-gray-200 rounded-lg overflow-hidden cursor-pointer group"
      onMouseEnter={() => setIsHovered(true)}
      onMouseLeave={() => setIsHovered(false)}
    >
      {post.imageUrl && (
        <img
          src={post.imageUrl}
          alt=""
          className="w-full h-full object-cover"
        />
      )}
      
      {/* Video indicator */}
      {post.type === "video" && (
        <div className="absolute top-3 right-3">
          <Play className="w-5 h-5 text-white drop-shadow-lg" fill="white" />
        </div>
      )}

      {/* Hover overlay */}
      <div
        className={cn(
          "absolute inset-0 bg-black/40 flex items-center justify-center gap-6 transition-opacity",
          isHovered ? "opacity-100" : "opacity-0"
        )}
      >
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

function PostSkeleton() {
  return (
    <div className="aspect-square bg-gray-200 rounded-lg animate-pulse" />
  );
}

export default function PostsGrid({ posts, isLoading }: PostsGridProps) {
  if (isLoading) {
    return (
      <div className="grid grid-cols-3 gap-1 mt-1">
        {Array.from({ length: 9 }).map((_, index) => (
          <PostSkeleton key={index} />
        ))}
      </div>
    );
  }

  if (posts.length === 0) {
    return (
      <div className="flex flex-col items-center justify-center py-16 text-gray-400">
        <p className="text-lg">Пока нет постов</p>
        <p className="text-sm mt-1">Создайте свой первый пост!</p>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-3 gap-1 mt-1">
      {posts.map((post) => (
        <PostCard key={post.id} post={post} />
      ))}
    </div>
  );
}


