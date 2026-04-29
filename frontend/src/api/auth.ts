import { apiRequest } from "./client";

export type PasswordStrength = "WEAK" | "FAIR" | "GOOD" | "STRONG";

export interface RegisterPhoneResponse {
  verificationId: string;
}

export interface VerifyPhoneResponse {
  verified: boolean;
}

export interface RegisterEmailResponse {
  userId: string;
  accessToken: string;
  refreshToken: string;
  passwordStrength: PasswordStrength;
}

export interface CheckPasswordStrengthResponse {
  strength: PasswordStrength;
  score: number;
  warnings: string[];
  suggestions: Record<string, string>;
}

/**
 * Шаг 1 регистрации: создание заявки по номеру телефона.
 * Возвращает verificationId, который нужно использовать на следующих шагах.
 */
export function registerPhone(phoneNumber: string) {
  return apiRequest<RegisterPhoneResponse>("/auth/register/phone", {
    method: "POST",
    body: { phoneNumber },
  });
}

/**
 * Шаг 2 регистрации: подтверждение кода из SMS.
 */
export function verifyPhone(verificationId: string, code: string) {
  return apiRequest<VerifyPhoneResponse>("/auth/register/verify", {
    method: "POST",
    body: { verificationId, code },
  });
}

/**
 * Шаг 3 регистрации: завершение регистрации (email + password).
 * Возвращает access/refresh токены.
 */
export function registerEmail(
  verificationId: string,
  email: string,
  password: string,
) {
  return apiRequest<RegisterEmailResponse>("/auth/register/email", {
    method: "POST",
    body: { verificationId, email, password },
  });
}

/**
 * Серверная проверка надёжности пароля.
 */
export function checkPasswordStrength(password: string) {
  return apiRequest<CheckPasswordStrengthResponse>(
    "/auth/password/check-strength",
    {
      method: "POST",
      body: { password },
    },
  );
}
