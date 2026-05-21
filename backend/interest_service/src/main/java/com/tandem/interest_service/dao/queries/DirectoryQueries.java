package com.tandem.interest_service.dao.queries;

/**
 * SQL для локального каталога пользователей и групп (поиск по подстроке имени).
 */
public final class DirectoryQueries {

    private DirectoryQueries() {}

    public static final String UPSERT_USER =
            "INSERT INTO user_directory (user_id, display_name, updated_at) "
                    + "VALUES (:userId, :displayName, NOW()) "
                    + "ON CONFLICT (user_id) DO UPDATE SET "
                    + "display_name = EXCLUDED.display_name, updated_at = NOW()";

    public static final String UPSERT_GROUP =
            "INSERT INTO group_directory (group_id, name, updated_at) "
                    + "VALUES (:groupId, :name, NOW()) "
                    + "ON CONFLICT (group_id) DO UPDATE SET "
                    + "name = EXCLUDED.name, updated_at = NOW()";

    public static final String SEARCH_USERS_BY_FRAGMENT =
            "SELECT user_id, display_name FROM user_directory "
                    + "WHERE POSITION(LOWER(:fragment) IN LOWER(display_name)) > 0 "
                    + "ORDER BY display_name "
                    + "LIMIT :limit";

    public static final String SEARCH_GROUPS_BY_FRAGMENT =
            "SELECT group_id, name FROM group_directory "
                    + "WHERE POSITION(LOWER(:fragment) IN LOWER(name)) > 0 "
                    + "ORDER BY name "
                    + "LIMIT :limit";
}
