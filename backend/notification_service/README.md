# Notification Service (`notification_service`)

Notification Service для платформы Tandem. Сервис принимает события из Kafka, применяет пользовательские настройки уведомлений, рендерит шаблоны, сохраняет уведомления и ведет трекинг доставки по каналам.

README рассчитан на разработчика, который впервые открывает проект.

---

## 1) Что делает сервис

Основные обязанности:

1. Хранение уведомлений пользователя (`notifications`)
2. Управление настройками (`notification_preferences`)
3. Рендеринг шаблонов (`notification_templates`)
4. Трекинг статусов доставки (`delivery_status`)
5. Обработка событий из Kafka
6. Очередь доставки через Redis, retry/backoff и DLQ
7. API для клиента: список, unread, mark read, delete, preferences

---

## 2) Архитектура слоев

Сервис реализован слоями:

- `api` — REST-контроллеры, DTO, мапперы, обработка ошибок
- `service` — бизнес-логика (event flow, delivery flow, template flow)
- `dal` — orchestration поверх DAO + кэш + транзакционные сценарии
- `dao` — SQL доступ к PostgreSQL (jdbc template, row mappers, queries)
- `cache` — Redis-кэш для unread count и preferences
- `integration` — Kafka listeners
- `configuration` — Kafka/Spring конфигурация

Это позволяет изолировать контракт API, бизнес-логику и работу с хранилищами.

---

## 3) Структура ключевых компонентов

### API слой

- `api/NotificationApi` — контракт endpoint’ов
- `api/impl/NotificationApiImpl` — реализация endpoint’ов
- `api/support/RequestUserProvider` — извлечение user id из:
  - `Authorization: Bearer ...` (UUID или JWT `sub`)
  - fallback `X-User-Id`
- `api/error/ApiExceptionHandler` — базовые 4xx ошибки

### Service слой

- `service/impl/NotificationEventServiceImpl`
  - принимает доменное событие из listener
  - определяет target user/type/title/body/data
  - загружает preferences
  - рендерит template (если есть)
  - применяет quiet hours и category/channel правила
  - создает notification запись
  - отправляет задачу в delivery pipeline

- `service/impl/NotificationDeliveryServiceImpl`
  - ставит delivery tasks в Redis sorted set (delayed queue)
  - фоновым worker’ом (`@Scheduled`) обрабатывает задачи батчами
  - ведет retry с exponential backoff
  - отправляет исчерпанные задачи в DLQ
  - пишет статусы в `delivery_status` (`pending/sent/failed`)

- `service/impl/NotificationTemplateServiceImpl`
  - ищет шаблон по типу
  - рендерит `{{variable}}` плейсхолдеры
  - возвращает финальные title/body/channels

- `service/impl/NotificationServiceImpl`
  - фасад для REST use-cases (list/unread/read/read-all/delete/preferences)

### DAL слой

- `dal/impl/NotificationDalImpl`
  - агрегирует DAO операции
  - поддерживает Redis cache (unread/preferences)
  - создает дефолтные preferences при первом обращении
  - инвалидация кэша при изменениях

### DAO слой

- `NotificationDao` — уведомления
- `NotificationPreferenceDao` — настройки
- `NotificationTemplateDao` — шаблоны
- `DeliveryStatusDao` — статусы доставки

SQL вынесен в `dao/queries/*`, row mapping — в `dao/mapper/*`.

### Integration слой

- `integration/NotificationEventListener`
  - `@KafkaListener` на topics
  - передает событие в `NotificationEventService`
  - manual ack

### Cache слой

- `cache/NotificationCacheService`
  - unread count cache
  - preferences cache
  - TTL + graceful fallback в БД

---

## 4) API endpoint’ы

- `GET /api/notifications?page=1&limit=20&type=&read=`
- `GET /api/notifications/unread`
- `PUT /api/notifications/{notificationId}/read`
- `PUT /api/notifications/read-all`
- `DELETE /api/notifications/{notificationId}`
- `GET /api/notifications/preferences`
- `PUT /api/notifications/preferences`

> Все endpoint’ы работают для текущего пользователя, определенного `RequestUserProvider`.

---

## 5) Kafka события

Listener подписан на:

- `user.registered`
- `profile.onboarding.completed`
- `message.sent`
- `chat.created`
- `group.created`
- `member.joined`
- `member.banned`
- `group.message.sent`

Минимальный expected payload:

- для user-событий: поле `userId` или соответствующий alias (`recipientId`, `memberId`, `bannedUserId`, ...)
- для `chat.created`: массив участников (`participants`/`participantIds`/`userIds`/`memberIds`)

---

## 6) Delivery pipeline (внутренний)

Ключи Redis:

- queue: `notifications:delivery:zset`
- dlq: `notifications:delivery:dlq`
- unread cache: `notifications:unread:{userId}`
- preferences cache: `notifications:preferences:{userId}`

Алгоритм:

1. При создании уведомления формируются delivery tasks по каналам
2. Каждая задача кладется в sorted set с `availableAtEpochMs`
3. Scheduler выбирает задачи `score <= now` батчами
4. При успехе пишется `delivery_status=sent`
5. При ошибке:
   - до `max-retries`: задача возвращается в queue с backoff
   - после `max-retries`: задача уходит в DLQ и статус `failed`

---

## 7) База данных и миграции

Миграции:

- `V1__create_notifications_table.sql`
  - `notifications`
  - `notification_preferences`
  - `notification_templates`
  - `delivery_status`
  - индексы
- `V2__seed_notification_templates.sql`
  - seed базовых шаблонов

---

## 8) Конфигурация (`application.properties`)

Ключевые группы:

- `spring.datasource.*` — PostgreSQL
- `spring.flyway.*` — миграции
- `spring.kafka.*` + `tandem.kafka.topic.*` — Kafka consumer
- `spring.data.redis.*` — Redis
- `notification.delivery.*` — параметры воркера доставки:
  - `initial-delay-ms`
  - `poll-interval-ms`
  - `batch-size`
  - `max-retries`
  - `base-backoff-ms`

---

## 9) Логирование

Используется `logback-spring.xml` + `logstash-logback-encoder`:

- JSON-формат в stdout
- custom field `service_name=notification-service`
- удобно для ELK/Logstash/Kibana

---

## 10) Локальный запуск (без Docker)

Требования:

- JDK 17
- Maven 3.9+
- PostgreSQL
- Redis
- Kafka (если нужно тестировать event flow)

Команды:

```bash
mvn clean package
java -jar target/notification_service-0.0.1-SNAPSHOT.jar
```

Проверка:

```bash
mvn test
```

---

## 11) Запуск через Docker Compose

В `docker-compose.yml` поднимаются:

- `db` (PostgreSQL)
- `redis`
- `kafka` (single-node KRaft)
- `app` (notification service)

Запуск:

```bash
docker compose up --build
```

Остановка:

```bash
docker compose down
```

---

## 12) Docker файлы (валидность)

### `Dockerfile`

- ожидает собранный jar: `target/notification_service-0.0.1-SNAPSHOT.jar`
- экспонирует `8083`
- entrypoint: `java -jar app.jar`

### `docker-compose.yml`

- корректно связывает app c `db`, `redis`, `kafka`
- выставляет необходимые `SPRING_*` env
- использует локальную bridge network (`tandem-network`)
- healthcheck для PostgreSQL присутствует

---

## 13) Где начинать разработку

Если добавляете новый event type:

1. Добавить topic в `application.properties`
2. Добавить topic в `NotificationEventListener`
3. Обновить маппинг в `NotificationEventServiceImpl`:
   - resolve user
   - resolve type/title/body
   - category/critical rules при необходимости
4. Добавить шаблон в `notification_templates` (миграцией)
5. Проверить delivery/status/cache поведение

Если меняете API контракт:

1. `api/model/*`
2. `api/NotificationApi`
3. `api/mapper/NotificationApiMapper`
4. `service` + `dal` + `dao`

---

## 14) Текущие ограничения (осознанно)

- Внешние провайдеры доставки (`FCM/APNS/SMTP/SMS`) пока заглушки.
- JWT проверяется облегченно (извлечение `sub`), без удаленной валидации через Auth Service/JWKS.
- Delivery worker in-process (не вынесен в отдельный worker deployment).

Это сделано осознанно, чтобы сохранить независимость сервиса до полноценной интеграции с остальными сервисами.
