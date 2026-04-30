INSERT INTO notification_templates (
    id,
    type,
    title_template,
    body_template,
    variables,
    channels,
    created_at,
    updated_at
) VALUES
(
    '11111111-1111-1111-1111-111111111111',
    'user.registered',
    'Welcome to Tandem',
    'Your account {{userId}} has been created successfully.',
    '{"required":["userId"]}'::jsonb,
    ARRAY['in-app','email'],
    now(),
    now()
),
(
    '22222222-2222-2222-2222-222222222222',
    'message.received',
    'New Message',
    'You have received a new message in chat {{chatId}}.',
    '{"required":["chatId"]}'::jsonb,
    ARRAY['in-app','push'],
    now(),
    now()
),
(
    '33333333-3333-3333-3333-333333333333',
    'group.message.sent',
    'New Group Message',
    'There is a new message in group {{groupId}}.',
    '{"required":["groupId"]}'::jsonb,
    ARRAY['in-app','push'],
    now(),
    now()
),
(
    '44444444-4444-4444-4444-444444444444',
    'group.member.banned',
    'Security Alert',
    'A moderation action was applied in your group activity.',
    '{"required":[]}'::jsonb,
    ARRAY['in-app','email'],
    now(),
    now()
)
ON CONFLICT (type) DO NOTHING;
