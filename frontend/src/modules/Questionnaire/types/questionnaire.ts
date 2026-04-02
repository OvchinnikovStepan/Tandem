export interface QuestionnaireResponse {
    questionId: string;
    answer: string | string[];
}

export interface OnboardingPayload {
    responses: QuestionnaireResponse[];
}
