package com.tandem.interest_service.dao.queries;

/**
 * SQL запросы для работы с таблицей tags
 */
public final class TagQueries {

    private TagQueries() {}

    public static final String INSERT =
            "INSERT INTO tags (id, name) " +
                    "VALUES (:id, :name)";

    public static final String UPDATE =
            "UPDATE tags SET name = :name, image_url = :imageUrl " +
                    "WHERE id = :id";

    public static final String DELETE =
            "DELETE FROM tags WHERE id = :id";

    public static final String SELECT_BY_ID =
            "SELECT id, name, image_url FROM tags WHERE id = :id";

    public static final String SELECT_ALL =
            "SELECT id, name, image_url FROM tags";

    public static final String SELECT_DEFAULT =
            "SELECT id, name, image_url FROM tags WHERE image_url IS NOT NULL";

    public static final String SELECT_BY_NAME =
            "SELECT id, name, image_url FROM tags WHERE name = :name";

    public static final String SEARCH_BY_PREFIX =
            "SELECT id, name, image_url, 0 as usage_count FROM tags " +
                    "WHERE LOWER(name) LIKE LOWER(:prefix) " +
                    "ORDER BY name " +
                    "LIMIT :limit";
}