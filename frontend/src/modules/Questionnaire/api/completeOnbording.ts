import type { OnboardingPayload } from "./../types/questionnaire";

export async function completeOnboarding(
    payload: OnboardingPayload,
): Promise<void> {
    const response = await fetch("/api/profile/onboarding/complete", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(payload),
    });

    if (!response.ok) {
        // throw new Error("Failed to complete onboarding");

        return new Promise((resolve) => setTimeout(resolve, 500));
        // return new Promise((_, reject) => {
        //     setTimeout(() => {
        //         reject(new Error("Сервер временно недоступен. Попробуйте позже."));
        //     }, 1000);
        // });
    }
}
