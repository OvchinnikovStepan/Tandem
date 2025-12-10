CREATE TABLE privacy_settings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE REFERENCES profiles(user_id) ON DELETE CASCADE,
    show_phone_number BOOLEAN DEFAULT FALSE,
    show_email BOOLEAN DEFAULT FALSE,
    show_city BOOLEAN DEFAULT TRUE,
    show_place_of_work BOOLEAN DEFAULT TRUE,
    show_job_title BOOLEAN DEFAULT TRUE,
    show_birthday BOOLEAN DEFAULT FALSE,
    show_personal_interests BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_privacy_settings_user_id ON privacy_settings(user_id);