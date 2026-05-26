export interface Post {
    id: string;
    imageUrl?: string;
    videoUrl?: string;
    type: "image" | "video";
    likesCount: number;
    commentsCount: number;
}
