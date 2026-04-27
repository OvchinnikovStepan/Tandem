CREATE TABLE groups (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    avatar_url TEXT,
    creator_id UUID NOT NULL,           -- References users (Auth Service)
    visibility VARCHAR(20) DEFAULT 'public' -- 'public' or 'private'
);

CREATE INDEX idx_groups_creator_id ON groups(creator_id);
CREATE INDEX idx_groups_visibility ON groups(visibility);

CREATE TABLE chats (
    id UUID PRIMARY KEY,
    group_id UUID NULL, -- только для групповых чатов, для личных null
    created_at TIMESTAMP NOT NULL,
    last_message_at TIMESTAMP, -- время отправки последнего сообщения
    CONSTRAINT fk_chats_group FOREIGN KEY (group_id)
        REFERENCES groups(id) ON DELETE CASCADE,
    CONSTRAINT unique_group_chat UNIQUE (group_id) -- одна группа = один чат
);

CREATE INDEX idx_chats_last_message_at ON chats(last_message_at);

-- Участники чатов
CREATE TABLE chat_participants (
    id UUID PRIMARY KEY,
    chat_id UUID REFERENCES chats(id) ON DELETE CASCADE,
    user_id UUID NOT NULL,              -- References Auth Service
    role VARCHAR(20) DEFAULT 'member', -- 'member', 'admin' (в личный чатах все admin)
    is_muted BOOLEAN DEFAULT FALSE,     -- мьют уведомлений
    is_banned BOOLEAN DEFAULT FALSE,    -- только для групп
    joined_at TIMESTAMP NOT NULL,
    exited_at TIMESTAMP,
    UNIQUE(chat_id, user_id)
);

CREATE INDEX idx_chat_participants_user_id ON chat_participants(user_id);
CREATE INDEX idx_chat_participants_chat_id ON chat_participants(chat_id);
CREATE INDEX idx_chat_participants_role ON chat_participants(role) WHERE role = 'admin';

CREATE TABLE messages (
    id UUID PRIMARY KEY,
    chat_id UUID REFERENCES chats(id) ON DELETE CASCADE,
    sender_id UUID NOT NULL, -- References Auth Service user
    content TEXT,
    message_type VARCHAR(20) NOT NULL, -- 'text', 'file', 'link', 'code', 'emoji', 'sticker'
    metadata JSONB,
    sent_at TIMESTAMP NOT NULL, -- время отправки
    delivered_at TIMESTAMP, -- время доставки получателю
    is_read BOOLEAN DEFAULT FALSE,  -- прочитано/не прочитано
    deleted_at TIMESTAMP -- Soft delete
);

CREATE INDEX idx_messages_chat_id ON messages(chat_id);
CREATE INDEX idx_messages_sender_id ON messages(sender_id);
CREATE INDEX idx_messages_sent_at ON messages(sent_at);
CREATE INDEX idx_messages_chat_sent_at ON messages(chat_id, sent_at);

CREATE TABLE group_requests (
    id UUID PRIMARY KEY,
    group_id UUID NOT NULL REFERENCES groups(id) ON DELETE CASCADE,
    user_id UUID NOT NULL, -- кто просит вступить
    requested_by UUID NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'pending', -- pending, approved, rejected, cancelled
    message TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    expires_at TIMESTAMP NOT NULL,
    reviewed_at TIMESTAMP,
    reviewed_by UUID,
    UNIQUE(group_id, user_id)
);
