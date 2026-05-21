CREATE TABLE tags (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE,
    image_url VARCHAR(500));
CREATE INDEX idx_tags_name ON tags(name);

CREATE TABLE user_interests (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL, -- References Auth Service user
    tag_id UUID REFERENCES tags(id),
    created_at TIMESTAMP NOT NULL,
    UNIQUE(user_id, tag_id));
CREATE INDEX idx_user_interests_user_id ON user_interests(user_id);
CREATE INDEX idx_user_interests_tag_id ON user_interests(tag_id);

CREATE TABLE group_tags (
    id UUID PRIMARY KEY,
    group_id UUID NOT NULL,
    tag_id UUID REFERENCES tags(id),
    created_at TIMESTAMP NOT NULL,
    UNIQUE(group_id, tag_id)
);

CREATE INDEX idx_group_tags_group_id ON group_tags(group_id);

-- Статистика использования тегов
CREATE MATERIALIZED VIEW tag_usage_stats AS
SELECT
    t.id as tag_id,
    t.name as tag_name,
    COUNT(ui.id) as usage_count,
    COUNT(DISTINCT ui.user_id) as unique_users
FROM tags t
LEFT JOIN user_interests ui ON t.id = ui.tag_id
GROUP BY t.id, t.name;
CREATE UNIQUE INDEX idx_tag_usage_stats_tag_id ON tag_usage_stats(tag_id);
