package com.tandem.interest_service.dao.queries;

/**
 * SQL запросы для работы с таблицей group_tags
 */
public final class GroupTagQueries {

    private GroupTagQueries() {}

    public static final String INSERT =
            "INSERT INTO group_tags (id, group_id, tag_id, created_at) " +
                    "VALUES (:id, :groupId, :tagId, :createdAt)";

    public static final String DELETE =
            "DELETE FROM group_tags WHERE id = :id";

    public static final String SELECT_BY_GROUP_ID =
            "SELECT id, group_id, tag_id, created_at " +
                    "FROM group_tags WHERE group_id = :groupId";

    public static final String SELECT_BY_GROUP_AND_TAG =
            "SELECT id, group_id, tag_id, created_at " +
                    "FROM group_tags WHERE group_id = :groupId AND tag_id = :tagId";

    public static final String COUNT_GROUP_TAGS =
            "SELECT COUNT(*) FROM group_tags WHERE group_id = :groupId";


    public static final String FIND_GROUPS_BY_MIN_COMMON_TAGS_WITH_USER =
            "SELECT gt.group_id, COUNT(*) as common_count " +
                    "FROM user_interests ui " +
                    "JOIN group_tags gt ON ui.tag_id = gt.tag_id " +
                    "WHERE ui.user_id = :userId " +
                    "GROUP BY gt.group_id " +
                    "HAVING COUNT(*) >= :minMatchCount " +
                    "ORDER BY common_count DESC";

    public static final String SELECT_COMMON_TAG_IDS_USER_GROUP =
            "SELECT ui.tag_id " +
                    "FROM user_interests ui " +
                    "JOIN group_tags gt ON ui.tag_id = gt.tag_id " +
                    "WHERE ui.user_id = :userId AND gt.group_id = :groupId";
}