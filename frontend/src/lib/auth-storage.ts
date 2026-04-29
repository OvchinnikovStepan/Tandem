/**
 * Хранение токенов авторизации.
 * Простое localStorage-обёртка.
 */

const ACCESS_KEY = "accessToken";
const REFRESH_KEY = "refreshToken";

export function setAuthTokens(accessToken: string, refreshToken: string) {
  if (typeof window === "undefined") return;
  window.localStorage.setItem(ACCESS_KEY, accessToken);
  window.localStorage.setItem(REFRESH_KEY, refreshToken);
}

export function clearAuthTokens() {
  if (typeof window === "undefined") return;
  window.localStorage.removeItem(ACCESS_KEY);
  window.localStorage.removeItem(REFRESH_KEY);
}

export function getAccessToken(): string | null {
  if (typeof window === "undefined") return null;
  return window.localStorage.getItem(ACCESS_KEY);
}

export function getRefreshToken(): string | null {
  if (typeof window === "undefined") return null;
  return window.localStorage.getItem(REFRESH_KEY);
}
