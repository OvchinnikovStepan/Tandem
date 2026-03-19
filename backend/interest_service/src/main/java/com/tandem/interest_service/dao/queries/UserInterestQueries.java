package com.tandem.interest_service.dao.queries;

/**
 * SQL запросы для работы с таблицей  user_interests
 */
public final class UserInterestQueries {

    private UserInterestQueries() {}

    public static final String INSERT =
            "INSERT INTO user_interests (id, user_id, tag_id, created_at) " +
                    "VALUES (:id, :userId, :tagId, :createdAt)";

    public static final String DELETE =
            "DELETE FROM user_interests WHERE id = :id";

    public static final String SELECT_BY_USER_ID =
            "SELECT id, user_id, tag_id, created_at " +
                    "FROM user_interests WHERE user_id = :userId";

    public static final String SELECT_BY_USER_AND_TAG =
            "SELECT id, user_id, tag_id, created_at " +
                    "FROM user_interests WHERE user_id = :userId AND tag_id = :tagId";

    public static final String FIND_USERS_BY_MIN_COMMON_TAGS =
            "SELECT ui2.user_id, COUNT(*) as common_count " +
                    "FROM user_interests ui1 " +
                    "JOIN user_interests ui2 ON ui1.tag_id = ui2.tag_id " +
                    "WHERE ui1.user_id = :currentUserId " +
                    "AND ui2.user_id != :currentUserId " +
                    "GROUP BY ui2.user_id " +
                    "HAVING COUNT(*) >= :minMatchCount " +
                    "ORDER BY common_count DESC";

    public static final String COUNT_USER_INTERESTS =
            "SELECT COUNT(*) FROM user_interests WHERE user_id = :userId";

    public static final String SELECT_COMMON_TAG_IDS =
            "SELECT ui1.tag_id " +
                    "FROM user_interests ui1 " +
                    "JOIN user_interests ui2 ON ui1.tag_id = ui2.tag_id " +
                    "WHERE ui1.user_id = :userId1 " +
                    "AND ui2.user_id = :userId2";
}
