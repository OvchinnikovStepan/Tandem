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