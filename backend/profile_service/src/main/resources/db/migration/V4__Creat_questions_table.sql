CREATE TABLE questions (
    id UUID PRIMARY KEY,
    poll_id UUID REFERENCES polls(id),
    question_order INTEGER NOT NULL,
    question_type VARCHAR(50) NOT NULL, -- text, textarea, select, multiselect
    label TEXT NOT NULL,
    description TEXT,
    is_required BOOLEAN DEFAULT FALSE,
    validation_rules JSONB,
    options JSONB, -- For select/multiselect types
    created_at TIMESTAMP NOT NULL,
    profile_field VARCHAR(50), -- Maps to profiles table column (e.g., 'name', 'surname', 'city')
    UNIQUE (poll_id, question_order)
);

CREATE INDEX idx_questions_poll_id ON questions (poll_id);
