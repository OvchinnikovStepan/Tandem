package com.tandem.chat_service.dao.queries;

/**
 * SQL запросы для работы с таблицей groups
 */
public final class GroupQueries {

    private GroupQueries() {}

    public static final String INSERT =
            "INSERT INTO groups (id, name, description, avatar_url, creator_id, visibility) " +
                    "VALUES (:id, :name, :description, :avatarUrl, :creatorId, :visibility)";

    public static final String UPDATE =
            "UPDATE groups SET name = :name, description = :description, " +
                    "avatar_url = :avatarUrl, visibility = :visibility WHERE id = :id";

    public static final String DELETE =
            "DELETE FROM groups WHERE id = :id";

    public static final String SELECT_BY_ID =
            "SELECT id, name, description, avatar_url, creator_id, visibility FROM groups WHERE id = :id";

    public static final String SELECT_BY_CREATOR_ID =
            "SELECT id, name, description, avatar_url, creator_id, visibility FROM groups WHERE creator_id = :creatorId";

    public static final String SELECT_BY_VISIBILITY =
            "SELECT id, name, description, avatar_url, creator_id, visibility FROM groups WHERE visibility = :visibility";

    public static final String SELECT_BY_NAME =
            "SELECT id, name, description, avatar_url, creator_id, visibility FROM groups WHERE name = :name";

    public static final String SEARCH_BY_PREFIX =
            "SELECT id, name, description, avatar_url, creator_id, visibility FROM groups " +
                    "WHERE LOWER(name) LIKE LOWER(:prefix) " +
                    "ORDER BY name LIMIT :limit";
}