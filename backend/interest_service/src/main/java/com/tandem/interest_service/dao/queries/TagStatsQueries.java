package com.tandem.interest_service.dao.queries;

/**
 * SQL запросы для работы с с materialized view tag_usage_stats
 */
public class TagStatsQueries {

    private TagStatsQueries() {}

    public static final String SELECT_STATS_BY_TAG_ID =
            "SELECT tag_id, tag_name, usage_count " +
                    "FROM tag_usage_stats " +
                    "WHERE tag_id = :tagId";


    public static final String SELECT_TOP_STATS =
            "SELECT tag_id, tag_name, usage_count " +
                    "FROM tag_usage_stats " +
                    "ORDER BY usage_count DESC " +
                    "LIMIT :limit";


    public static final String SELECT_USAGE_COUNT_BY_TAG_ID =
            "SELECT usage_count " +
                    "FROM tag_usage_stats " +
                    "WHERE tag_id = :tagId";


    public static final String REFRESH_STATS_VIEW =
            "REFRESH MATERIALIZED VIEW CONCURRENTLY tag_usage_stats";
}
