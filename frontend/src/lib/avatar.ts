const DEFAULT_AVATAR_BASE_URL =
  "https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d";

export function getDefaultAvatarUrl(size: number = 100) {
  return `${DEFAULT_AVATAR_BASE_URL}?w=${size}&h=${size}&fit=crop&crop=face`;
}
