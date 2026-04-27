-- 1. Группы
INSERT INTO groups (id, name, description, avatar_url, creator_id, visibility) VALUES
('11111111-1111-1111-1111-111111111111', 'Tandem Developers', 'Main group for tandem development team', 'https://example.com/avatars/tandem.png', '123e4567-e89b-12d3-a456-426614174001', 'public'),
('22222222-2222-2222-2222-222222222222', 'Secret Project X', 'Closed discussion for managers', NULL, '123e4567-e89b-12d3-a456-426614174002', 'private');

-- 2. Чаты (2 групповых и 1 личный)
INSERT INTO chats (id, group_id, created_at, last_message_at) VALUES
('33333333-3333-3333-3333-333333333333', '11111111-1111-1111-1111-111111111111', NOW() - INTERVAL '2 days', NOW() - INTERVAL '1 hour'), -- Чат для первой группы
('44444444-4444-4444-4444-444444444444', '22222222-2222-2222-2222-222222222222', NOW() - INTERVAL '1 day', NULL), -- Чат для второй группы (пока без сообщений)
('55555555-5555-5555-5555-555555555555', NULL, NOW() - INTERVAL '5 hours', NOW()); -- Личный чат (group_id = NULL)

-- 3. Участники чатов
INSERT INTO chat_participants (id, chat_id, user_id, role, is_muted, is_banned, joined_at, exited_at) VALUES
-- Участники первого группового чата (Tandem Developers)
('66666666-6666-6666-6666-666666666666', '33333333-3333-3333-3333-333333333333', '123e4567-e89b-12d3-a456-426614174001', 'admin', FALSE, FALSE, NOW() - INTERVAL '2 days', NULL),
('77777777-7777-7777-7777-777777777777', '33333333-3333-3333-3333-333333333333', '123e4567-e89b-12d3-a456-426614174002', 'member', FALSE, FALSE, NOW() - INTERVAL '1 day', NULL),
('88888888-8888-8888-8888-888888888888', '33333333-3333-3333-3333-333333333333', '123e4567-e89b-12d3-a456-426614174003', 'member', TRUE, FALSE, NOW() - INTERVAL '12 hours', NULL),

-- Участники второго группового чата (Secret Project X)
('99999999-9999-9999-9999-999999999999', '44444444-4444-4444-4444-444444444444', '123e4567-e89b-12d3-a456-426614174002', 'admin', FALSE, FALSE, NOW() - INTERVAL '1 day', NULL),

-- Участники личного чата (юзер 1 и юзер 2 - оба админы по бизнес-логике личных чатов)
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa', '55555555-5555-5555-5555-555555555555', '123e4567-e89b-12d3-a456-426614174001', 'admin', FALSE, FALSE, NOW() - INTERVAL '5 hours', NULL),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb', '55555555-5555-5555-5555-555555555555', '123e4567-e89b-12d3-a456-426614174002', 'admin', FALSE, FALSE, NOW() - INTERVAL '5 hours', NULL);

-- 4. Сообщения
INSERT INTO messages (id, chat_id, sender_id, content, message_type, metadata, sent_at, delivered_at, is_read, deleted_at) VALUES
-- Сообщения в группе
('cccccccc-cccc-cccc-cccc-cccccccccccc', '33333333-3333-3333-3333-333333333333', '123e4567-e89b-12d3-a456-426614174001', 'Welcome to the Tandem Developers group!', 'text', NULL, NOW() - INTERVAL '2 days', NOW() - INTERVAL '47 hours', TRUE, NULL),
('dddddddd-dddd-dddd-dddd-dddddddddddd', '33333333-3333-3333-3333-333333333333', '123e4567-e89b-12d3-a456-426614174002', 'Hello everyone! Glad to be here.', 'text', NULL, NOW() - INTERVAL '1 day', NOW() - INTERVAL '23 hours', TRUE, NULL),

-- Сообщения в личном чате
('eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee', '55555555-5555-5555-5555-555555555555', '123e4567-e89b-12d3-a456-426614174001', 'Hey, could you review my latest PR?', 'text', NULL, NOW() - INTERVAL '4 hours', NOW() - INTERVAL '3 hours', TRUE, NULL),
('ffffffff-ffff-ffff-ffff-ffffffffffff', '55555555-5555-5555-5555-555555555555', '123e4567-e89b-12d3-a456-426614174002', 'Sure, can you drop the code snippet here?', 'text', NULL, NOW() - INTERVAL '3 hours', NOW() - INTERVAL '2 hours', TRUE, NULL),
('00000000-0000-0000-0000-000000000000', '55555555-5555-5555-5555-555555555555', '123e4567-e89b-12d3-a456-426614174001', 'public class App { ... }', 'code', '{"language": "java"}', NOW(), NULL, FALSE, NULL);

-- 5. Запросы на вступление в группу
INSERT INTO group_requests (id, group_id, user_id, requested_by, status, message, created_at, expires_at, reviewed_at, reviewed_by) VALUES
-- Пользователь 3 просит вступить в приватную группу (Secret Project X)
('10101010-1010-1010-1010-101010101010', '22222222-2222-2222-2222-222222222222', '123e4567-e89b-12d3-a456-426614174003', '123e4567-e89b-12d3-a456-426614174003', 'pending', 'I was told I need access to this project.', NOW(), NOW() + INTERVAL '7 days', NULL, NULL);