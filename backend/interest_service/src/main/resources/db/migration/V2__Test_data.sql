INSERT INTO tags (id, name, image_url) VALUES
('11111111-1111-1111-1111-111111111111', 'gaming', 'https://example.com/images/gaming.jpg'),
('22222222-2222-2222-2222-222222222222', 'reading', 'https://example.com/images/reading.jpg'),
('33333333-3333-3333-3333-333333333333', 'music', NULL);

-- Интересы пользователя
INSERT INTO user_interests (id, user_id, tag_id, created_at) VALUES
('11111111-1111-1111-1111-111111111111', '123e4567-e89b-12d3-a456-426614174001', '11111111-1111-1111-1111-111111111111', NOW()),
('22222222-2222-2222-2222-222222222222', '123e4567-e89b-12d3-a456-426614174002', '11111111-1111-1111-1111-111111111111', NOW()),
('33333333-3333-3333-3333-333333333333', '123e4567-e89b-12d3-a456-426614174002', '22222222-2222-2222-2222-222222222222', NOW());

INSERT INTO group_tags (id, group_id, tag_id, created_at) VALUES
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa1',
 '88888888-8888-8888-8888-888888888801',
 '11111111-1111-1111-1111-111111111111', -- gaming
 NOW()),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2',
 '88888888-8888-8888-8888-888888888801',
 '22222222-2222-2222-2222-222222222222', -- reading
 NOW()),
('aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3',
 '88888888-8888-8888-8888-888888888801',
 '33333333-3333-3333-3333-333333333333', -- music
 NOW());

INSERT INTO group_tags (id, group_id, tag_id, created_at) VALUES
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb1',
 '88888888-8888-8888-8888-888888888802',
 '11111111-1111-1111-1111-111111111111', -- gaming
 NOW()),
('bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2',
 '88888888-8888-8888-8888-888888888802',
 '22222222-2222-2222-2222-222222222222', -- reading
 NOW());

INSERT INTO group_tags (id, group_id, tag_id, created_at) VALUES
('cccccccc-cccc-cccc-cccc-ccccccccccc1',
 '88888888-8888-8888-8888-888888888803',
 '11111111-1111-1111-1111-111111111111', -- gaming
 NOW());