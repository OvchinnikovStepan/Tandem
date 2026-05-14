package com.tandem.notification_service.dao.queries;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DaoQueriesSmokeTest {

    @Test
    void sqlFragmentsAreDefined() {
        assertThat(NotificationQueries.INSERT).containsIgnoringCase("INSERT INTO notifications");
        assertThat(NotificationQueries.SELECT_BY_USER_ID).containsIgnoringCase("FROM notifications");

        assertThat(DeliveryStatusQueries.INSERT).containsIgnoringCase("INSERT INTO delivery_status");

        assertThat(NotificationPreferenceQueries.SELECT_BY_USER_ID).containsIgnoringCase("notification_preferences");

        assertThat(NotificationTemplateQueries.SELECT_BY_TYPE).containsIgnoringCase("notification_templates");
    }
}
