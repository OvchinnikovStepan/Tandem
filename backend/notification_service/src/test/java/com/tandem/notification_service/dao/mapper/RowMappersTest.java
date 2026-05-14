package com.tandem.notification_service.dao.mapper;

import com.tandem.notification_service.dao.model.NotificationEntity;
import com.tandem.notification_service.dao.model.NotificationPreferenceEntity;
import com.tandem.notification_service.dao.model.NotificationTemplateEntity;
import org.junit.jupiter.api.Test;

import java.sql.Array;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RowMappersTest {

    @Test
    void notificationRowMapperMapsAllFields() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ResultSet rs = mock(ResultSet.class);
        when(rs.getObject("id", UUID.class)).thenReturn(id);
        when(rs.getObject("user_id", UUID.class)).thenReturn(userId);
        when(rs.getString("type")).thenReturn("message.received");
        when(rs.getString("channel")).thenReturn("in-app");
        when(rs.getString("title")).thenReturn("Title");
        when(rs.getString("body")).thenReturn("Body");
        when(rs.getString("data")).thenReturn("{}");
        when(rs.getBoolean("read")).thenReturn(true);
        when(rs.getTimestamp("read_at")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, 1, 1, 10, 0)));
        when(rs.getTimestamp("delivered_at")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, 1, 1, 9, 0)));
        when(rs.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, 1, 1, 8, 0)));
        when(rs.getTimestamp("expires_at")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, 1, 2, 8, 0)));
        when(rs.getTimestamp("archived_at")).thenReturn(null);

        NotificationEntity entity = new NotificationDaoRowMapper().rowMapper.mapRow(rs, 0);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(entity.getRead()).isTrue();
        assertThat(entity.getChannel()).isEqualTo("in-app");
        assertThat(entity.getCreatedAt()).isEqualTo(LocalDateTime.of(2026, 1, 1, 8, 0));
    }

    @Test
    void preferenceRowMapperMapsAllFields() throws Exception {
        UUID id = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        ResultSet rs = mock(ResultSet.class);
        when(rs.getObject("id", UUID.class)).thenReturn(id);
        when(rs.getObject("user_id", UUID.class)).thenReturn(userId);
        when(rs.getBoolean("push_enabled")).thenReturn(true);
        when(rs.getBoolean("email_enabled")).thenReturn(false);
        when(rs.getBoolean("sms_enabled")).thenReturn(false);
        when(rs.getBoolean("in_app_enabled")).thenReturn(true);
        when(rs.getString("preferences")).thenReturn("{}");
        when(rs.getBoolean("quiet_hours_enabled")).thenReturn(true);
        when(rs.getTime("quiet_hours_start")).thenReturn(java.sql.Time.valueOf(LocalTime.of(23, 0)));
        when(rs.getTime("quiet_hours_end")).thenReturn(java.sql.Time.valueOf(LocalTime.of(6, 0)));
        when(rs.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, 1, 1, 8, 0)));
        when(rs.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, 1, 1, 9, 0)));

        NotificationPreferenceEntity entity = new NotificationPreferenceDaoRowMapper().rowMapper.mapRow(rs, 0);

        assertThat(entity.getUserId()).isEqualTo(userId);
        assertThat(entity.getPushEnabled()).isTrue();
        assertThat(entity.getQuietHoursStart()).isEqualTo(LocalTime.of(23, 0));
        assertThat(entity.getQuietHoursEnd()).isEqualTo(LocalTime.of(6, 0));
    }

    @Test
    void templateRowMapperMapsChannelsArray() throws Exception {
        UUID id = UUID.randomUUID();
        ResultSet rs = mock(ResultSet.class);
        Array channels = mock(Array.class);
        when(channels.getArray()).thenReturn(new String[]{"in-app", "push"});
        when(rs.getObject("id", UUID.class)).thenReturn(id);
        when(rs.getString("type")).thenReturn("message.received");
        when(rs.getString("title_template")).thenReturn("Hi");
        when(rs.getString("body_template")).thenReturn("Body");
        when(rs.getString("variables")).thenReturn("[]");
        when(rs.getArray("channels")).thenReturn(channels);
        when(rs.getTimestamp("created_at")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, 1, 1, 8, 0)));
        when(rs.getTimestamp("updated_at")).thenReturn(Timestamp.valueOf(LocalDateTime.of(2026, 1, 1, 9, 0)));

        NotificationTemplateEntity entity = new NotificationTemplateDaoRowMapper().rowMapper.mapRow(rs, 0);

        assertThat(entity.getId()).isEqualTo(id);
        assertThat(entity.getChannels()).containsExactly("in-app", "push");
    }
}
