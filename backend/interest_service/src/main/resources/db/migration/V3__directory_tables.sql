CREATE TABLE user_directory (
    user_id UUID PRIMARY KEY,
    display_name VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE group_directory (
    group_id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_user_directory_display_name_lower ON user_directory (LOWER(display_name));
CREATE INDEX idx_group_directory_name_lower ON group_directory (LOWER(name));
