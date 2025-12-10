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
    UNIQUE(poll_id, question_order),
    INDEX idx_poll_id (poll_id)
);