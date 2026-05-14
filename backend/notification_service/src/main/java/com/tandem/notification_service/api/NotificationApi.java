package com.tandem.notification_service.api;

import com.tandem.notification_service.api.model.request.NotificationPreferencesUpdateRequestJson;
import com.tandem.notification_service.api.model.response.*;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("/api/notifications")
public interface NotificationApi {

    @Operation(summary = "Получить уведомления текущего пользователя")
    @GetMapping
    ResponseEntity<NotificationListResponseJson> getNotifications(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "20") Integer limit,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean read
    );

    @Operation(summary = "Получить непрочитанные уведомления")
    @GetMapping("/unread")
    ResponseEntity<NotificationUnreadResponseJson> getUnreadNotifications();

    @Operation(summary = "Отметить уведомление как прочитанное")
    @PutMapping("/{notificationId}/read")
    ResponseEntity<NotificationReadResponseJson> markAsRead(
            @PathVariable UUID notificationId
    );

    @Operation(summary = "Отметить все уведомления как прочитанные")
    @PutMapping("/read-all")
    ResponseEntity<NotificationMarkAllReadResponseJson> markAllAsRead();

    @Operation(summary = "Удалить уведомление (soft delete)")
    @DeleteMapping("/{notificationId}")
    ResponseEntity<NotificationDeleteResponseJson> deleteNotification(
            @PathVariable UUID notificationId
    );

    @Operation(summary = "Получить настройки уведомлений")
    @GetMapping("/preferences")
    ResponseEntity<NotificationPreferencesResponseJson> getPreferences();

    @Operation(summary = "Обновить настройки уведомлений")
    @PutMapping("/preferences")
    ResponseEntity<NotificationPreferencesUpdateResponseJson> updatePreferences(
            @Valid @RequestBody NotificationPreferencesUpdateRequestJson request
    );
}
