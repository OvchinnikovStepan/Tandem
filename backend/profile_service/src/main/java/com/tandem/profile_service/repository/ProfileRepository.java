package com.tandem.profile_service.repository;

import com.tandem.profile_service.model.Profile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class ProfileRepository extends GeneralRepository<Profile> {

    @Autowired
    public ProfileRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, "profiles", "id", "user_id");
    }

    @Override
    protected RowMapper<Profile> getRowMapper() {
        return (rs, rowNum) -> Profile.builder()
                .id(UUID.fromString(rs.getString("id")))
                .userId(UUID.fromString(rs.getString("user_id")))
                .name(rs.getString("name"))
                .surname(rs.getString("surname"))
                .phoneNumber(rs.getString("phone_number"))
                .email(rs.getString("email"))
                .status(rs.getString("status"))
                .birthday(rs.getDate("birthday") != null ? rs.getDate("birthday").toLocalDate() : null)
                .city(rs.getString("city"))
                .placeOfWork(rs.getString("place_of_work"))
                .jobTitle(rs.getString("job_title"))
                .personalInterests(rs.getString("personal_interests"))
                .onboardingCompleted(rs.getBoolean("onboarding_completed"))
                .onboardingCompletedAt(rs.getTimestamp("onboarding_completed_at") != null ?
                        rs.getTimestamp("onboarding_completed_at").toLocalDateTime() : null)
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }

    public Profile save(Profile profile) {
        if (profile.getId() == null) {
            profile.setId(UUID.randomUUID());
            profile.setCreatedAt(LocalDateTime.now());
            profile.setUpdatedAt(LocalDateTime.now());

            String sql = """
                INSERT INTO profiles (id, user_id, name, surname, phone_number, email, status, 
                                    birthday, city, place_of_work, job_title, personal_interests, 
                                    onboarding_completed, onboarding_completed_at, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

            jdbcTemplate.update(sql,
                    profile.getId(), profile.getUserId(), profile.getName(), profile.getSurname(),
                    profile.getPhoneNumber(), profile.getEmail(), profile.getStatus(),
                    profile.getBirthday(), profile.getCity(), profile.getPlaceOfWork(),
                    profile.getJobTitle(), profile.getPersonalInterests(),
                    profile.isOnboardingCompleted(), profile.getOnboardingCompletedAt(),
                    profile.getCreatedAt(), profile.getUpdatedAt());
        } else {
            profile.setUpdatedAt(LocalDateTime.now());

            String sql = """
                UPDATE profiles SET 
                    name = ?, surname = ?, phone_number = ?, email = ?, status = ?,
                    birthday = ?, city = ?, place_of_work = ?, job_title = ?, personal_interests = ?,
                    onboarding_completed = ?, onboarding_completed_at = ?, updated_at = ?
                WHERE id = ?
                """;

            jdbcTemplate.update(sql,
                    profile.getName(), profile.getSurname(), profile.getPhoneNumber(),
                    profile.getEmail(), profile.getStatus(), profile.getBirthday(),
                    profile.getCity(), profile.getPlaceOfWork(), profile.getJobTitle(),
                    profile.getPersonalInterests(), profile.isOnboardingCompleted(),
                    profile.getOnboardingCompletedAt(), profile.getUpdatedAt(), profile.getId());
        }

        return profile;
    }

    public Profile createNewUserProfile(UUID userId, String phoneNumber, String email) {
        UUID profileId = UUID.randomUUID();
        LocalDateTime now = LocalDateTime.now();

        String sql = """
            INSERT INTO profiles (
                id, 
                user_id, 
                phone_number,
                email,
                onboarding_completed, 
                onboarding_completed_at, 
                created_at, 
                updated_at
            ) VALUES (?, ?, ?, ?, ?, ?, ?, ?)
            """;

        jdbcTemplate.update(sql,
                profileId,
                userId,
                phoneNumber,
                email,
                false,
                null,
                now,
                now);

        return Profile.builder()
                .id(profileId)
                .userId(userId)
                .phoneNumber(phoneNumber)
                .email(email)
                .onboardingCompleted(false)
                .onboardingCompletedAt(null)
                .createdAt(now)
                .updatedAt(now)
                .build();
    }
}