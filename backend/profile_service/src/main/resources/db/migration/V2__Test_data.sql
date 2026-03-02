-- Профиль
INSERT INTO profiles (
    id,
    user_id,
    created_at,
    updated_at
) VALUES (
    '11111111-1111-1111-1111-111111111111',
    '123e4567-e89b-12d3-a456-426614174002',
    NOW(),
    NOW()
);

-- Опрос
INSERT INTO polls (
    id,
    name,
    version,
    is_active,
    created_at,
    updated_at
) VALUES (
    '11111111-1111-1111-1111-111111111111',
    'onboarding',
    1,
    TRUE,
    NOW(),
    NOW()
);

-- 1. Name
INSERT INTO questions (
    id,
    poll_id,
    question_order,
    question_type,
    label,
    description,
    is_required,
    validation_rules,
    options,
    profile_field,
    created_at
) VALUES (
    '22222222-2222-2222-2222-222222222221',
    '11111111-1111-1111-1111-111111111111',
    1,
    'text',
    'name',
    'Your first name',
    TRUE,
    '{"minLength": 2, "maxLength": 50, "pattern": "^[A-Za-z ]+$"}'::jsonb,
    NULL,
    'name',
    NOW()
);

-- 2. Surname
INSERT INTO questions (
    id,
    poll_id,
    question_order,
    question_type,
    label,
    description,
    is_required,
    validation_rules,
    options,
    profile_field,
    created_at
) VALUES (
    '22222222-2222-2222-2222-222222222222',
    '11111111-1111-1111-1111-111111111111',
    2,
    'text',
    'surname',
    'Your surname',
    TRUE,
    '{"minLength": 2, "maxLength": 50, "pattern": "^[A-Za-z ]+$"}'::jsonb,
    NULL,
    'surname',
    NOW()
);

-- 3. Bio
INSERT INTO questions (
    id,
    poll_id,
    question_order,
    question_type,
    label,
    description,
    is_required,
    validation_rules,
    options,
    profile_field,
    created_at
) VALUES (
    '22222222-2222-2222-2222-222222222223',
    '11111111-1111-1111-1111-111111111111',
    3,
    'textarea',
    'bio',
    'Tell us about yourself...',
    FALSE,
    '{"maxLength": 500}'::jsonb,
    NULL,
    'personalInterests',
    NOW()
);

-- 4. Personal Interests
INSERT INTO questions (
    id,
    poll_id,
    question_order,
    question_type,
    label,
    description,
    is_required,
    validation_rules,
    options,
    profile_field,
    created_at
) VALUES (
    '22222222-2222-2222-2222-222222222224',
    '11111111-1111-1111-1111-111111111111',
    4,
    'multiselect',
    'personal_interests',
    'Select your personal interests',
    TRUE,
    '{"minSelected": 1}'::jsonb,
    '["otaku","doing sports","gaming","reading","music"]'::jsonb,
    'personalInterests',
    NOW()
);

-- 5. City
INSERT INTO questions (
    id,
    poll_id,
    question_order,
    question_type,
    label,
    description,
    is_required,
    validation_rules,
    options,
    profile_field,
    created_at
) VALUES (
    '22222222-2222-2222-2222-222222222225',
    '11111111-1111-1111-1111-111111111111',
    5,
    'text',
    'city',
    'Your city',
    FALSE,
    '{"minLength": 2, "maxLength": 100}'::jsonb,
    NULL,
    'city',
    NOW()
);

-- 6. Place of Work
INSERT INTO questions (
    id,
    poll_id,
    question_order,
    question_type,
    label,
    description,
    is_required,
    validation_rules,
    options,
    profile_field,
    created_at
) VALUES (
    '22222222-2222-2222-2222-222222222226',
    '11111111-1111-1111-1111-111111111111',
    6,
    'text',
    'place_of_work',
    'Your place of work',
    FALSE,
    '{"minLength": 2, "maxLength": 200}'::jsonb,
    NULL,
    'placeOfWork',
    NOW()
);

-- 7. Job Title
INSERT INTO questions (
    id,
    poll_id,
    question_order,
    question_type,
    label,
    description,
    is_required,
    validation_rules,
    options,
    profile_field,
    created_at
) VALUES (
    '22222222-2222-2222-2222-222222222227',
    '11111111-1111-1111-1111-111111111111',
    7,
    'text',
    'job_title',
    'Your job title',
    FALSE,
    '{"minLength": 2, "maxLength": 100}'::jsonb,
    NULL,
    'jobTitle',
    NOW()
);
