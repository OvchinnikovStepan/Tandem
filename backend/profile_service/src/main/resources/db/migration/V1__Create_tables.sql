CREATE TABLE profiles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL UNIQUE,
    name VARCHAR(100),
    surname VARCHAR(100),
    phone_number VARCHAR(20),
    email VARCHAR(255),
    status VARCHAR(100),
    birthday DATE,
    city VARCHAR(100),
    place_of_work VARCHAR(200),
    job_title VARCHAR(100),
    personal_interests TEXT,
    onboarding_completed BOOLEAN DEFAULT FALSE,
    onboarding_completed_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_profiles_user_id ON profiles(user_id);
CREATE INDEX idx_profiles_name_surname ON profiles(name, surname);
CREATE INDEX idx_profiles_city ON profiles(city);
CREATE INDEX idx_profiles_place_of_work ON profiles(place_of_work);
CREATE INDEX idx_profiles_onboarding_completed ON profiles(onboarding_completed);

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

CREATE TABLE polls (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    version INTEGER NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    UNIQUE(name, version)
);

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
