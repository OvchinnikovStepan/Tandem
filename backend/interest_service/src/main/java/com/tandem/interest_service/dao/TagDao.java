package com.tandem.interest_service.dao;

import com.tandem.interest_service.dao.model.TagEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TagDao {
    void insert(TagEntity entity);
    void update(TagEntity entity);
    void delete(UUID id);
    Optional<TagEntity> findById(UUID id);
    List<TagEntity> findAll();
    List<TagEntity> findDefault();
    Optional<TagEntity> findByName(String name);
    List<TagEntity> searchByNamePrefix(String prefix, int limit);
}