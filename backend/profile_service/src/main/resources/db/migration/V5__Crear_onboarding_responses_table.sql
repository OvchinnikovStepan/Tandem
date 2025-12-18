CREATE TABLE onboarding_responses (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES profiles(user_id),
    poll_id UUID REFERENCES polls(id),
    question_id UUID REFERENCES questions(id),
    answer_text TEXT,
    answer_array TEXT[], -- For multiselect
    created_at TIMESTAMP NOT NULL,
    UNIQUE (user_id, poll_id, question_id)
);

CREATE INDEX idx_onboarding_responses_user_id ON onboarding_responses (user_id);
CREATE INDEX idx_onboarding_responses_poll_id ON onboarding_responses (poll_id);
