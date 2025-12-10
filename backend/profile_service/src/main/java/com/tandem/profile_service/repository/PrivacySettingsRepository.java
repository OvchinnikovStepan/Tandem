package com.tandem.profile_service.repository;

import com.tandem.profile_service.model.PrivacySettings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public class PrivacySettingsRepository extends GeneralRepository<PrivacySettings> {

    @Autowired
    public PrivacySettingsRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate, "privacy_settings", "id", "user_id");
    }

    @Override
    protected RowMapper<PrivacySettings> getRowMapper() {
        return (rs, rowNum) -> PrivacySettings.builder()
                .id(UUID.fromString(rs.getString("id")))
                .userId(UUID.fromString(rs.getString("user_id")))
                .showPhoneNumber(rs.getBoolean("show_phone_number"))
                .showEmail(rs.getBoolean("show_email"))
                .showCity(rs.getBoolean("show_city"))
                .showPlaceOfWork(rs.getBoolean("show_place_of_work"))
                .showJobTitle(rs.getBoolean("show_job_title"))
                .showBirthday(rs.getBoolean("show_birthday"))
                .showPersonalInterests(rs.getBoolean("show_personal_interests"))
                .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                .updatedAt(rs.getTimestamp("updated_at").toLocalDateTime())
                .build();
    }


    public PrivacySettings save(PrivacySettings settings) {
        if (settings.getId() == null) {
            settings.setId(UUID.randomUUID());
            settings.setCreatedAt(LocalDateTime.now());
            settings.setUpdatedAt(LocalDateTime.now());

            String sql = """
                INSERT INTO privacy_settings (id, user_id, show_phone_number, show_email, show_city, 
                                            show_place_of_work, show_job_title, show_birthday, 
                                            show_personal_interests, created_at, updated_at)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

            jdbcTemplate.update(sql,
                    settings.getId(), settings.getUserId(), settings.isShowPhoneNumber(),
                    settings.isShowEmail(), settings.isShowCity(), settings.isShowPlaceOfWork(),
                    settings.isShowJobTitle(), settings.isShowBirthday(),
                    settings.isShowPersonalInterests(),
                    settings.getCreatedAt(), settings.getUpdatedAt());
        } else {
            settings.setUpdatedAt(LocalDateTime.now());

            String sql = """
                UPDATE privacy_settings SET 
                    show_phone_number = ?, show_email = ?, show_city = ?, 
                    show_place_of_work = ?, show_job_title = ?, show_birthday = ?, 
                    show_personal_interests = ?, updated_at = ?
                WHERE id = ?
                """;

            jdbcTemplate.update(sql,
                    settings.isShowPhoneNumber(), settings.isShowEmail(), settings.isShowCity(),
                    settings.isShowPlaceOfWork(), settings.isShowJobTitle(), settings.isShowBirthday(),
                    settings.isShowPersonalInterests(), settings.getUpdatedAt(), settings.getId());
        }

        return settings;
    }


    public PrivacySettings saveDefaultSettings(UUID userId) {
        PrivacySettings defaultSettings = PrivacySettings.builder()
                .userId(userId)
                .showPhoneNumber(false)
                .showEmail(false)
                .showCity(true)
                .showPlaceOfWork(true)
                .showJobTitle(true)
                .showBirthday(false)
                .showPersonalInterests(true)
                .build();

        return save(defaultSettings);
    }
}