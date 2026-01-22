import { Search, Plus } from "lucide-react";
import { Button } from "@/ui/button";

interface ProfileHeaderProps {
  onSearch?: (query: string) => void;
  onCreatePost?: () => void;
}

export default function ProfileHeader({ onSearch, onCreatePost }: ProfileHeaderProps) {
  return (
    <div className="bg-white rounded-2xl border border-gray-200 p-4 mb-6 overflow-hidden">
      <div className="flex items-center gap-3">
        {/* Search */}
        <div className="flex-1 min-w-0 relative">
          <Search className="absolute left-4 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
          <input
            type="text"
            placeholder="Поиск по вашим постам..."
            className="w-full h-11 pl-12 pr-4 rounded-full border border-gray-200 bg-white text-sm placeholder:text-gray-400 focus:outline-none focus:ring-2 focus:ring-primary/50 focus:border-primary transition-all"
            onChange={(e) => onSearch?.(e.target.value)}
          />
        </div>

        {/* Create Post Button */}
        <Button
          onClick={onCreatePost}
          className="h-11 px-4 rounded-full font-medium shrink-0 whitespace-nowrap"
        >
          Создать пост
          <Plus className="w-5 h-5 ml-1" />
        </Button>
      </div>
    </div>
  );
}

